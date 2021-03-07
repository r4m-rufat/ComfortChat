package com.example.comfortchat.Services;

import android.content.Context;
import android.net.Uri;
import android.os.storage.StorageManager;

import androidx.annotation.NonNull;

import com.example.comfortchat.Models.Chats;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatService {

    private Context context;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String reciverID;

    public ChatService(Context context) {
        this.context = context;
    }

    public ChatService(Context context, String reciverID) {
        this.context = context;
        this.reciverID = reciverID;
    }

    public void readChat(final OnReadChatInterface onReadChatInterface){

        databaseReference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Chats> chatsList = new ArrayList<>();

                for (DataSnapshot singleSnapshot: snapshot.getChildren()){

                    Chats chats = singleSnapshot.getValue(Chats.class);
                    if((chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(reciverID)) ||
                            chats.getSender().equals(reciverID) && chats.getReceiver().equals(firebaseUser.getUid())){
                        chatsList.add(chats);
                    }

                }
                onReadChatInterface.OnReadChatSuccess(chatsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                onReadChatInterface.OnReadChatFailed();

            }
        });

    }

    public void sendTextMessage(String textMessage){

        try{

            Chats chats = new Chats(
                    firebaseUser.getUid(),
                    reciverID,
                    "TEXT",
                    getCurrentDate(),
                    textMessage,
                    "");


            databaseReference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            // add to ChatsList
            DatabaseReference chatReference1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(reciverID);
            chatReference1.child("chatid").setValue(reciverID);


            DatabaseReference chatReference2 = FirebaseDatabase.getInstance().getReference("ChatList").child(reciverID).child(firebaseUser.getUid());
            chatReference2.child("chatid").setValue(firebaseUser.getUid());


        }catch (Exception e){

            e.printStackTrace();

        }

    }

    public void sendImage(String imageURl){

        try{

            Chats chats = new Chats(
                    firebaseUser.getUid(),
                    reciverID,
                    "Image",
                    getCurrentDate(),
                    "",
                    imageURl);


            databaseReference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            // add to ChatsList
            DatabaseReference chatReference1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(reciverID);
            chatReference1.child("chatid").setValue(reciverID);


            DatabaseReference chatReference2 = FirebaseDatabase.getInstance().getReference("ChatList").child(reciverID).child(firebaseUser.getUid());
            chatReference2.child("chatid").setValue(firebaseUser.getUid());


        }catch (Exception e){

            e.printStackTrace();

        }

    }

    public String getCurrentDate() {

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String today = simpleDateFormat.format(date);

        Date current = Calendar.getInstance().getTime();
        SimpleDateFormat currentFormat = new SimpleDateFormat("HH:mm");
        String currentTime = currentFormat.format(current);

        return today + ", " + currentTime;

    }

    public void sendVoiceMessage(String voice_path){

        Uri uriAudio = Uri.fromFile(new File(voice_path));
        final StorageReference audioReference = FirebaseStorage.getInstance().getReference().child("Chats/Voice/" + System.currentTimeMillis());

        audioReference.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot audioSnapshot) {

                Task<Uri> uriTask = audioSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri downloadUrl = uriTask.getResult();
                String voiceUrl = String.valueOf(downloadUrl);


                Chats chats = new Chats(
                        firebaseUser.getUid(),
                        reciverID,
                        "VOICE",
                        getCurrentDate(),
                        "",
                        voiceUrl);

                databaseReference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

                // Add to Chat List
                DatabaseReference chatReference1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(reciverID);
                chatReference1.child("chatid").setValue(reciverID);


                DatabaseReference chatReference2 = FirebaseDatabase.getInstance().getReference("ChatList").child(reciverID).child(firebaseUser.getUid());
                chatReference2.child("chatid").setValue(firebaseUser.getUid());


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

}
