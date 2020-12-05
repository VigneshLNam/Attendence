package com.devlover.attendance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.devlover.classes.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "Register";
    private String ClassTable = "create table Class (Sl_No INTEGER PRIMARY KEY AUTOINCREMENT,class TEXT,Section TEXT,teacherName TEXT,Subject TEXT,UNIQUE(class,Section))";
    private String Students = "create table studentsTable (Sl_No INTEGER,studentNo INTEGER PRIMARY KEY AUTOINCREMENT,studentsName TEXT,FOREIGN KEY(Sl_No) REFERENCES Class(Sl_No))";
    private String Attendance = "create table attendenceTable (studentNo INTEGER,Date TEXT,FOREIGN KEY(studentNo) REFERENCES studentsTable(studentNo),UNIQUE(studentNo,Date))";
    private String timeTable = "create table timeTable (Sl_No INTEGER,Day TEXT,Time TEXT,FOREIGN KEY(Sl_NO) REFERENCES Class(Sl_NO),UNIQUE(Sl_No,Day,Time))";
    private String ScheduledMeetings = "create table scheduletable(topicName TEXT,Date TEXT,Meeting_ID TEXT,Meeting_Password TEXT)";
    Date c = Calendar.getInstance().getTime();
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
    String formattedDate = df.format(c);

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ClassTable);
        db.execSQL(Students);
        db.execSQL(Attendance);
        db.execSQL(timeTable);
        db.execSQL(ScheduledMeetings);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Class");
        db.execSQL("DROP TABLE IF EXISTS studentsTable");
        db.execSQL("DROP TABLE IF EXISTS attendenceTable");
        db.execSQL("DROP TABLE IF EXISTS timeTable");
        db.execSQL("DROP TABLE IF EXISTS scheduletable");
        onCreate(db);
    }

    public boolean deleteMeetings(String topic){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selection = {topic};
        int i = db.delete("scheduletable","topicName = ?",selection);
        if(i>0){
            return true;
        }
        return false;
    }

    public boolean insertMeetings(String topic,String date,String meetingId,String password){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(ScheduledMeetings);
        }catch (Exception e){

        }
        String[] columns = {"Date"};
        String[] selection = {topic};
        Cursor cursor = db.query("scheduletable",columns,"topicName = ?",selection,null,null,null);
        if(cursor.getCount() >1){
            ContentValues cv = new ContentValues();
            cv.put("Date",date);
            int val = db.update("scheduletable",cv,"topicName = ?",selection);
            Log.d("check",val + "status");
            return true;
        }else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("topicName",topic);
            contentValues.put("Date",date);
            contentValues.put("Meeting_ID",meetingId);
            contentValues.put("Meeting_Password",password);
            long val = db.insert("scheduletable",null,contentValues);
            if(!String.valueOf(val).equals("-1")){
                return true;
            }
        }
        return false;
    }

    public ArrayList<ArrayList> getScheduledmeeting(){
        ArrayList<ArrayList> string = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {"topicName","Date","Meeting_ID","Meeting_Password"};
        Cursor cursor = db.query("scheduletable",columns,null,null,null,null,null);
        while (cursor.moveToNext()){
            ArrayList<String> list = new ArrayList<>();
            int i1 = cursor.getColumnIndex("topicName");
            list.add(cursor.getString(i1));
            int i2 = cursor.getColumnIndex("Date");
            list.add(cursor.getString(i2));
            int i3 = cursor.getColumnIndex("Meeting_ID");
            list.add(cursor.getString(i3));
            int i4 = cursor.getColumnIndex("Meeting_Password");
            list.add(cursor.getString(i4));
            string.add(list);
        }
        return string;
    }

    public boolean deletetimeTable(int Sl_No,String Day,String Time){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selection={String.valueOf(Sl_No),Day,Time};
        int i = db.delete("timeTable","Sl_No = ? and Day = ? and Time = ?",selection);
        if(i>0) return true;
        else return false;
    }

    public boolean inserttable(int Sl_No,String Day,String Time){
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.execSQL(timeTable);
        }catch (Exception e){

        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("Sl_No",Sl_No);
        contentValues.put("Day",Day);
        contentValues.put("Time",Time);
        long val = db.insert("timeTable",null,contentValues);
        if(String.valueOf(val).equals("-1")){
            return false;
        }
        return true;
    }

    public boolean insert(String cname,String sec,String tname,String sub){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("class",cname);
        contentValues.put("Section",sec);
        contentValues.put("teacherName",tname);
        contentValues.put("Subject",sub);
        long val = db.insert("Class",null,contentValues);
        if(String.valueOf(val).equals("-1")){
            return false;
        }
        return true;
    }

    public ArrayList<TableClass> scheduleTimetable(String Day){
        ArrayList<TableClass> values = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.execSQL(timeTable);
        }catch (Exception e){

        }
        String[] Selectionargs = {
                Day
        };
        String[] Colums = {
                "Sl_No","Time"
        };
        Cursor cursor = db.query("timeTable",Colums,"Day = ?",Selectionargs,null,null,null);
        while (cursor.moveToNext()){
            TableClass list = new TableClass();
            list.setDay(Day);
            int index = cursor.getColumnIndexOrThrow("Time");
            list.setTime(cursor.getString(index));
            int index1 = cursor.getColumnIndexOrThrow("Sl_No");
            list.setSl_No(Integer.parseInt(cursor.getString(index1)));
            values.add(list);
        }
        return values;
    }

    public String getClassName(int Sl_No){
        String Class = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String[] Selectionargs = {
                String.valueOf(Sl_No)
        };
        String[] Colums = {
                "class"
        };
        Cursor cursor = db.query("Class",Colums,"Sl_No  = ?",Selectionargs,null,null,null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex("class");
            Class = cursor.getString(index);
        }
        return Class;
    }

    public ArrayList<TableClass> getTimetable(int Sl_No){
        ArrayList<TableClass> values = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(timeTable);
        }catch (Exception e){

        }
        String[] Selectionargs = {
                String.valueOf(Sl_No)
        };
        String[] Colums = {
                "Day","Time"
        };
        Cursor cursor = db.query("timeTable",Colums,"Sl_No = ?",Selectionargs,null,null,null);
        while (cursor.moveToNext()){
            TableClass list = new TableClass();
            list.setSl_No(Sl_No);
            int index = cursor.getColumnIndexOrThrow("Day");
            list.setDay(cursor.getString(index));
            int index1 = cursor.getColumnIndexOrThrow("Time");
            list.setTime(cursor.getString(index1));
            values.add(list);
        }
        return values;
    }

    public boolean Attendance(int Stu,String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("studentNo",Stu);
        if(date == null){
            contentValues.put("Date",formattedDate);
        }else{
            contentValues.put("Date",date);
        }
        long val = db.insert("attendenceTable",null,contentValues);
        if(String.valueOf(val).equals("-1")){
            return false;
        }
        return true;
    }

    public boolean insertStudent(int Sl_No ,String sname){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Sl_No",Sl_No);
        contentValues.put("studentsName",sname);
        long val = db.insert("studentsTable",null,contentValues);
        if(String.valueOf(val).equals("-1")){
            return false;
        }
        return true;
    }

    public ArrayList<StudentsList> Students(int Sl_No) {
        ArrayList<StudentsList> passList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                "Sl_No","studentNo","studentsName"
        };
        String[] selection = {
                String.valueOf(Sl_No)
        };
        Cursor cursor = db.query("studentsTable",columns,"Sl_No = ?",selection,null,null,"studentsName");
        while (cursor.moveToNext()){
            StudentsList list = new StudentsList();
            int index = cursor.getColumnIndexOrThrow("Sl_No");
            list.setSl_No(cursor.getInt(index));
            int index1 = cursor.getColumnIndexOrThrow("studentNo");
            list.setStudentNo(cursor.getInt(index1));
            int index2 = cursor.getColumnIndexOrThrow("studentsName");
            list.setStudentName(cursor.getString(index2));
            passList.add(list);
        }
        return passList;

    }

    public ArrayList<ClassList> Class() {
        ArrayList<ClassList> values = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                "Sl_No","class","Section","teacherName","Subject"
        };
        Cursor cursor = db.query("Class",columns,null,null,null,null,null);
        while (cursor.moveToNext()){
            ClassList list = new ClassList();
            int index = cursor.getColumnIndexOrThrow("Sl_No");
            list.setSl_No(cursor.getInt(index));
            int index1 = cursor.getColumnIndexOrThrow("class");
            list.setClass(cursor.getString(index1));
            int index2 = cursor.getColumnIndexOrThrow("Section");
            list.setSection(cursor.getString(index2));
            int index3 = cursor.getColumnIndexOrThrow("teacherName");
            list.setTeacherName(cursor.getString(index3));
            int index4 = cursor.getColumnIndexOrThrow("Subject");
            list.setSubject(cursor.getString(index4));
            values.add(list);
        }
        return values;
    }

    public int deleteAttendence(int Stu_No){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] where = { String.valueOf(Stu_No) };
        int val = db.delete("studentsTable","studentNo = ?",where);
        return val;
    }

    public boolean deleteAttendence(int Stu_No,String date){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] where = { String.valueOf(Stu_No) ,date};
        db.delete("attendenceTable","studentNo = ? AND Date = ?",where);
        return true;
    }

    public boolean updateName(int Stu_No,String editedName){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] where = { String.valueOf(Stu_No) };
        ContentValues contentValues = new ContentValues();
        contentValues.put("studentsName",editedName);
        int val = db.update("studentsTable",contentValues,"studentNo = ?",where);
        if(val!=0){
            return true;
        }
        return false;
    }

    public ArrayList<Integer> getClasses(){
        ArrayList<Integer> values = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"Sl_No"};
        Cursor cursor = db.query("Class",columns,null,null,null,null,null);
        while (cursor.moveToNext()){
            int index  = cursor.getColumnIndex("Sl_No");
            values.add(cursor.getInt(index));
        }
        return values;
    }


    public ArrayList<String> classReport(int Sl_No,String date){
        ArrayList<String> values = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] Selectionargs = {
          String.valueOf(Sl_No),date
        };
        Cursor cursor = db.rawQuery("select studentsName from studentsTable where Sl_No = ? AND studentNo in (select studentNo from attendenceTable where Date = ?) order by studentsName",Selectionargs);
        while (cursor.moveToNext()){
            int index = cursor.getColumnIndexOrThrow("studentsName");
            values.add(cursor.getString(index));
        }
        return values;
    }

    public boolean cReport(int Sl_No,String date){
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        String[] Selectionargs = {
                String.valueOf(Sl_No),date
        };
        Cursor cursor = db.rawQuery("delete from attendenceTable where studentNo in (select studentNo from studentsTable where Sl_No = ?) and Date = ?",Selectionargs);
        while(cursor.moveToNext()){
            count += 1;
        }
        return true;
    }

    public ArrayList<String> StudReport(int No) {
        ArrayList<String> values = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                "Date"
        };
        String[] selection = {
                String.valueOf(No)
        };
        Cursor cursor = db.query("attendenceTable",columns,"studentNo = ?",selection,null,null,"Date");
        while (cursor.moveToNext()){
            int index = cursor.getColumnIndexOrThrow("Date");
            values.add(cursor.getString(index));
        }
        return values;
    }
}
