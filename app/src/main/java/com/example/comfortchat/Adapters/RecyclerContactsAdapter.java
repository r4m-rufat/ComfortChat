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
import com.example.comfortchat.Activities.Chats.ChatActivity;
import com.example.comfortchat.Models.Users;
import com.example.comfortchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerContactsAdapter extends RecyclerView.Adapter<RecyclerContactsAdapter.ViewHolder> {

    private Context context;
    private List<Users> usersList;

    public RecyclerContactsAdapter(Context context, List<Users> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public RecyclerContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_contact_recycler_view_items, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerContactsAdapter.ViewHolder holder, int position) {

        final Users users = usersList.get(position);

        holder.profileName.setText(users.getUsername());
        holder.bio.setText(users.getUserPhone());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatActivity.class)
                .putExtra("userID", users.getUserID())
                .putExtra("username", users.getUsername())
                .putExtra("profileImage", users.getProfileImage())
                .putExtra("bio", users.getBio())
                .putExtra("userPhone", users.getUserPhone())
                .putExtra("dateOfBirth", users.getDateOfBirth()));
            }
        });

        if (!users.getProfileImage().isEmpty()) {
            Glide.with(context).load(users.getProfileImage()).into(holder.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView profileName, bio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.circleImageForContacts);
            profileName = itemView.findViewById(R.id.profilenameforContact);
            bio = itemView.findViewById(R.id.txtBio);

        }

    }
}
