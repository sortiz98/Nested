package com.example.nested;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText emailId, password;
    Button registerButton;
    FirebaseAuth mFirebaseAuth;
    TextView alreadyRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextRegisterEmail);
        /*mFirebaseAuth.sendSignInLinkToEmail(emailId, );
        mFirebaseAuth.getCurrentUser().sendEmailVerification();*/
        password = findViewById(R.id.editTextRegisterPassword);
        alreadyRegistered = findViewById(R.id.alreadyRegistered);
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            String email = emailId.getText().toString();
            String pwd = password.getText().toString();
            if (email.isEmpty()) {
                emailId.setError("Please enter email address");
                emailId.requestFocus();
            } else if (pwd.isEmpty()) {
                password.setError("Please enter your password");
                password.requestFocus();
            } else if (email.isEmpty() && pwd.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Fields Are Empty!", Toast.LENGTH_SHORT).show();
            } else if (!(email.isEmpty() && pwd.isEmpty())) {
                mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Sign Up Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(RegisterActivity.this, NameActivity.class);
                        intent.putExtra("EXTRA_ID", mFirebaseAuth.getCurrentUser().getUid());
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(RegisterActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
            }
        });

        alreadyRegistered.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

    }
}