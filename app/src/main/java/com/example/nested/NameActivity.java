package com.example.nested;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NameActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    EditText nameEditText;
    Button button;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        String userID = getIntent().getStringExtra("EXTRA_ID");

        nameEditText = findViewById(R.id.editTextName);
        button = findViewById(R.id.nameButton);

        fStore = FirebaseFirestore.getInstance();

        button.setOnClickListener(view -> {
            String name = nameEditText.getText().toString();
            DocumentReference documentReference = fStore.collection("users").document(userID);
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            //User user = new User(userID, name);
            documentReference.set(user).addOnSuccessListener(aVoid -> {
                Log.d(TAG, "onSuccess: user Profile is created for " + userID);
            });

            Intent intent = new Intent(NameActivity.this, PhotoActivity.class);
            intent.putExtra("EXTRA_ID", userID);
            startActivity(intent);
        });


    }
}