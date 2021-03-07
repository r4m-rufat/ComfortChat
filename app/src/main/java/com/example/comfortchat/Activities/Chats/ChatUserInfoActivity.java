package com.example.comfortchat.Activities.Chats;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.comfortchat.R;

public class ChatUserInfoActivity extends AppCompatActivity {

    private ImageView collapseImage;
    private TextView bio, userPhone, userBirthday, callNumber;
    private Toolbar username;


    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_info);
        context = this;

        setupWidgets();
        setItems();

    }

    private void setupWidgets(){

        collapseImage = findViewById(R.id.profileImageForUser);
        bio = findViewById(R.id.profileBioForUser);
        username = findViewById(R.id.profilenameForUser);
        userPhone = findViewById(R.id.profileNumberForUser);
        userBirthday = findViewById(R.id.profileBirthdayForUser);
        callNumber = findViewById(R.id.profilePhoneInTelephone);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setItems(){

        Intent intent =  getIntent();
        String imageUrl = intent.getStringExtra("profileUserImage");
        String user = intent.getStringExtra("profilename");
        String strBio = intent.getStringExtra("profileBio");
        String phone = intent.getStringExtra("phonenumber");
        String birthday = intent.getStringExtra("birthday");

        if (!imageUrl.isEmpty()){

            Glide.with(context).load(imageUrl).into(collapseImage);

        }

        username.setTitle(user);
        bio.setText(strBio);
        userPhone.setText(phone);
        userBirthday.setText(birthday);
        callNumber.setText(phone);

    }

}