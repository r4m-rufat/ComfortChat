package com.example.comfortchat.Fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comfortchat.Activities.Contacts.ContactsActivity;
import com.example.comfortchat.Activities.Status.AddStatusActivity;
import com.example.comfortchat.Activities.Status.ViewStatusActivity;
import com.example.comfortchat.Adapters.RecyclerContactsAdapter;
import com.example.comfortchat.Adapters.RecyclerStatusOfUsers;
import com.example.comfortchat.BuildConfig;
import com.example.comfortchat.Models.Users;
import com.example.comfortchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusFragment extends Fragment {

    private CircleImageView statusImage;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private RelativeLayout myStatus;
    private String imageUrl;
    private ImageButton addImageButton;
    private CircleImageView circleStatusImage;
    public static final int REQUEST_READ_CONTACTS = 79;
    private ArrayList mobile_array;
    private List<Users> usersList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerStatusOfUsers recyclerStatusOfUsers;
    public static Uri imageUri = null;
    private static final String TAG = "StatusFragment";

    public StatusFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        statusImage = view.findViewById(R.id.circleProfileImage);
        myStatus = view.findViewById(R.id.relStatus);
        addImageButton = view.findViewById(R.id.addstoryButton);
        circleStatusImage = view.findViewById(R.id.circleProfileImage);
        recyclerView = view.findViewById(R.id.recycler_forUsersStatus);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        setStatusImage();
        relStatusClicked();
        if (firebaseUser != null) {

            getContactFromMobile();
            getContactListSetStatus();

        }

        return view;
    }

    private void setStatusImage() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();


        firestore.collection("Status").document(firebaseUser.getUid()).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        imageUrl = documentSnapshot.getString("statusImage");

                        if (imageUrl == null || imageUrl.equals("") || imageUrl.equals("null")) {

                            firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(
                                    new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            imageUrl = documentSnapshot.getString("profileImage");

                                            if (imageUrl != "") {
                                                Glide.with(getContext()).load(imageUrl).into(statusImage);
                                            } else {
                                                statusImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.person));
                                            }

                                        }
                                    }
                            );

                        } else {
                            Glide.with(getContext()).load(imageUrl).into(statusImage);
                            addImageButton.setVisibility(View.INVISIBLE);
                            circleStatusImage.setBorderWidth(3);
                        }

                    }
                }
        );
    }

    private void relStatusClicked() {

        firestore.collection("Status").document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String imageUrl = documentSnapshot.getString("statusImage");

                        if (imageUrl == null || imageUrl.equals("") || imageUrl.equals("null")) {

                            myStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    checkCameraPermission();
                                }
                            });

                        } else {

                            myStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(getActivity(), ViewStatusActivity.class));
                                }
                            });

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 101);

        } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);

        } else {

            openCamera();

        }

    }

    private void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        try {

            File file = File.createTempFile("IMG_" + timeStamp, ".jpg", getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, 444);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 444
                && resultCode == getActivity().RESULT_OK) {

            if (imageUri != null) {

                startActivity(new Intent(getActivity(), AddStatusActivity.class).putExtra("fromFragment", "OK"));

            }

        }

    }

    private void getContactFromMobile() {

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobile_array = getAllPhoneNumbers();
        } else {
            requestPermission();
        }

    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobile_array = getAllPhoneNumbers();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private ArrayList getAllPhoneNumbers() {
        ArrayList<String> contactsList = new ArrayList<>();
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactsList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return contactsList;
    }

    private void getContactListSetStatus() {

        firestore.collection("Users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        usersList.clear();

                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {

                            String userID = queryDocumentSnapshot.getString("userID");
                            String profileName = queryDocumentSnapshot.getString("username");
                            String profileImage = queryDocumentSnapshot.getString("profileImage");
                            String bio = queryDocumentSnapshot.getString("bio");
                            String phone = queryDocumentSnapshot.getString("userPhone");
                            String birthday = queryDocumentSnapshot.getString("dateOfBirth");

                            Users users = new Users();
                            users.setUserID(userID);
                            users.setUsername(profileName);
                            users.setProfileImage(profileImage);
                            users.setDateOfBirth(birthday);
                            users.setUserPhone(phone);
                            users.setBio(bio);

                            if (userID != null && !userID.equals(firebaseUser.getUid())) {
                                if (mobile_array.contains(users.getUserPhone())) {

                                    firestore.collection("Status").document(userID).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                    if (documentSnapshot.getString("statusImage") != "" || documentSnapshot.getString("statusImage") != null) {
                                                        usersList.add(users);
                                                        Log.d(TAG, "onSuccess: Firstly");
                                                    }

                                                        recyclerView.setHasFixedSize(false);
                                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                                        recyclerStatusOfUsers = new RecyclerStatusOfUsers(getContext(), firestore, usersList);
                                                        recyclerView.setAdapter(recyclerStatusOfUsers);
                                                        Log.d(TAG, "onSuccess: Secondly");

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                                }

                            }

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }

        });

    }

}