package com.example.gettimeslot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Util.StudentApi;

public class CreateAccountStudentActivity extends AppCompatActivity {
    private static final String TAG = "CreateAccountStudentActivity";
    private ProgressBar progressBar;
    private Button createAccount;
    private AutoCompleteTextView emailTextView;
    private EditText passwordEditText, scholarNoEditText, studentNameEditText;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Student");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_student);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        progressBar = findViewById(R.id.create_student_progressBar);
        createAccount = findViewById(R.id.create_account_student);
        emailTextView = findViewById(R.id.create_student_email);
        passwordEditText = findViewById(R.id.create_student_password);
        scholarNoEditText = findViewById(R.id.create_student_no);
        studentNameEditText = findViewById(R.id.create_student_name);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null) {
                    //user is already logged in
                }
                else{
                    //no user yet
                }
            }
        };

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(emailTextView.getText().toString().trim()) &&
                !TextUtils.isEmpty(passwordEditText.getText().toString().trim()) &&
                !TextUtils.isEmpty(scholarNoEditText.getText().toString().trim()) &&
                !TextUtils.isEmpty(studentNameEditText.getText().toString().trim())) {

                    String email = emailTextView.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    createUserAccount(email,password);
                }

                else
                {
                    Toast.makeText(CreateAccountStudentActivity.this,
                            "Empty Fields Are Not Allowed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createUserAccount(String email, final String password) {

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()) {
                                currentUser = firebaseAuth.getCurrentUser();
                                final String currentUserId = currentUser.getUid();

                                String studentName = studentNameEditText.getText().toString().trim();
                                String scholarNo = scholarNoEditText.getText().toString().trim();

                                Map<String, String> studentObj = new HashMap<>();
                                studentObj.put("UserId", currentUserId);
                                studentObj.put("UserName", studentName);
                                studentObj.put("ScholarNo", scholarNo);

                                 collectionReference.add(studentObj)
                                         .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                             @Override
                                             public void onSuccess(DocumentReference documentReference) {

                                                 documentReference.get()
                                                         .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                 if(task.getResult().exists()) {
                                                                     progressBar.setVisibility(View.INVISIBLE);

                                                                     String Name = task.getResult().getString("UserName");
                                                                     String SchNo = task.getResult().getString("ScholarNo");

                                                                     StudentApi studentApi = StudentApi.getStudentInstance();
                                                                     studentApi.setUserId(currentUserId);
                                                                     studentApi.setStudentName(Name);
                                                                     studentApi.setScholarNo(SchNo);

                                                                     startActivity(new Intent(CreateAccountStudentActivity.this,
                                                                             DepartmentActivity.class));
                                                                     finish();
                                                                 }
                                                                 else {
                                                                     progressBar.setVisibility(View.INVISIBLE);
                                                                 }
                                                             }
                                                         });
                                             }
                                         })
                                         .addOnFailureListener(new OnFailureListener() {
                                             @SuppressLint("LongLogTag")
                                             @Override
                                             public void onFailure(@NonNull Exception e) {
                                                 Log.d(TAG, "onFailure: " + e.getMessage());
                                             }
                                         });
                            }
                            else{
                                //something went wrong
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                        }
                    });
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