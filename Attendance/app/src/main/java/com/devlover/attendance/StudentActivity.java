package com.devlover.attendance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.devlover.adapters.StudentAdapter;
import com.devlover.adapters.StudentAdapter1;
import com.devlover.classes.StudentsList;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StudentActivity extends AppCompatActivity {
    DatePickerDialog picker;
    String Date = null;
    int Sl_No;
    StudentAdapter studentAdapter;
    StudentAdapter1 studentAdapter1;
    ListView StudList,StudList1;
    DataBaseHelper MyDb;
    ArrayList<StudentsList> studentList,studList1;
    Toolbar toolbar;
    LinearLayout slayout,Addlayout;
    EditText search,studentName,date;;
    Button submitAttendance;
    int count,count1,present,count2 = 0;
    ArrayList myList;
    String Classstring;
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        myList = new ArrayList();
        StudList = findViewById(R.id.StudList);
        StudList1 = findViewById(R.id.StudList1);
        Intent intent = getIntent();
        Sl_No = intent.getIntExtra("Sl_No",0);
        Classstring = intent.getStringExtra("Class");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Classstring);
        slayout = findViewById(R.id.slayout);
        Addlayout = findViewById(R.id.Addlayout);
        submitAttendance = findViewById(R.id.submitAttendance);
        search = (EditText)findViewById(R.id.search);
        studentName = (EditText)findViewById(R.id.studentName);
        date = (EditText)findViewById(R.id.date);
        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(StudentActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        java.util.Date da ;
                        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            da = (Date)format1.parse(dayOfMonth + "-" + (month + 1) + "-" + year);
                            date.setText(df.format(da));
                        } catch (ParseException e) {
                            date.setText(df.format(dayOfMonth + "-" + (month + 1) + "-" + year));
                            e.printStackTrace();
                        }
                    }
                },year,month,day);
                picker.show();
            }
        });
        Button stuName = findViewById(R.id.stuName);
        toolbar.inflateMenu(R.menu.tbar1);
        MyDb = new DataBaseHelper(StudentActivity.this);
        function();
        function2();

        submitAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(date.getText().toString())){
                    Date = date.getText().toString();
                    Log.e("check",Date);
                }
                if(myList.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Mark Attendance",Toast.LENGTH_LONG).show();
                }else{
                    for(Object i : myList){
                        int result = (Integer) i;
                        if(MyDb.Attendance(result,Date)){
                            present+=1;
                        }
                    }
                    Intent Class = new Intent(StudentActivity.this,MainActivity.class);
                    Toast.makeText(getApplicationContext(),"Total No of Students Present = "+present,Toast.LENGTH_LONG).show();
                    Class.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(Class);
                    finish();
                }

            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case (R.id.sbar):
                        if(count%2==0) {
                            if(count1%2!=0) {
                                count1+=1;
                                Addlayout.setVisibility(View.GONE);
                            }
                            slayout.setVisibility(View.VISIBLE);
                        }else {
                            slayout.setVisibility(View.GONE);
                        }
                        count+=1;
                        break;
                    case (R.id.addstudent):
                        if(count1%2==0) {
                            if(count%2!=0) {
                                count += 1;
                                slayout.setVisibility(View.GONE);
                            }
                            Addlayout.setVisibility(View.VISIBLE);
                        }else {
                            Addlayout.setVisibility(View.GONE);
                        }
                        count1+=1;
                        break;
                    case (R.id.report1):
                        if(count2%2==0) {
                            StudList1.setVisibility(View.VISIBLE);
                            StudList.setVisibility(View.GONE);
                            date.setVisibility(View.GONE);
                        }else {
                            StudList1.setVisibility(View.GONE);
                            StudList.setVisibility(View.VISIBLE);
                            date.setVisibility(View.VISIBLE);
                        }
                        count2+=1;
                        break;
                }
                return false;
            }
        });

        stuName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(studentName.getText().toString())){
                    if(MyDb.insertStudent(Sl_No,studentName.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Sucess",Toast.LENGTH_LONG).show();
                        Addlayout.setVisibility(View.GONE);
                        count1 += 1;
                        studentName.setText("");
                        function();
                        function2();
                    }else{
                        Toast.makeText(getApplicationContext(),"Error!!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    studentAdapter.filter(s.toString());
                }catch (Exception ex){
                }
                try {
                    studentAdapter1.filter(s.toString());
                }catch (Exception ex){
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void function2(){
        studList1 = studentList;
        if(!studList1.isEmpty()){
            studentAdapter1 = new StudentAdapter1(StudentActivity.this,studList1);
            StudList1.setAdapter(studentAdapter1);
            StudList1.setTextFilterEnabled(true);
            StudList1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    StudentsList studentsList = studList1.get(position);
                    Intent intent2 = new Intent(StudentActivity.this,Attendence_Report.class);
                    intent2.putExtra("No",studentsList.getStudentNo());
                    intent2.putExtra("Name",studentsList.getStudentName());
                    intent2.putExtra("Class",Classstring);
                    startActivity(intent2);
                 }
            });
        }

    }

    public void function(){
        studentList = MyDb.Students(Sl_No);
        if(!studentList.isEmpty()){
            studentAdapter = new StudentAdapter(StudentActivity.this,studentList);
            StudList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            StudList.setAdapter(studentAdapter);
            StudList.setTextFilterEnabled(true);
            StudList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    StudentsList sList = studentList.get(position);
                    StudentAdapter.Viewholder viewholder;
                    if(view == null) {
                        viewholder = new StudentAdapter.Viewholder();
                        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                        view = inflater.inflate(R.layout.student_adapter, parent, false);
                        viewholder.StudentView = view.findViewById(R.id.stuName1);
                        view.setTag(viewholder);
                    }else{
                        viewholder = (StudentAdapter.Viewholder)view.getTag();
                    }
                    int selectedItem = sList.getStudentNo();
                    if(myList.contains(selectedItem)){
                        int index = myList.indexOf(selectedItem);
                        myList.remove(index);
                        Toast.makeText(getApplicationContext(),"Removed "+sList.getStudentName(),Toast.LENGTH_SHORT).show();
                    }else{
                        myList.add(selectedItem);
                        Toast.makeText(getApplicationContext(),"Added "+sList.getStudentName(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Empty",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        function();
        function2();
        super.onResume();
    }
}