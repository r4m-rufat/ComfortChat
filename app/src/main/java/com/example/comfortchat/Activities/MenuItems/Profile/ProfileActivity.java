package com.example.comfortchat.Activities.MenuItems.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comfortchat.BuildConfig;
import com.example.comfortchat.R;
import com.example.comfortchat.Activities.SplashScreen;
import com.example.comfortchat.Utils.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    // widgets
    private TextView profilename, phone, txtBio;
    private FloatingActionButton addProfilephoto;
    private BottomSheetDialog bottomSheetDialog, bottomSheetDialogForUsername, bottomSheetDialogForBio;
    private CircleImageView galleryImage, cameraImage, profileImage;
    private EditText eChangeUsername, eChangeBio;
    private Button confirmUsername, confirmBio, signOut;
    private ImageView ic_change_username, ic_change_bio, ic_back;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;

    private static final String TAG = "ProfileActivity";
    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        context = this;
        progressDialog = new ProgressDialog(context);

        getWidgets();

        if (firebaseUser != null) {
            getInfo();
        }

        initFloatingActionButton();
        updatedProfileItems();
        viewProfileImage();
        signOut();

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, Settings.class));
                finish();
            }
        });

    }


    private void getInfo() {

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String username = documentSnapshot.get("username").toString();
                String phoneNumber = documentSnapshot.get("userPhone").toString();
                String profileImg = documentSnapshot.get("profileImage").toString();
                String bio = documentSnapshot.get("bio").toString();

                if (profileImg != "") {
                    Glide.with(context).load(profileImg).into(profileImage);
                }

                profilename.setText(username);
                phone.setText(phoneNumber);
                txtBio.setText(bio);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Username is null or something went wrong");
            }
        });

    }

    private void getWidgets() {
        profilename = findViewById(R.id.txtUsername);
        phone = findViewById(R.id.txtPhone);
        addProfilephoto = findViewById(R.id.floating_action_profileAdd);
        profileImage = findViewById(R.id.circleDefaultImage);
        ic_change_username = findViewById(R.id.ic_createForUsername);
        signOut = findViewById(R.id.signOut);
        ic_change_bio = findViewById(R.id.ic_createForBio);
        txtBio = findViewById(R.id.txtBio);
        ic_back = findViewById(R.id.ic_back_forProfile);
    }

    private void initFloatingActionButton() {

        addProfilephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSheetDialog();
            }
        });

    }

    private void viewProfileImage() {

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileImage.invalidate();
                Drawable drawable = profileImage.getDrawable();
                Common.IMAGE_BITMAP = ((BitmapDrawable) drawable.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, profileImage, "profileimage");
                Intent intent = new Intent(context, ActivityViewProfile.class);
                startActivity(intent, activityOptionsCompat.toBundle());


            }
        });

    }

    private void updatedProfileItems() {

        ic_change_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSheetDialogForUsername();
            }
        });

        ic_change_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSheetDialogForBio();
            }
        });


    }

    private void showSheetDialogForBio() {

        bottomSheetDialogForBio = new BottomSheetDialog(context);
        View view = getLayoutInflater().inflate(R.layout.layout_update_bio, null);

        confirmBio = view.findViewById(R.id.confirm_buttonChangeBio);
        eChangeBio = view.findViewById(R.id.etxt_update_bio);

        bottomSheetDialogForBio.setContentView(view);
        bottomSheetDialogForBio.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        bottomSheetDialogForBio.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialogForBio = null;
            }
        });

        bottomSheetDialogForBio.show();

        confirmBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updated Username...");
                progressDialog.show();
                uploadBioToDatabase();
                bottomSheetDialogForBio.dismiss();
            }
        });

    }

    private void uploadBioToDatabase() {

        firestore = FirebaseFirestore.getInstance();
        String updatedBio = eChangeBio.getText().toString().trim();

        HashMap<String, Object> bioHash = new HashMap<>();
        bioHash.put("bio", updatedBio);

        firestore.collection("Users").document(firebaseUser.getUid()).update(bioHash)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getInfo();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void showSheetDialogForUsername() {

        bottomSheetDialogForUsername = new BottomSheetDialog(context);
        View view = getLayoutInflater().inflate(R.layout.layout_update_username, null);

        confirmUsername = view.findViewById(R.id.confirm_buttonChangeUsername);
        eChangeUsername = view.findViewById(R.id.etxt_update_username);

        bottomSheetDialogForUsername.setContentView(view);
        bottomSheetDialogForUsername.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        bottomSheetDialogForUsername.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialogForUsername = null;
            }
        });

        bottomSheetDialogForUsername.show();

        confirmUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updated Username...");
                progressDialog.show();
                uploadUsernameToDatabase();
                bottomSheetDialogForUsername.dismiss();
            }
        });

    }

    private void uploadUsernameToDatabase() {

        firestore = FirebaseFirestore.getInstance();
        String updatedUsername = eChangeUsername.getText().toString().trim();

        HashMap<String, Object> usernameHash = new HashMap<>();
        usernameHash.put("username", updatedUsername);

        firestore.collection("Users").document(firebaseUser.getUid()).update(usernameHash)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getInfo();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void showSheetDialog() {

        View view = getLayoutInflater().inflate(R.layout.layout_bottom_choice_photo_sheet, null);

        galleryImage = view.findViewById(R.id.circleGallery);
        cameraImage = view.findViewById(R.id.circleCamera);

        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog = null;
            }
        });

        bottomSheetDialog.show();

        galleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });

        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkCameraPermission();
                bottomSheetDialog.dismiss();

            }
        });

    }

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 221);

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 222);

        } else {

            openCamera();

        }

    }

    private void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        try {

            File file = File.createTempFile("IMG_" + timeStamp, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, 440);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void openGallery() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select image"), IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            imageUri = data.getData();

            uploadImageToFirebase();


        }

        if (requestCode == 440
                && resultCode == RESULT_OK) {

            uploadImageToFirebase();

        }

    }


    private String getFileExtention(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadImageToFirebase() {

        progressDialog.setMessage("Image Profile uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (imageUri != null) {

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImages/" + System.currentTimeMillis() + "." + getFileExtention(imageUri));
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressDialog.dismiss();

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri downloadUrl = uriTask.getResult();

                    final String download_url = String.valueOf(downloadUrl);

                    HashMap<String, Object> hashMap = new HashMap<>();

                    hashMap.put("profileImage", download_url);

                    firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Upload Succesfully", Toast.LENGTH_SHORT).show();
                                    getInfo();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();

                }
            });
        }

    }

    private void signOut() {

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setMessage("Do you want to sign out your profile ?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(context, SplashScreen.class));
                        finish();
                    }
                });

                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog1 = alertDialog.create();
                alertDialog1.show();

            }
        });

    }

}