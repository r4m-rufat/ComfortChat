package com.example.comfortchat.Activities.MenuItems.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.comfortchat.R;
import com.example.comfortchat.Utils.Common;
import com.jsibbold.zoomage.ZoomageView;

public class ActivityViewProfile extends AppCompatActivity {

    private ZoomageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        profileImage = findViewById(R.id.zoomProfilePhoto);

        profileImage.setImageBitmap(Common.IMAGE_BITMAP);


    }
}