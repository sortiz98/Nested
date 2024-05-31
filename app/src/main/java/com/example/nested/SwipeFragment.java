package com.example.nested;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nested.ui.main.PageViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class SwipeFragment extends Fragment {

    private ArrayList<User> al;
    private CustomAdapter arrayAdapter;
    private int i;
    private static final String TAG = "TAG";
    private static final String ARG_UID = "UID";
    StorageReference storageReference;
    DocumentReference documentReference;
    CollectionReference collectionReference;
    PageViewModel pageViewModel;
    String uid;
    ListView listView;
    //List<User> items;

    SwipeFlingAdapterView flingContainer;

    public static SwipeFragment newInstance(String uid) {
        SwipeFragment fragment = new SwipeFragment();
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
        View root = inflater.inflate(R.layout.swipe_fragment, container, false);
        flingContainer = root.findViewById(R.id.frame);
        //root.findViewById(R.id.right).setOnClickListene;

        al = new ArrayList<>();
        /*
        al.add("php");
        al.add("c");
        al.add("python");
        al.add("java");
        al.add("html");
        al.add("c++");
        al.add("css");
        al.add("javascript");
         */

        arrayAdapter = new CustomAdapter(getActivity(), R.layout.item, al);

        storageReference = FirebaseStorage.getInstance().getReference().child("users/" + uid + "/profile.jpg");
        collectionReference = FirebaseFirestore.getInstance().collection("users");
        documentReference = collectionReference.document(uid);


        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                makeToast(getActivity(), "Left!");
                User subject = (User) dataObject;
                //subject.getUid();
                //documentReference.
                //documentReference.collection("matches").document("jdd").set();

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                makeToast(getActivity(), "Right!");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                //al.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                makeToast(getActivity(), "Clicked!");
            }
        });


        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "listen:error", error);
                }

                for (DocumentChange change : value.getDocumentChanges()) {
                    QueryDocumentSnapshot doc = change.getDocument();
                    if (doc.getId().equals(uid)) {
                        continue;
                    }
                    switch (change.getType()) {
                        case ADDED:
                            Log.d(TAG,"New User: " + change.getDocument().getData());
                            RequestCreator rc1 = Picasso.get().load((String) doc.get("uri-1"));
                            User item = new User(doc.getId(), doc.get("name").toString(), rc1);
                            al.add(item);
                            arrayAdapter.notifyDataSetChanged();
                            break;
                        case REMOVED:
                            Log.d(TAG,"Modified User: " + change.getDocument().getData());
                            break;
                        case MODIFIED:
                            Log.d(TAG,"Removed User: " + change.getDocument().getData());
                            break;
                    }
                }
            }
        });

        return root;
    }



    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }


   // @OnClick(R.id.right)
    public void right() {
        flingContainer.getTopCardListener().selectRight();
    }

    //@OnClick(R.id.left)
    public void left() {
        flingContainer.getTopCardListener().selectLeft();
    }



}