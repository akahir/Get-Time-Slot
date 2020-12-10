package com.example.gettimeslot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Util.FacultyApi;
import Util.StudentApi;

public class AddSlotActivity extends AppCompatActivity {
    private static final String TAG = "AddSlotActivity";
    private ProgressBar progressBar;
    private EditText subjectEditText, sTimeEditText, eTimeEditText;
    private Button saveButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Slot");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_slot);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        progressBar = findViewById(R.id.add_slot_progressBar);
        saveButton = findViewById(R.id.add_slot_saveButton);
        subjectEditText = findViewById(R.id.add_slot_subject);
        sTimeEditText = findViewById(R.id.add_slot_sTime);
        eTimeEditText = findViewById(R.id.add_slot_eTime);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null) {
                    //user already logged in
                }
                else{
                    //no user yet
                }
            }
        };

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(subjectEditText.getText().toString().trim()) &&
                !TextUtils.isEmpty(sTimeEditText.getText().toString().trim()) &&
                !TextUtils.isEmpty(eTimeEditText.getText().toString().trim())) {

                    saveSlot();
                }
                else {
                    Toast.makeText(AddSlotActivity.this,
                            "Empty Fields Are Not Allowed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveSlot() {
        String subject = subjectEditText.getText().toString().trim();
        String sTime = sTimeEditText.getText().toString().trim();
        String eTime = eTimeEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(subject) && !TextUtils.isEmpty(sTime) && !TextUtils.isEmpty(eTime)) {

            if(FacultyApi.getFacultyInstance() != null) {
                String currentUserId = FacultyApi.getFacultyInstance().getUserId();
                String department = FacultyApi.getFacultyInstance().getDepartment();
                String facultyName = FacultyApi.getFacultyInstance().getFacultyName();

                Map<String, String > slotObj = new HashMap<>();
                slotObj.put("UserId", currentUserId);
                slotObj.put("StartTime", sTime);
                slotObj.put("EndTime", eTime);
                slotObj.put("Subject", subject);
                slotObj.put("Department", department);
                slotObj.put("UserName", facultyName);

                collectionReference.add(slotObj)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressBar.setVisibility(View.INVISIBLE);

                                startActivity(new Intent(AddSlotActivity.this, FacultySlotListActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.INVISIBLE);

                                Log.d(TAG, "onFailure: " + e.getMessage());
                            }
                        });
            }
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(firebaseAuth != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
    }
}