package com.example.comfortchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comfortchat.Activities.Status.ViewContactUsersStatusActivity;
import com.example.comfortchat.Models.Users;
import com.example.comfortchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerStatusOfUsers extends RecyclerView.Adapter<RecyclerStatusOfUsers.ViewHolder> {

    private Context context;
    private FirebaseFirestore firestore;
    private List<Users> users;

    public RecyclerStatusOfUsers(Context context, FirebaseFirestore firestore, List<Users> users) {
        this.context = context;
        this.firestore = firestore;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerStatusOfUsers.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_for_status_recyclerview, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerStatusOfUsers.ViewHolder holder, int position) {

        final String[] username = new String[1];
        final String[] profileUrl = new String[1];

        firestore.collection("Status")
                .document(users.get(position)
                        .getUserID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String imageUrl = documentSnapshot.getString("statusImage");
                String statusTime = documentSnapshot.getString("statusDate");
                String caption = documentSnapshot.getString("statusText");

                holder.statusTime.setText(setupTimeToLocal(statusTime));
                Glide.with(context).load(imageUrl).into(holder.userImage);

                firestore.collection("Users")
                        .document(users.get(position).getUserID())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                username[0] = documentSnapshot.getString("username");
                                profileUrl[0] = documentSnapshot.getString("profileImage");

                                holder.username.setText(username[0]);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(context, "Something went wrong. Maybe your internet connection is weak!", Toast.LENGTH_SHORT).show();

                            }
                        });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        context.startActivity(new Intent(context, ViewContactUsersStatusActivity.class)
                                .putExtra("statusImage", imageUrl)
                                .putExtra("statusTime", setupTimeToLocal(statusTime))
                                .putExtra("statusUsername", username[0])
                                .putExtra("statusProfileImage", profileUrl[0])
                                .putExtra("caption", caption));

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(context, "Something went wrong. Maybe your internet connection is weak!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, statusTime;
        private CircleImageView userImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameForStatus);
            statusTime = itemView.findViewById(R.id.timeForStatus);
            userImage = itemView.findViewById(R.id.circleStatusImage);

        }
    }

    public String setupTimeToLocal(String time){

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
