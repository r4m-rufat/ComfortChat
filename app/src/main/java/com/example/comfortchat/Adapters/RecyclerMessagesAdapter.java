package com.example.comfortchat.Adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comfortchat.InternalStorage.PreferenceManager;
import com.example.comfortchat.Models.Chats;
import com.example.comfortchat.R;
import com.example.comfortchat.Setups.VoiceService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerMessagesAdapter extends RecyclerView.Adapter<RecyclerMessagesAdapter.ViewHolder> {

    private Context context;
    private List<Chats> chatsList;
    private FirebaseUser firebaseUser;
    private static final int MOD_TYPE_LEFT = 1;
    private static final int MOD_TYPE_RIGHT = 2;
    private PreferenceManager preferenceManager;
    private ImageButton imageButtonPlay;
    private VoiceService voiceService;

    public void setChatsList(List<Chats> chatsList){
        this.chatsList = chatsList;
        notifyDataSetChanged();
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull ViewHolder holder) {
        return super.onFailedToRecycleView(holder);
    }

    public RecyclerMessagesAdapter(Context context, List<Chats> chatsList, PreferenceManager preferenceManager) {
        this.context = context;
        this.chatsList = chatsList;
        this.preferenceManager = preferenceManager;
        this.voiceService = new VoiceService(context);
    }

    @NonNull
    @Override
    public RecyclerMessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MOD_TYPE_LEFT){

            View viewLeft = LayoutInflater.from(context).inflate(R.layout.layout_message_for_left, parent, false);

            return new ViewHolder(viewLeft);

        }else {

            View viewRight = LayoutInflater.from(context).inflate(R.layout.layout_message_for_right, parent, false);

            return new ViewHolder(viewRight);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerMessagesAdapter.ViewHolder holder, int position) {

        switch (chatsList.get(position).getType()){

            case "Image":
                holder.relativeLayoutImage.setVisibility(View.VISIBLE);
                holder.relativeLayoutText.setVisibility(View.GONE);
                holder.layoutVoice.setVisibility(View.GONE);
                Glide.with(context).load(chatsList.get(position).getImageUrl()).into(holder.messageImage);
                break;

            case "TEXT":
                holder.relativeLayoutImage.setVisibility(View.GONE);
                holder.relativeLayoutText.setVisibility(View.VISIBLE);
                holder.layoutVoice.setVisibility(View.GONE);
                break;

            case "VOICE":
                holder.relativeLayoutImage.setVisibility(View.GONE);
                holder.relativeLayoutText.setVisibility(View.GONE);
                holder.layoutVoice.setVisibility(View.VISIBLE);

                holder.playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (imageButtonPlay != null){
                            imageButtonPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                        }

                        holder.playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));

                        voiceService.playAudioFromFirebaseUrl(chatsList.get(position).getImageUrl(), new VoiceService.onPlayCallBack() {
                            @Override
                            public void onFinished() {

                                holder.playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));

                            }
                        });

                        imageButtonPlay = holder.playButton;

                    }
                });

                break;



        }

        holder.txtMessage.setText(chatsList.get(position).getMessage());
        holder.messageTime.setText(chatsList.get(position).getDateTime().substring(12));

        if (!preferenceManager.getString("profile_image_for_message").isEmpty()) {
            Glide.with(context).load(preferenceManager.getString("profile_image_for_message")).into(holder.profileMessageImage);
        }


    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtMessage, messageTime;
        CircleImageView profileMessageImage;
        RelativeLayout relativeLayoutText, relativeLayoutImage, layoutVoice;
        ImageView messageImage;
        ImageButton playButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileMessageImage = itemView.findViewById(R.id.circleImageForMessage);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            messageTime = itemView.findViewById(R.id.messageTime);
            relativeLayoutText = itemView.findViewById(R.id.rellayout1);
            relativeLayoutImage = itemView.findViewById(R.id.rellayout2);
            messageImage = itemView.findViewById(R.id.imageForMessage);
            layoutVoice = itemView.findViewById(R.id.layout_voice);
            playButton = itemView.findViewById(R.id.ic_play);
        }
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chatsList.get(position).getSender().equals(firebaseUser.getUid())){

            return MOD_TYPE_RIGHT;

        }else {

            return MOD_TYPE_LEFT;

        }

    }
}
