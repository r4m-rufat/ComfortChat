package com.example.comfortchat.Activities.Status;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.comfortchat.Activities.MainActivity;
import com.example.comfortchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jsibbold.zoomage.ZoomageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ViewStatusActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private ZoomageView viewStatus;
    private TextView description, statusTime;
    private RelativeLayout descLayout;
    private ImageView ic_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);

        getWidgets();
        setFirebaseStatusInfo();
        ic_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFirebaseStatusInfoUpdated();
            }
        });

    }

    private void setFirebaseStatusInfoUpdated(){

        HashMap<String, Object> deleteStatus = new HashMap<>();
        deleteStatus.put("statusImage", "null");

        firestore.collection("Status").document(firebaseUser.getUid()).update(deleteStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("fromStatus", "OK"));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void setFirebaseStatusInfo() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Status").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                statusTime.setText(setupTimeToLocal(documentSnapshot.getString("statusDate")));

                if (documentSnapshot.getString("statusText") != ""){
                    description.setText(documentSnapshot.getString("statusText"));
                }else{
                    descLayout.setVisibility(View.INVISIBLE);
                }

                Glide.with(ViewStatusActivity.this).load(documentSnapshot.getString("statusImage")).into(viewStatus);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void getWidgets(){

        viewStatus = findViewById(R.id.statusImage);
        description = findViewById(R.id.txtCaption);
        statusTime = findViewById(R.id.txtDate);
        descLayout = findViewById(R.id.descLayout);
        ic_delete = findViewById(R.id.ic_delete);

    }

    private String setupTimeToLocal(String time){

        int hour = Integer.parseInt(time.substring(12,14));
        int minute = Integer.parseInt(time.substring(15));

        Date current = Calendar.getInstance().getTime();
        SimpleDateFormat currentFormat = new SimpleDateFormat("HH:mm");
        String currentTime = currentFormat.format(current);

        int currentHour = Integer.parseInt(currentTime.substring(0,2));
        int currentMinute = Integer.parseInt(currentTime.substring(3));

        long sumStatus = hour * 60 + minute;
        long sumCurrent = currentHour * 60 + currentMinute;

        int differentiateThem = (int) (sumStatus - sumCurrent);

        if (differentiateThem<0){
            differentiateThem*=-1;
        }

        String endTime = null;

        if (differentiateThem <=60 && differentiateThem > 1){

            endTime = differentiateThem + " minutes ago";

        }else if (differentiateThem <= 1){

            endTime = "a few seconds ago";

        }else if (differentiateThem > 60 && differentiateThem <=1440 ){

            endTime = "at " + time.substring(12);

        }

        return endTime;

    }


}