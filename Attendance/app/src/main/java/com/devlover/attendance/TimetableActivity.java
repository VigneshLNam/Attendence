package com.devlover.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devlover.adapters.ClassAdapter;

import java.util.ArrayList;

public class TimetableActivity extends AppCompatActivity {
    private ListView cList1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        cList1 = findViewById(R.id.clist1);
        function2();
    }

    public void function2(){
        final ArrayList my = ClassAdapter.classList;
        if(!ClassAdapter.classListNames.isEmpty()) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_view,R.id.simpleTview, ClassAdapter.classListNames);
            cList1.setAdapter(arrayAdapter);
            cList1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent report = new Intent(TimetableActivity.this,SettimeActivity.class);
                    report.putExtra("SL_No",(Integer) my.get(position));
                    startActivity(report);
                }
            });
        }else{
            Toast.makeText(this,"Empty",Toast.LENGTH_SHORT).show();
        }
    }
}