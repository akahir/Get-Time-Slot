package com.example.gettimeslot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import Util.FacultyApi;
import Util.StudentApi;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button studentButton, facultyButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference facultyReference = db.collection("Faculty");
    private CollectionReference studentReference = db.collection("Student");

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        studentButton = findViewById(R.id.studentButton);
        facultyButton = findViewById(R.id.facultyButton);

        facultyButton.setOnClickListener(this);
        studentButton.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null) {
                    currentUser = firebaseAuth.getCurrentUser();
                    String currentUserId = currentUser.getUid();

                    facultyReference
                            .whereEqualTo("UserId",currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                    @Nullable FirebaseFirestoreException error) {

                                    if(error != null) {
                                        return;
                                    }

                                    if(!queryDocumentSnapshots.isEmpty()) {
                                        for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {
                                            FacultyApi facultyApi = FacultyApi.getFacultyInstance();
                                            facultyApi.setUserId(snapshot.getString("UserId"));
                                            facultyApi.setFacultyName(snapshot.getString("UserName"));
                                            facultyApi.setDepartment(snapshot.getString("DepartmentName"));

                                            startActivity(new Intent(MainActivity.this,
                                                    FacultySlotListActivity.class));
                                            finish();
                                        }
                                    }
                                }
                            });

                    studentReference
                            .whereEqualTo("UserId",currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                    @Nullable FirebaseFirestoreException error) {
                                    if(error != null) {
                                        return;
                                    }

                                    if(!queryDocumentSnapshots.isEmpty()) {
                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            StudentApi studentApi = StudentApi.getStudentInstance();
                                            studentApi.setUserId(snapshot.getString("UserId"));
                                            studentApi.setStudentName(snapshot.getString("UserName"));
                                            studentApi.setScholarNo(snapshot.getString("ScholarNo"));

                                            startActivity(new Intent(MainActivity.this,
                                                    DepartmentActivity.class));
                                            finish();
                                        }
                                    }
                                }
                            });

                }
                else {

                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facultyButton:
                startActivity(new Intent(MainActivity.this, LoginFacultyActivity.class));
                //finish();
                break;

            case R.id.studentButton:
                startActivity(new Intent(MainActivity.this, LoginStudentActivity.class));
                //finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
    }
}