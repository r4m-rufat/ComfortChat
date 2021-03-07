package com.example.comfortchat.Activities.Registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comfortchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    // widgets
    private ProgressDialog progressDialog;
    private Button nextButton;
    private EditText number, etextCountry, verificationSMS;
    private TextView txtverification;
    private static final String TAG = "RegisterActivity";
    Context context;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private String verificationID;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;

        firebaseAuth = FirebaseAuth.getInstance();



        setupWidgets();

        setupNextButton();
        setupCallBack();

    }

    private void setupWidgets(){

        nextButton = findViewById(R.id.nextButton);
        number = findViewById(R.id.etxtphoneNumber);
        verificationSMS = findViewById(R.id.etxtverificationCode);
        etextCountry = findViewById(R.id.etxtcountryCode);
        txtverification = findViewById(R.id.txtverifyCode);

    }


    private void setupNextButton(){

        progressDialog = new ProgressDialog(context);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextButton.getText().toString().equals("NEXT")){
                    progressDialog.setMessage("Please wait. Code is sent...");
                    progressDialog.show();

                    String phoneNumber = "+" + etextCountry.getText().toString() + number.getText().toString();
                    phoneNumberVerificationCode(phoneNumber);

                    txtverification.setVisibility(View.VISIBLE);
                    verificationSMS.setVisibility(View.VISIBLE);

                }else {

                    progressDialog.setMessage("Verifying phone number...");
                    progressDialog.show();

                    String getValueOfCode = verificationSMS.getText().toString();
                    verifyPhoneNumberWithCode(verificationID, getValueOfCode);

                }
            }
        });

    }

    private void setupCallBack(){

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                progressDialog.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                verificationID = verificationId;
                resendingToken = forceResendingToken;

                progressDialog.dismiss();
                nextButton.setText("Confirm");

            }

        };
    }


    private void phoneNumberVerificationCode(String phoneNumber){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                callbacks
        );

    }

    private void verifyPhoneNumberWithCode(String verificationID, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);

        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            if (user != null){

                                startActivity(new Intent(context, UserInfoActivity.class));

                            } else {
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            startActivity(new Intent(context, UserInfoActivity.class));
        }
    }
}