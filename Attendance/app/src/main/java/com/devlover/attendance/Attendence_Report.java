package com.devlover.attendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class Attendence_Report extends AppCompatActivity {
    int No;
    ListView reportView;
    DataBaseHelper MyDb;
    ArrayList<String> reportList;
    Button deleteStudent,updateStudent;
    EditText upName;
    String string,name;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence__report);
        reportView = findViewById(R.id.reportView);
        deleteStudent = findViewById(R.id.deleteStudent);
        updateStudent = findViewById(R.id.updateName);
        upName = findViewById(R.id.upName);
        Intent intent = getIntent();
        No = intent.getIntExtra("No",0);
        toolbar = findViewById(R.id.toolbar);
        string = intent.getStringExtra("Class");
        name = intent.getStringExtra("Name");
        upName.setText(name);
        toolbar.setTitle(string);
        MyDb = new DataBaseHelper(getApplicationContext());
        function();
        Toast.makeText(getApplicationContext(),"Student No : "+No,Toast.LENGTH_SHORT).show();
        reportView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyDb.deleteAttendence(No,reportList.get(position));
                function();
            }
        });
        deleteStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder Alert = new AlertDialog.Builder(Attendence_Report.this);
                Alert.setMessage("You Sure you want to delete this Data").setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int val = MyDb.deleteAttendence(No);
                                Log.e("check",String.valueOf(val));
                                if(val==1){
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Data cannot be deleted",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                AlertDialog alert = Alert.create();
                alert.show();
            }
        });
        updateStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(upName.getText().toString())){
                    if(MyDb.updateName(No,upName.getText().toString()))
                        finish();
                    else
                        Toast.makeText(getApplicationContext(),"Data is not Updated",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter the name to update",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void function(){
        reportList = MyDb.StudReport(No);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,reportList);
        reportView.setAdapter(arrayAdapter);
    }
}