package com.example.comfortchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comfortchat.Activities.Chats.ChatActivity;
import com.example.comfortchat.Models.ChatList;
import com.example.comfortchat.R;
import com.example.comfortchat.Utils.ProfileViewDialog;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerChatsAdapter extends RecyclerView.Adapter<RecyclerChatsAdapter.ViewHolder> {

    private Context context;
    private List<ChatList> chatList;


    public RecyclerChatsAdapter(Context context, List<ChatList> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public RecyclerChatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_for_chats, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerChatsAdapter.ViewHolder holder, final int position) {

        holder.profileName.setText(chatList.get(position).getUsername());
        holder.lastMessage.setText(chatList.get(position).getMessages());
        if (chatList.get(position).getDate().equals("Online")){
            holder.lastSeen.setText(chatList.get(position).getDate());
            holder.lastSeen.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.lastSeen.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }else{
            holder.lastSeen.setText("Last seen: " + chatList.get(position).getDate());
        }

        if (chatList.get(position).getUrlProfile() == "" || chatList.get(position).getUrlProfile().equals("null")){
            holder.circleProfileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.person));
        }else{
            Glide.with(context).load(chatList.get(position).getUrlProfile()).into(holder.circleProfileImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userID", chatList.get(position).getUserId())
                        .putExtra("username", chatList.get(position).getUsername())
                        .putExtra("profileImage", chatList.get(position).getUrlProfile())
                        );
            }
        });

        holder.circleProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ProfileViewDialog(context, chatList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleProfileImage;
        TextView profileName, lastMessage, lastSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleProfileImage = itemView.findViewById(R.id.circleProfileImage);
            profileName = itemView.findViewById(R.id.profilenameforChat);
            lastMessage = itemView.findViewById(R.id.lastMessageForChats);
            lastSeen = itemView.findViewById(R.id.lastseen);

        }
    }
}
