package com.example.comfortchat.Activities.MenuItems;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.comfortchat.Activities.MainActivity;
import com.example.comfortchat.Activities.MenuItems.Profile.ProfileActivity;
import com.example.comfortchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    // widgets
    private TextView profileName;
    private RelativeLayout relProfile;
    private CircleImageView profileImage;
    private ImageView ic_back;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    Context context;

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        context = this;

        getWidgets();
        passProfileActivity();

        if (firebaseUser != null){
            getInfo();
        }

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });

    }

    private void getInfo(){

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username = documentSnapshot.get("username").toString();
                profileName.setText(username);

                String profileImg = documentSnapshot.get("profileImage").toString();

                if (profileImg != ""){
                    Glide.with(context).load(profileImg).into(profileImage);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Username is null or something went wrong");
            }
        });

    }

    private void getWidgets(){
        profileName = findViewById(R.id.txtsettingsProfileName);
        relProfile = findViewById(R.id.rellayout1);
        profileImage = findViewById(R.id.settingsProfileImage);
        ic_back = findViewById(R.id.ic_back_forProfile);
    }

    private void passProfileActivity(){

        relProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ProfileActivity.class));
            }
        });

    }




}