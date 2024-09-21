package com.football.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyOtp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_otp);

        String verificationId = getIntent().getStringExtra("verificationId");
        String mobileNumber = getIntent().getStringExtra("mobile");

        // Assuming you have OTP input fields and a "verify" button
        Button verifyButton = findViewById(R.id.verify_proceed_button);

        EditText[] otpFields = new EditText[]{
                findViewById(R.id.otp_digit_1),
                findViewById(R.id.otp_digit_2),
                findViewById(R.id.otp_digit_3),
                findViewById(R.id.otp_digit_4),
                findViewById(R.id.otp_digit_5),
                findViewById(R.id.otp_digit_6)
        };

        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otpFields[index - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        verifyButton.setOnClickListener(v -> {
            // Collect the OTP entered by the user
            String otpCode = getOtpFromInputFields();

            if (otpCode.length() == 6 && verificationId != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpCode);

                signInWithPhoneAuthCredential(credential);
            } else {
                Toast.makeText(VerifyOtp.this, "Please enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getOtpFromInputFields() {
        EditText digit1 = findViewById(R.id.otp_digit_1);
        EditText digit2 = findViewById(R.id.otp_digit_2);
        EditText digit3 = findViewById(R.id.otp_digit_3);
        EditText digit4 = findViewById(R.id.otp_digit_4);
        EditText digit5 = findViewById(R.id.otp_digit_5);
        EditText digit6 = findViewById(R.id.otp_digit_6);
        return digit1.getText().toString() + digit2.getText().toString() +
                digit3.getText().toString() + digit4.getText().toString() +
                digit5.getText().toString() + digit6.getText().toString();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successful sign-in, proceed to the next activity
                        Intent intent = new Intent(VerifyOtp.this, home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String error = "OTP verification failed";
                        if (task.getException() != null) {
                            error += ": " + task.getException().getMessage();
                        }
                        Toast.makeText(VerifyOtp.this, error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
