package com.example.comfortchat.Activities.Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.comfortchat.Activities.MainActivity;
import com.example.comfortchat.Models.Users;
import com.example.comfortchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserInfoActivity extends AppCompatActivity {

    private EditText username;
    private ImageView profileImage;
    private Button nextButton;
    private ProgressDialog progressDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fortest);
        context = this;

        setupWidgets();
        initButtonClicked();

    }

    private void setupWidgets(){

        username = findViewById(R.id.etxtProfileName);
        nextButton = findViewById(R.id.nextButton);
        progressDialog = new ProgressDialog(context);

    }

    private void initButtonClicked(){

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(username.getText().toString())){
                    username.setError("Enter your name !");
                }else {
                    userInfoUpdate();
                }
            }
        });

    }

    private void userInfoUpdate(){

        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        FirebaseFirestore firestore =  FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){

            Users users = new Users(user.getUid(),
                    username.getText().toString(),
                    user.getPhoneNumber(),
                    "null",
                    "",
                    "",
                    "",
                    "",
                    "I am using ComfortChat",
                    "online");

            firestore.collection("Users").document(user.getUid()).set(users)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            progressDialog.dismiss();
                            Toast.makeText(UserInfoActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(UserInfoActivity.this, "ERROR", Toast.LENGTH_SHORT).show();

                }
            });
        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "firstly, you must login", Toast.LENGTH_SHORT).show();
        }

    }

}