package com.example.gettimeslot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import Util.FacultyApi;

public class CreateAccountFacultyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "CreateAccountFacultyActivity";
    private Spinner spinner;
    private ProgressBar progressBar;
    private Button createAccount;
    private AutoCompleteTextView emailTextView;
    private EditText passwordEditText, employeeNoEditText, facultyNameEditText;
    private String department;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Faculty");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_faculty);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        spinner = findViewById(R.id.create_faculty_spinner);
        progressBar = findViewById(R.id.create_faculty_progressBar);
        createAccount = findViewById(R.id.create_account_faculty);
        emailTextView = findViewById(R.id.create_faculty_email);
        passwordEditText = findViewById(R.id.create_faculty_password);
        employeeNoEditText = findViewById(R.id.create_faculty_no);
        facultyNameEditText = findViewById(R.id.create_faculty_name);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null) {
                    //user is already logged in...
                }
                else {
                    //no user yet
                }
            }
        };

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.department_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(emailTextView.getText().toString().trim()) &&
                !TextUtils.isEmpty(passwordEditText.getText().toString().trim()) &&
                !TextUtils.isEmpty(employeeNoEditText.getText().toString().trim()) &&
                !TextUtils.isEmpty(facultyNameEditText.getText().toString().trim()) &&
                !department.isEmpty()) {

                    String email = emailTextView.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    createUserAccount(email,password);
                }

                else
                {
                    Toast.makeText(CreateAccountFacultyActivity.this,
                            "Empty Fields Are Not Allowed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void createUserAccount(String email, final String password) {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()) {
                                currentUser = firebaseAuth.getCurrentUser();
                                final String currentUserId = currentUser.getUid();

                                String employeeNo = employeeNoEditText.getText().toString().trim();
                                String facultyName = facultyNameEditText.getText().toString().trim();

                                Map<String, String> facultyObj = new HashMap<>();
                                facultyObj.put("UserId", currentUserId);
                                facultyObj.put("UserName", facultyName);
                                facultyObj.put("EmployeeNo",employeeNo);
                                facultyObj.put("DepartmentName",department);

                                collectionReference.add(facultyObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                if(Objects.requireNonNull(task.getResult()).exists()) {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    String FacultyName = task.getResult().getString("UserName");

                                                                    FacultyApi facultyApi = FacultyApi.getFacultyInstance();
                                                                    facultyApi.setUserId(currentUserId);
                                                                    facultyApi.setFacultyName(FacultyName);
                                                                    facultyApi.setDepartment(department);

                                                                    startActivity(new Intent(CreateAccountFacultyActivity.this,
                                                                            FacultySlotListActivity.class));
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
                            else {
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        department = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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