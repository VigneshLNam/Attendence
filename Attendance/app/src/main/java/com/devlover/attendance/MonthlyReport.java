package com.devlover.attendance;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MonthlyReport extends AppCompatActivity {
    String[] Months;
    String PrevMonth;
    Spinner dayPicker;
    SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
    DataBaseHelper MyDb;
    ListView MonthlyRep;
    ArrayList<String> Text;
    ArrayList<Integer> classes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);
        Text = new ArrayList<>();
        dayPicker = findViewById(R.id.Day);
        MonthlyRep = findViewById(R.id.monReportList);
        MyDb = new DataBaseHelper(MonthlyReport.this);
        classes = MyDb.getClasses();
        Months = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayPicker.setAdapter(adapter);
        dayPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PrevMonth = position!=0 ? Months[position-1] : Months[11];
                GetDates(PrevMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void GetDates(String prevMonth) {
        int Year = Calendar.getInstance().get(Calendar.YEAR);
        if((prevMonth.equals("Jan")|prevMonth.equals("Mar")|prevMonth.equals("May")|prevMonth.equals("Jul")|prevMonth.equals("Aug")|prevMonth.equals("Oct"))){
            Log.d("check","01-"+prevMonth+"-"+Year+" Last : "+"31-"+prevMonth+"-"+Year);
            try {
                getCount(getDates("01-"+prevMonth+"-"+Year,"31-"+prevMonth+"-"+Year));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if((prevMonth.equals("Apr")|prevMonth.equals("Jun")|prevMonth.equals("Sep")|prevMonth.equals("Nov"))){
            Log.d("check","01-"+prevMonth+"-"+Year+" Last : "+"30-"+prevMonth+"-"+Year);
            try {
                getCount(getDates("01-"+prevMonth+"-"+Year,"30-"+prevMonth+"-"+Year));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if(prevMonth.equals("Dec")){
            Log.d("check","01-"+prevMonth+"-"+(Year-1)+" Last : "+"31-"+prevMonth+"-"+(Year-1));
            try {
                getCount(getDates("01-"+prevMonth+"-"+(Year-1),"31-"+prevMonth+"-"+(Year-1)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            if(Year%4==0){
                Log.d("check","01-"+prevMonth+"-"+Year+" Last : "+"29-"+prevMonth+"-"+Year);
                try {
                    getCount(getDates("01-"+prevMonth+"-"+Year,"29-"+prevMonth+"-"+Year));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                Log.d("check","01-"+prevMonth+"-"+Year+" Last : "+"28-"+prevMonth+"-"+Year);
                try {
                    getCount(getDates("01-"+prevMonth+"-"+Year,"28-"+prevMonth+"-"+Year));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getCount(ArrayList<String> dates){
        String result = "";
        for(String j : dates) {
            for (int i : classes) {
                ArrayList<String> MyList = MyDb.classReport(i, j);
                if (!MyList.isEmpty()) {
                    String className = MyDb.getClassName(i);
                    result+=className+" "+j+" "+MyList.size()+"\n";
                }
            }
        }
        Text.add(result);
        if(!Text.isEmpty()) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_view2,R.id.simpleTview, Text);
            MonthlyRep.setAdapter(arrayAdapter);
        }
    }

    private ArrayList<String> getDates(String dateString1, String dateString2) throws ParseException {
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