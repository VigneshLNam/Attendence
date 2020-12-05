package com.devlover.attendance;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devlover.classes.TableClass;
import com.devlover.adapters.TableAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class SettimeActivity extends AppCompatActivity {
    Spinner dayPicker;
    EditText timePicker;
    Button Add;
    String Day,Time;
    int Sl_No;
    DataBaseHelper MyDb;
    ArrayList<TableClass> myList;
    TableAdapter tableAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settime);
        MyDb = new DataBaseHelper(SettimeActivity.this);
        Intent intent = getIntent();
        Sl_No = intent.getIntExtra("SL_No",0);
        dayPicker = findViewById(R.id.Day);
        timePicker = findViewById(R.id.timePicker);
        Add = findViewById(R.id.setTime);
        listView = findViewById(R.id.timeList);
        final String[] day = new String[]{"Select","Mon","Tue","Wed","Thu","Fri","Sat"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, day);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayPicker.setAdapter(adapter);
        function();
        dayPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) Day = day[position];
                else Day = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SettimeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePickerr, int selectedHour, int selectedMinute) {
                        if(selectedMinute<10 & selectedHour<10){
                            Time = "0"+selectedHour + ":0" + selectedMinute;
                        }else if(selectedMinute<10){
                            Time = selectedHour + ":0" + selectedMinute;
                        }else if(selectedHour<10){
                            Time = "0"+selectedHour + ":" + selectedMinute;
                        }else{
                            Time = selectedHour + ":" + selectedMinute;
                        }
                        timePicker.setText(Time);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Day!=null | !TextUtils.isEmpty(timePicker.getText().toString()) | Sl_No!=0){
                    if(MyDb.inserttable(Sl_No,Day,timePicker.getText().toString())){
                        function();
                        Toast.makeText(SettimeActivity.this,"Success",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SettimeActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void function(){
        myList = MyDb.getTimetable(Sl_No);
        if(!myList.isEmpty()){
            tableAdapter = new TableAdapter(getApplicationContext(),myList);
            listView.setAdapter(tableAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TableClass list = myList.get(position);
                    if(MyDb.deletetimeTable(list.getSl_No(),list.getDay(),list.getTime())){
                        myList.clear();
                        function();
                    }
                }
            });
        }
    }
}