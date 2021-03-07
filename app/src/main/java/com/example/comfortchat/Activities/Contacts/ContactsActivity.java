package com.example.comfortchat.Activities.Contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.comfortchat.Adapters.RecyclerContactsAdapter;
import com.example.comfortchat.Models.Users;
import com.example.comfortchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private List<Users> usersList = new ArrayList<>();
    private RecyclerContactsAdapter contactsAdapter;
    private RecyclerView recylerContacts;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private static final String TAG = "ContactsActivity";
    public static final int REQUEST_READ_CONTACTS = 79;
    private ArrayList mobile_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        recylerContacts = findViewById(R.id.recyclerViewForContacts);

        // if users in your contact uses this chat then you will see you in contact list
        if (firebaseUser != null){
            getContactFromMobile();
            getContactList();
        }

    }

    private void getContactFromMobile() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobile_array = getAllPhoneNumbers();
        } else {
            requestPermission();
        }

    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
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
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
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

    private void getContactList() {

        firestore.collection("Users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        usersList.clear();

                       for (QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){

                           Log.d(TAG, "onSuccess: data " + queryDocumentSnapshot.toString());

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

                           if (userID != null && !userID.equals(firebaseUser.getUid())){
                               if (mobile_array.contains(users.getUserPhone())){

                                   usersList.add(users);
                                   Log.d(TAG, "onSuccess: users phone: " + users.getUserPhone());

                               }
                           }


                       }

                        recylerContacts.setHasFixedSize(true);
                        recylerContacts.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                        contactsAdapter = new RecyclerContactsAdapter(ContactsActivity.this, usersList);
                        recylerContacts.setAdapter(contactsAdapter);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }

        });


    }
}