package com.example.gettimeslot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Model.Slot;
import UI.StudentSlotRecyclerAdapter;

public class StudentSlotListActivity extends AppCompatActivity {
    private static final String TAG = "StudentSlotListActivity";
    private TextView noSlotTextView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Slot");

    private RecyclerView recyclerView;
    private StudentSlotRecyclerAdapter recyclerAdapter;
    private List<Slot> slotList;

    private String department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_slot_list);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        Bundle bundle = getIntent().getExtras();
        department = String.valueOf(Objects.requireNonNull(bundle).get("department"));

        noSlotTextView = findViewById(R.id.no_available_textView);

        slotList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        recyclerView = findViewById(R.id.student_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        collectionReference.whereEqualTo("Department", department)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()) {

                            for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {

                                Slot slot = snapshot.toObject(Slot.class);
                                slotList.add(slot);
                            }

                            recyclerAdapter = new StudentSlotRecyclerAdapter(StudentSlotListActivity.this, slotList);
                            recyclerView.setAdapter(recyclerAdapter);
                            recyclerAdapter.notifyDataSetChanged();
                        }
                        else {
                            noSlotTextView.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.student_signOut:
                //sign out user
                signOutUser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOutUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.sure_popup,null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button noButton = view.findViewById(R.id.popup_no_button);
        Button yesButton = view.findViewById(R.id.popup_yes_button);

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentUser!=null && firebaseAuth!=null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(StudentSlotListActivity.this, MainActivity.class));
                    finish();
                }
                dialog.dismiss();
            }
        });

    }

}