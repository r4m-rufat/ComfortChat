package com.example.comfortchat.Activities.Status;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comfortchat.R;
import com.jsibbold.zoomage.ZoomageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewContactUsersStatusActivity extends AppCompatActivity {

    private TextView username, statusTime, caption;
    private ImageView backButton;
    private CircleImageView profileImage;
    private ZoomageView statusImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact_users_status);

        getWidgets();
        getStatusDataFromDatabase();

    }

    private void getStatusDataFromDatabase(){

        username.setText(getIntent().getStringExtra("statusUsername"));
        statusTime.setText(getIntent().getStringExtra("statusTime"));
        caption.setText(getIntent().getStringExtra("caption"));

        Glide.with(ViewContactUsersStatusActivity.this).load(getIntent().getStringExtra("statusImage")).into(statusImage);
        Glide.with(ViewContactUsersStatusActivity.this).load(getIntent().getStringExtra("statusProfileImage")).into(profileImage);

        if (getIntent().getStringExtra("statusProfileImage")=="null"){

            profileImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.person));

        }else{

            Glide.with(ViewContactUsersStatusActivity.this).load(getIntent().getStringExtra("statusProfileImage")).into(profileImage);

        }

    }

    private void getWidgets() {

        username = findViewById(R.id.usernameForStatusUsers);
        statusImage = findViewById(R.id.statusImage);
        statusTime = findViewById(R.id.txtDate);
        caption = findViewById(R.id.txtCaption);
        profileImage = findViewById(R.id.anotherPersonStatusImage);
        backButton = findViewById(R.id.ic_backForStatusView);

    }
}