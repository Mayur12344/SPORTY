package com.football.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SentOtp extends AppCompatActivity {
    EditText numberInput;
    Button sendOtpButton;
    ProgressBar progressBar;

    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_otp);

        numberInput = findViewById(R.id.number_input);
        sendOtpButton = findViewById(R.id.SentOtp);
        progressBar = findViewById(R.id.progress_bar);

        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = numberInput.getText().toString().trim();

                if (phoneNumber.isEmpty()) {
                    Toast.makeText(SentOtp.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ensure the phone number starts with a "+" and includes the country code
                if (!phoneNumber.startsWith("+")) {
                    // Assuming you want to add the country code for India, replace with your country's code
                    phoneNumber = "+91" + phoneNumber;  // Replace with the appropriate country code
                }

                progressBar.setVisibility(View.VISIBLE);
                sendOtpButton.setVisibility(View.INVISIBLE);

                // Initiate OTP sending
                String finalPhoneNumber = phoneNumber;
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        SentOtp.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.INVISIBLE);
                                sendOtpButton.setVisibility(View.VISIBLE);
                                // Auto verification is successful, handle here if needed
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                sendOtpButton.setVisibility(View.VISIBLE);
                                Toast.makeText(SentOtp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressBar.setVisibility(View.INVISIBLE);
                                sendOtpButton.setVisibility(View.VISIBLE);
                                mVerificationId = s;

                                // Intent to move to VerifyOtp activity
                                Intent intent = new Intent(SentOtp.this, VerifyOtp.class);
                                intent.putExtra("mobile", finalPhoneNumber);
                                intent.putExtra("verificationId", mVerificationId);
                                startActivity(intent);
                            }
                        }
                );
            }
        });
    }
}
