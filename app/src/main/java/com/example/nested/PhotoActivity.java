package com.example.nested;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class PhotoActivity extends AppCompatActivity {

    ImageView photoImageView;
    Button button;
    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    String userID;
    DocumentReference documentReference;
    RequestCreator requestCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        userID = getIntent().getStringExtra("EXTRA_ID");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        documentReference = FirebaseFirestore.getInstance().collection("users").document(userID);


        photoImageView = findViewById(R.id.photoImageView);
        button = findViewById(R.id.photoButton);

        photoImageView.setOnClickListener(view -> choosePicture());

        button.setOnClickListener(view -> {
            uploadPicture();
            Intent intent = new Intent(PhotoActivity.this, TabbedActivity.class);
            intent.putExtra("EXTRA_ID", userID);
            startActivity(intent);
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageUri = data.getData();
                requestCreator = Picasso.get().load(imageUri);
                requestCreator.into(photoImageView);
                //photoImageView.setImageURI(imageUri);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        Map<String, Object> user = new HashMap<>();
        user.put("uri-1", imageUri.toString());
        documentReference.set(user, SetOptions.merge());

        StorageReference reference = storageReference.child("users/" + userID + "/profile.jpg");

        reference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(exception -> {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed To Upload", Toast.LENGTH_LONG).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage("Percentage: " + (int) progressPercent + "%");
                });


    }


    private void uploadData() {
        String name = "temp_name";

        if (!TextUtils.isEmpty(name) || imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));
            UploadTask uploadTask = reference.putFile(imageUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Map<String, String> profile = new HashMap<>();
                    profile.put("name", name);

                    member.setName(name);
                    member.setUid(userID);
                    member.setUri(downloadUri.toString());

                    databaseReference.child(userID).setValue(member);

                    documentReference.set(profile)
                            .addOnSuccessListener((OnSuccessListener<Void>) aVoid -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(CreateProfile.this, "Profile Created", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    Intent intent = new Intent(CreateProfile.this, Fragment1.class);
                                    startActivity(intent);
                                }, 2000);
                            });
                }
            });
        } else {
            Toast.makeText(this, "Please fill all Fields", Toast.LENGTH_SHORT).show();
        }
    }

}