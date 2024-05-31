package com.example.nested;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.example.nested.ui.main.PageViewModel;
import com.example.nested.ui.main.PlaceholderFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProfileFragment extends Fragment {

    private static final String TAG = "TAG";
    private static final String ARG_UID = "UID";
    Button saveButton;
    ViewFlipper viewFlipper;
    TextView nameTextView;
    EditText nameEditText;
    ImageView imageView;
    StorageReference storageReference;
    DocumentReference documentReference;
    Uri imageUri;
    String uid;
    String name;
    PageViewModel pageViewModel;
    RequestCreator rc;
    boolean newUpload;

    public static ProfileFragment newInstance(String uid) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_UID, uid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        String uid = "";
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
            this.uid = uid;
        }
        pageViewModel.setUid(uid);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_fragment, container, false);
        viewFlipper = root.findViewById(R.id.viewFlipper);
        saveButton = root.findViewById(R.id.saveButton);
        nameTextView = root.findViewById(R.id.nameTextView);
        nameEditText = root.findViewById(R.id.nameEditText);
        imageView = root.findViewById(R.id.imageView);
        newUpload = false;

        storageReference = FirebaseStorage.getInstance().getReference().child("users/" + uid + "/profile.jpg");
        documentReference = FirebaseFirestore.getInstance().collection("users").document(uid);

        showInitialSettings();
        addListeners();

        /*pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@androidx.annotation.Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
    
    private void showInitialSettings() {
        //RequestCreator requestCreator;
        //requestCreator.into(imageView);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    name = (String) document.get("name");
                    rc = Picasso.get().load(Uri.parse((String) document.get("uri-1")));
                    pageViewModel.setName(name);
                    pageViewModel.setRC(rc);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        pageViewModel.getName().observe(getViewLifecycleOwner(), s -> {
            nameTextView.setText(name);
            nameEditText.setText(name);
        });

/*
        storageReference.getDownloadUrl()
                .addOnSuccessListener(uri -> pageViewModel.setFirstUri(uri))
                .addOnFailureListener(exception -> Log.d(TAG, "No such image uri"));*/

        //pageViewModel.getFirstUri().observe(getViewLifecycleOwner(), uri -> Picasso.get().load(uri).into(imageView));
        pageViewModel.getRC().observe(getViewLifecycleOwner(), rc -> rc.into(imageView));
    }

    private void addListeners() {
        imageView.setOnClickListener(view -> choosePicture());

        saveButton.setOnClickListener(view -> {
            nameTextView.setText(name);
            viewFlipper.setDisplayedChild(0);
            documentReference.update("name", name);
            if (newUpload) {
                uploadPicture();
                newUpload = false;
            }

        });

        nameTextView.setOnClickListener(view -> viewFlipper.setDisplayedChild(1));

        nameEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if ((keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
                name = nameEditText.getText().toString();
                nameTextView.setText(name);
                viewFlipper.setDisplayedChild(0);
            }
            return false;
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
        newUpload = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle("Uploading Image...");
        pd.show();

        //StorageReference reference = storageReference.child("users/" + uid + "/profile.jpg");

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(exception -> {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Failed To Upload", Toast.LENGTH_LONG).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage("Percentage: " + (int) progressPercent + "%");
                });


    }
}