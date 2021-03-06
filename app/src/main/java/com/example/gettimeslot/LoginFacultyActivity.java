package com.example.gettimeslot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class LoginFacultyActivity extends AppCompatActivity {
    private static final String TAG = "LoginFacultyActivity";
    private ProgressBar progressBar;
    private Button createAccount, loginButton;
    private AutoCompleteTextView emailTextView;
    private EditText passwordEditText;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Faculty");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_faculty);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        progressBar = findViewById(R.id.login_faculty_progressBar);
        createAccount = findViewById(R.id.create_acc_button_login_faculty);
        loginButton = findViewById(R.id.login_faculty_button);
        emailTextView = findViewById(R.id.login_email_faculty);
        passwordEditText = findViewById(R.id.login_password_faculty);

        firebaseAuth = FirebaseAuth.getInstance();

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginFacultyActivity.this, CreateAccountFacultyActivity.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginWithEmailPassword(emailTextView.getText().toString().trim(),
                        passwordEditText.getText().toString().trim());
            }
        });
    }

    private void loginWithEmailPassword(String email, String password) {

        progressBar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            currentUser = firebaseAuth.getCurrentUser();
                            String currentUserId = currentUser.getUid();

                            collectionReference.whereEqualTo("UserId",currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                            @Nullable FirebaseFirestoreException error) {

                                            if(error != null){

                                            }
                                            assert queryDocumentSnapshots != null;
                                            if(!queryDocumentSnapshots.isEmpty()) {
                                                progressBar.setVisibility(View.INVISIBLE);

                                                for(QueryDocumentSnapshot snapshot: queryDocumentSnapshots) {

                                                    FacultyApi facultyApi = FacultyApi.getFacultyInstance();
                                                    facultyApi.setUserId(snapshot.getString("UserId"));
                                                    facultyApi.setFacultyName(snapshot.getString("UserName"));
                                                    facultyApi.setDepartment(snapshot.getString("DepartmentName"));

                                                    startActivity(new Intent(LoginFacultyActivity.this,
                                                            FacultySlotListActivity.class));
                                                    finish();
                                                }
                                            }
                                            else {
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "onFailure: " + e.getMessage());
                        }
                    });
        }

        else {

            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginFacultyActivity.this,
                    "Enter email and password",
                    Toast.LENGTH_LONG).show();
        }
    }

}