package com.devlover.attendance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassReport extends AppCompatActivity {
    int Sl_No;
    String Date = null;
    String Text = "Empty";
    String string;
    EditText date,date2;
    Button BatchReport,searReport;
    ListView report;
    DatePickerDialog picker;
    DataBaseHelper MyDb;
    List<String> dateL;
    ArrayList<String> list,list1;
    LinearLayout LLayot;
    Toolbar toolbar;
    SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_report);
        Intent intent = getIntent();
        list = new ArrayList<>();
        list1 = new ArrayList<>();
        Sl_No = intent.getIntExtra("SL_No",0);
        string = intent.getStringExtra("Class");
        MyDb = new DataBaseHelper(ClassReport.this);
        Toast.makeText(getApplicationContext(),String.valueOf(Sl_No),Toast.LENGTH_SHORT).show();
        date = (EditText)findViewById(R.id.Cal);
        date2 = (EditText)findViewById(R.id.Cal1);
        BatchReport = findViewById(R.id.BatchReport);
        searReport = findViewById(R.id.searReport);
        LLayot = findViewById(R.id.LLayot);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(string);
        report = findViewById(R.id.reportClass);
        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(ClassReport.this, new DatePickerDialog.OnDateSetListener() {
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
        date2.setInputType(InputType.TYPE_NULL);
        date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                picker = new DatePickerDialog(ClassReport.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        java.util.Date da ;
                        try {
                            da = (Date)format1.parse(dayOfMonth + "-" + (month + 1) + "-" + year);
                            date2.setText(df.format(da));
                        } catch (ParseException e) {
                            date2.setText(df.format(dayOfMonth + "-" + (month + 1) + "-" + year));
                            e.printStackTrace();
                        }
                    }
                },year,month,day);
                picker.show();
            }
        });
        searReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(date.getText().toString()) & TextUtils.isEmpty(date2.getText().toString())){
                    Date = date.getText().toString();
                    list1.clear();
                    list.clear();
                    ArrayList<String> MyList = MyDb.classReport(Sl_No,Date);
                    if(!MyList.isEmpty()){
                        Text = Date;
                        Text += "\n";
                        Text += "\n";
                        Text += "Total no of students present = "+MyList.size();
                        Text += "\n";
                        Text += "\n";
                        for (String i : MyList){
                            Text += i;
                            Text += "\n";
                        }
                    }
                    if(!list.contains(Text)) {
                        if(!Text.equals("Empty")) {
                            list1.add(Date);
                            list.add(Text);
                        }
                    }
                    function();
                    LLayot.setVisibility(View.GONE);
                    BatchReport.setVisibility(View.VISIBLE);
                }else if(TextUtils.isEmpty(date.getText().toString()) & !TextUtils.isEmpty(date2.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Please Select From Date",Toast.LENGTH_SHORT).show();
                }else if(!TextUtils.isEmpty(date.getText().toString()) & !TextUtils.isEmpty(date2.getText().toString())){
                    try {
                        list1.clear();
                        list.clear();
                        dateL = getDates(date.getText().toString(),date2.getText().toString());
                        for (String i : dateL){
                            ArrayList<String> MyList = MyDb.classReport(Sl_No,i);
                            if(!MyList.isEmpty()){
                                Text = i;
                                Text += "\n";
                                Text += "\n";
                                Text += "Total no of students present = "+MyList.size();
                                Text += "\n";
                                Text += "\n";
                                for (String j : MyList){
                                    Text += j;
                                    Text += "\n";
                                }
                            }
                            if(!list.contains(Text)) {
                                if(!Text.equals("Empty")) {
                                    list1.add(i);
                                    list.add(Text);
                                }
                            }
                        }
                        function();
                        LLayot.setVisibility(View.GONE);
                        BatchReport.setVisibility(View.VISIBLE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        BatchReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLayot.setVisibility(View.VISIBLE);
                BatchReport.setVisibility(View.GONE);
                list1.clear();
                list.clear();
                date.setText("");
                date2.setText("");
            }
        });
    }

    public void function(){
        if(!list.isEmpty()){
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
            report.setAdapter(arrayAdapter);
            report.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ClassReport.this);
                    builder.setMessage("You Sure you want to delete this Data").setCancelable(false)
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(MyDb.cReport(Sl_No,list1.get(position))){
                                list.remove(position);
                                function();
                            }else{
                                Toast.makeText(getApplicationContext(),list1.get(position),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }
    }

    public void generatePdf(){
        Bitmap bmp;
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(210,297,1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(pageInfo);
        Canvas canvas = myPage.getCanvas();
        pdfDocument.finishPage(myPage);
    }

    private List<String> getDates(String dateString1, String dateString2) throws ParseException {
        ArrayList<String> dates = new ArrayList<>();
        int day,month,year;

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df.parse(dateString1);
            date2 = df.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            day = cal1.get(Calendar.DAY_OF_MONTH);
            month = cal1.get(Calendar.MONTH);
            year = cal1.get(Calendar.YEAR);
            Date da = (Date)format1.parse(day+ "-" + (month + 1) + "-" + year);
            String dis = df.format(da);
            dates.add(dis);
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }
}