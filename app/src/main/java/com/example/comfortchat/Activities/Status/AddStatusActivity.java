package com.example.comfortchat.Activities.Status;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comfortchat.Activities.MainActivity;
import com.example.comfortchat.BuildConfig;
import com.example.comfortchat.Fragments.StatusFragment;
import com.example.comfortchat.Models.Status;
import com.example.comfortchat.R;
import com.example.comfortchat.Services.ChatService;
import com.example.comfortchat.Services.FirebaseService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddStatusActivity extends AppCompatActivity {

    private Uri imageUri;
    private ZoomageView zoomageView;
    private FloatingActionButton sendStatusButton;
    private EditText description;
    private static String txtDescription="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_status);

        if (getIntent().getStringExtra("fromFragment")!=null){
            imageUri = StatusFragment.imageUri;
        }else{
            imageUri = MainActivity.imageUri;
        }

        getWidgets();
        getAndSetInfo();
        clickedSendStatusButton();

    }

    private void clickedSendStatusButton() {

        sendStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog =  new ProgressDialog(AddStatusActivity.this);
                progressDialog.setMessage("Uploading...");
                progressDialog.show();

                if (!description.getText().toString().isEmpty()){
                    txtDescription = description.getText().toString();
                }

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("StatusImages/" + System.currentTimeMillis() + "." + getFileExtention(imageUri));
                storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUrl = uriTask.getResult();

                        final String statusUrl = String.valueOf(downloadUrl);

                        Status status = new Status();
                        status.setId(UUID.randomUUID().toString());
                        status.setStatusDate(new ChatService(AddStatusActivity.this).getCurrentDate());
                        status.setUserID(FirebaseAuth.getInstance().getUid());
                        status.setViewCount("0");
                        status.setStatusText(txtDescription);
                        status.setStatusImage(statusUrl);

                        new FirebaseService(AddStatusActivity.this).addStatusToFirebase(status, new FirebaseService.OnAddStatusCallBack() {
                            @Override
                            public void onSuccess() {

                                Toast.makeText(AddStatusActivity.this, "Successfully shared", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddStatusActivity.this, MainActivity.class).putExtra("fromStatus", "StatusOK"));
                                finish();

                            }

                            @Override
                            public void onFailed() {

                                Toast.makeText(AddStatusActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        });

                        progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });

    }

    private String getFileExtention(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void getAndSetInfo(){

        Glide.with(AddStatusActivity.this).load(imageUri).into(zoomageView);

    }

    private void getWidgets(){

        zoomageView = findViewById(R.id.status_image_from_camera);
        sendStatusButton = findViewById(R.id.sendStatusButton);
        description = findViewById(R.id.etxtDescription);

    }

}