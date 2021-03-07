package com.example.comfortchat.Activities.Chats;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.example.comfortchat.Activities.Dialogs.DialogReviewSendImage;
import com.example.comfortchat.Adapters.RecyclerMessagesAdapter;
import com.example.comfortchat.InternalStorage.PreferenceManager;
import com.example.comfortchat.Models.Chats;
import com.example.comfortchat.R;
import com.example.comfortchat.Services.ChatService;
import com.example.comfortchat.Services.FirebaseService;
import com.example.comfortchat.Services.OnReadChatInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final int REQUEST_CORD_PERMISSION = 332;
    private CircleImageView circleProfileImage;
    private TextView profileName, profileBio;
    private EditText etxtMessage;
    private RecyclerView messagesRecyler;
    private RecordButton sendVoiceButton;
    private RecordView recordView;
    private CardView cardViewForFiles;
    private ImageView ic_files, ic_gallery, ic_emoji, ic_camera;
    FloatingActionButton floatingActionButton;
    private ChatService chatService;

    private static final String TAG = "ChatActivity";
    private RecyclerMessagesAdapter messagesAdapter;
    private List<Chats> list;

    private String reciverID, profileImage, username, bio,
    phone, birthday;
    private Context context;
    private PreferenceManager preferenceManager;

    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;
    private boolean show = true;
    private MediaRecorder mediaRecorder;
    private String audio_path;
    private String rTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initialize();
        setupSendVoiceButton();
        sendButtonClicked();
        passChatUserInfoActivity();
        fileButtonClicked();
        galleryButtonClicked();

        readChats();


    }

    private void initialize(){

        context = this;
        preferenceManager = new PreferenceManager(context);

        Intent intent = getIntent();

        reciverID = intent.getStringExtra("userID");
        username = intent.getStringExtra("username");
        profileImage = intent.getStringExtra("profileImage");
        bio = intent.getStringExtra("bio");
        birthday = intent.getStringExtra("dateOfBirth");
        phone = intent.getStringExtra("userPhone");

        preferenceManager.putString("profile_image_for_message", profileImage);
        preferenceManager.putString("profile_bio_for_message", bio);

        // create the object of ChatService class
        chatService = new ChatService(this, reciverID);

        getWidgets();

        list = new ArrayList<>();
        messagesRecyler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        messagesAdapter = new RecyclerMessagesAdapter(context, list, preferenceManager);
        messagesRecyler.setAdapter(messagesAdapter);

        if (reciverID != null){

            profileName.setText(username);
            profileBio.setText(bio);
            if (!profileImage.isEmpty()) {

                Glide.with(ChatActivity.this).load(profileImage).into(circleProfileImage);

            }

        }

        etxtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(etxtMessage.getText().toString().trim())){

                    floatingActionButton.setVisibility(View.INVISIBLE);
                    sendVoiceButton.setVisibility(View.VISIBLE);

                }else{

                    floatingActionButton.setVisibility(View.VISIBLE);
                    sendVoiceButton.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setupSendVoiceButton(){

        sendVoiceButton.setRecordView(recordView);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                etxtMessage.setVisibility(View.INVISIBLE);
                ic_emoji.setVisibility(View.INVISIBLE);
                ic_camera.setVisibility(View.INVISIBLE);
                ic_files.setVisibility(View.INVISIBLE);

                if(!checkPermissionFromDevice()){

                    etxtMessage.setVisibility(View.INVISIBLE);
                    ic_emoji.setVisibility(View.INVISIBLE);
                    ic_camera.setVisibility(View.INVISIBLE);
                    ic_files.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "Ready", Toast.LENGTH_SHORT).show();

                    startRecord();
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    if (vibrator != null){
                        vibrator.vibrate(100);
                    }

                }else{
                    requestPermisson();
                }

            }

            @Override
            public void onCancel() {

                try{

                    mediaRecorder.reset();

                }catch (Exception e){

                    e.printStackTrace();

                }

            }

            @Override
            public void onFinish(long recordTime) {

                etxtMessage.setVisibility(View.VISIBLE);
                ic_emoji.setVisibility(View.VISIBLE);
                ic_camera.setVisibility(View.VISIBLE);
                ic_files.setVisibility(View.VISIBLE);

                try {

                    rTime = getPersonTimeText(recordTime);
                    stopRecorder();

                }catch (Exception e){

                    e.printStackTrace();

                }

            }

            @Override
            public void onLessThanSecond() {

                etxtMessage.setVisibility(View.VISIBLE);
                ic_emoji.setVisibility(View.VISIBLE);
                ic_camera.setVisibility(View.VISIBLE);
                ic_files.setVisibility(View.VISIBLE);

            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {

                etxtMessage.setVisibility(View.VISIBLE);
                ic_emoji.setVisibility(View.VISIBLE);
                ic_camera.setVisibility(View.VISIBLE);
                ic_files.setVisibility(View.VISIBLE);

            }
        });

    }


     private void getWidgets(){

        circleProfileImage = findViewById(R.id.circleImageForChats);
        profileName = findViewById(R.id.profilenameforChat);
        profileBio = findViewById(R.id.txtBioForChats);
        etxtMessage = findViewById(R.id.etxtMessage);
        sendVoiceButton = findViewById(R.id.record_button);
        messagesRecyler = findViewById(R.id.messsagesRecycler);
        cardViewForFiles = findViewById(R.id.showCardViewForFiles);
        ic_files = findViewById(R.id.ic_attachment);
        ic_gallery = findViewById(R.id.ic_gallery);
        ic_camera = findViewById(R.id.ic_camera);
        ic_emoji = findViewById(R.id.ic_emoji);
        floatingActionButton = findViewById(R.id.floatingSendMessage);
        recordView = findViewById(R.id.record_view);

     }

     private void sendButtonClicked(){

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etxtMessage.getText().toString().trim())){

                    chatService.sendTextMessage(etxtMessage.getText().toString().trim());

                    etxtMessage.setText("");

                }
            }
        });

     }

     private void fileButtonClicked(){

        ic_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show){

                    cardViewForFiles.setVisibility(View.VISIBLE);
                    show = false;

                }else{

                    cardViewForFiles.setVisibility(View.GONE);
                    show = true;

                }
            }
        });

     }

     private void passChatUserInfoActivity(){

        circleProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ChatUserInfoActivity.class)
                .putExtra("profileUserImage", profileImage)
                .putExtra("profilename", username)
                .putExtra("profileBio", bio)
                .putExtra("phonenumber", phone)
                .putExtra("birthday", birthday));
            }
        });

     }

     private void readChats(){

        chatService.readChat(new OnReadChatInterface() {
            @Override
            public void OnReadChatSuccess(List<Chats> chats) {

                messagesAdapter.setChatsList(chats);
            }

            @Override
            public void OnReadChatFailed() {
                
                
            }
        });

     }

     private void galleryButtonClicked(){

        ic_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

     }

    private void openGallery(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            imageUri = data.getData();

            // uploadImageToFirebase();
            try{

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                reviewImage(bitmap);

            }catch (Exception e){

                e.printStackTrace();

            }

        }

    }

    private void reviewImage(Bitmap bitmap){

        new DialogReviewSendImage(context, bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void onClickedSendButton() {
                // to upload image to firebase storage
                if (imageUri != null){
                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Sending image...");
                    progressDialog.show();

                    show = false;
                    cardViewForFiles.setVisibility(View.GONE);

                    new FirebaseService(context).uploadImageToFirebase(imageUri, new FirebaseService.OnCallBack() {
                        @Override
                        public void onUploadSuccess(String imageUrl) {
                            chatService.sendImage(imageUrl);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onUploadFailed(Exception e) {
                            progressDialog.dismiss();
                        }
                    });

                }
            }
        });

    }

    private boolean checkPermissionFromDevice() {

        int write_external_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage == PackageManager.PERMISSION_DENIED || record_audio_result == PackageManager.PERMISSION_DENIED;

    }

    private void requestPermisson() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_CORD_PERMISSION);

    }

    private void startRecord(){

        setupMediaRecorder();

        try {

            mediaRecorder.prepare();
            mediaRecorder.start();

        }catch (Exception e){

            e.printStackTrace();
            Toast.makeText(context, "Recording Error!!!", Toast.LENGTH_SHORT).show();

        }

    }

    private void setupMediaRecorder(){

        String path_save  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "audio_record.m4a";
        audio_path = path_save;

        mediaRecorder = new MediaRecorder();

        try {

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(audio_path);

        }catch (Exception e){

            Log.d(TAG, "setupMediaRecorder: " + e.getMessage());

        }

    }

    private String getPersonTimeText(long milliseconds){

        return String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

    }

    private void stopRecorder() {

        try {

            if (mediaRecorder != null){

                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;

                // send voice message
                chatService.sendVoiceMessage(audio_path);

            }else{

                Toast.makeText(context, "Null", Toast.LENGTH_SHORT).show();

            }

        }catch (Exception e){

            e.printStackTrace();
            Toast.makeText(context, "Stop Recording Error:  " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

}
