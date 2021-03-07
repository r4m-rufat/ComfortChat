package com.example.comfortchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.comfortchat.Activities.Status.AddStatusActivity;
import com.example.comfortchat.Adapters.SectionsPagerAdapter;
import com.example.comfortchat.Activities.Contacts.ContactsActivity;
import com.example.comfortchat.BuildConfig;
import com.example.comfortchat.Fragments.CallsFragment;
import com.example.comfortchat.Fragments.ChatsFragment;
import com.example.comfortchat.Fragments.StatusFragment;
import com.example.comfortchat.Activities.MenuItems.SettingsActivity;
import com.example.comfortchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private FloatingActionButton FAButton;
    private ImageButton editButton;
    private Context context;
    public static Uri imageUri = null;
    private FirebaseUser firebaseUser;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        setupWidgets();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setSupportActionBar(toolbar);
        setupViewPagerForFAB();
    }

    private void setupWidgets(){
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar);
        FAButton = findViewById(R.id.floating_action);
        editButton = findViewById(R.id.edit_status);
    }

    private void setupViewPager(ViewPager viewPager){

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        sectionsPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        sectionsPagerAdapter.addFragment(new StatusFragment(), "Status");
        sectionsPagerAdapter.addFragment(new CallsFragment(), "Calls");

        viewPager.setAdapter(sectionsPagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        
        switch (item.getItemId()){
            case R.id.search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                startActivity(new Intent(context, SettingsActivity.class));
                break;

            case R.id.share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;

            case R.id.about:
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                break;
        }
        
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeFAButton(final int index){

        if (index == 0 || index == 2){
            editButton.setVisibility(View.INVISIBLE);
        }

        FAButton.hide();
        editButton.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (index){
                    case 0:
                        FAButton.setImageDrawable(getDrawable(R.drawable.ic_chat));
                        FAButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(context, ContactsActivity.class));
                            }
                        });
                        break;
                    case 1:
                        FAButton.setImageDrawable(getDrawable(R.drawable.ic_camera));
                        editButton.setVisibility(View.VISIBLE);
                        FAButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkCameraPermission();
                            }
                        });
                        break;
                    case 2:
                        FAButton.setImageDrawable(getDrawable(R.drawable.ic_white_call));
                        break;
                }
                FAButton.show();
            }
        }, 400);

    }

    private void setupViewPagerForFAB(){

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageSelected(int position) {

                changeFAButton(position);

            }
            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });

    }

    private void checkCameraPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);

        }else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);

        }else {

            openCamera();

        }

    }

    private void openCamera(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        try {

            File file = File.createTempFile("IMG_" + timeStamp, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, 440);

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 440
                && resultCode == RESULT_OK){

            if (imageUri!=null){

                startActivity(new Intent(context, AddStatusActivity.class));

            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getStringExtra("fromStatus")!=null){
            viewPager.setCurrentItem(1);
        }
    }

}