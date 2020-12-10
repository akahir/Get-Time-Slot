package com.example.gettimeslot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DepartmentActivity extends AppCompatActivity {
    private ArrayList<String> departmentList;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        listView = findViewById(R.id.listView);

        String[] values = new String[] {"","ECE","CSE","Electrical","Mechanical","Civil","Chemical"};

        departmentList = new ArrayList<String>();
        departmentList.addAll(Arrays.asList(values));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                departmentList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String itemValue = (String) listView.getItemAtPosition(position);

                Intent intent = new Intent(DepartmentActivity.this, StudentSlotListActivity.class);
                intent.putExtra("department", itemValue);
                startActivity(intent);
            }
        });
    }

}