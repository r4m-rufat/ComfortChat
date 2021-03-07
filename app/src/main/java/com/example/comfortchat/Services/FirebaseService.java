package com.example.comfortchat.Services;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.example.comfortchat.Models.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class FirebaseService {

    private Context context;

    public FirebaseService(Context context) {
        this.context = context;
    }

    public void uploadImageToFirebase(Uri uri, OnCallBack onCallBack){

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ChatImages/" + System.currentTimeMillis() + "." + getFileExtention(uri));
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri downloadUrl = uriTask.getResult();

                final String download_url = String.valueOf(downloadUrl);

                onCallBack.onUploadSuccess(download_url);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                onCallBack.onUploadFailed(e);

            }
        });

    }

    private String getFileExtention(Uri uri){

        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    public interface OnCallBack{

        void onUploadSuccess(String imageUrl);
        void onUploadFailed(Exception e);
    }

    public interface OnAddStatusCallBack{

        void onSuccess();
        void onFailed();

    }

    public void addStatusToFirebase(Status status, OnAddStatusCallBack onAddStatusCallBack){

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Status").
                document(status.getUserID())
                .set(status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        onAddStatusCallBack.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {

                         onAddStatusCallBack.onFailed();

                    }
                });

    }

}
