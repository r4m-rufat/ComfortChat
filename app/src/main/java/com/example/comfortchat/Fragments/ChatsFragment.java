package com.example.comfortchat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.comfortchat.Adapters.RecyclerChatsAdapter;
import com.example.comfortchat.Models.ChatList;
import com.example.comfortchat.Models.Users;
import com.example.comfortchat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatsFragment extends Fragment {

    private List<ChatList> chatLists;
    private RecyclerChatsAdapter recyclerChatsAdapter;
    private RecyclerView recyclerView;

    // Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firestore;

    private ArrayList<String> userIDs;
    private Handler handler = new Handler();
    private static final String TAG = "ChatsFragment";


    public ChatsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewForChats);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        chatLists = new ArrayList<>();
        userIDs = new ArrayList<>();


        setupAdapters();

        if (firebaseUser != null) {
            getChatList();
        }


        return view;
    }

    private void setupAdapters() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    private void getChatList() {

        databaseReference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatLists.clear();
                userIDs.clear();

                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {

                    String userID = singleSnapshot.child("chatid").getValue().toString();

                    userIDs.add(userID);

                }

                getUserData();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserData() {

        handler.post(new Runnable() {
            @Override
            public void run() {

                for (final String userID : userIDs) {

                    firestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            try {

                                ChatList chatList = new ChatList(
                                        userID,
                                        documentSnapshot.getString("username"),
                                        "Everything will be okay",
                                        documentSnapshot.getString("lastSeen"),
                                        documentSnapshot.getString("profileImage")
                                );

                                chatLists.add(chatList);

                            } catch (Exception e) {

                            }

                            /**
                             * recylerview set in here
                             */
                            recyclerChatsAdapter = new RecyclerChatsAdapter(getContext(), chatLists);
                            if (recyclerChatsAdapter != null) {
                                recyclerChatsAdapter.notifyItemInserted(0);
                                recyclerChatsAdapter.notifyDataSetChanged();
                            }
                            recyclerView.setAdapter(recyclerChatsAdapter);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }

            }
        });

    }

    public String getCurrentDate() {

        Date current = Calendar.getInstance().getTime();
        SimpleDateFormat currentFormat = new SimpleDateFormat("HH:mm");
        String currentTime = currentFormat.format(current);

        return "" + currentTime;

    }

    @Override
    public void onStop() {
        super.onStop();

        if (firebaseUser != null) {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("lastSeen", getCurrentDate());

            firestore.collection("Users")
                    .document(firebaseUser.getUid())
                    .update(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.d(TAG, "onSuccess: successfully updated last seen");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: is not updated last seen");

                }
            });

        }

    }

    @Override
    public void onStart() {
        super.onStart();

        if (firebaseUser != null) {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("lastSeen", "Online");

            firestore.collection("Users")
                    .document(firebaseUser.getUid())
                    .update(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.d(TAG, "onSuccess: successfully updated last seen");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: is not updated last seen");

                }
            });
        }
    }
}