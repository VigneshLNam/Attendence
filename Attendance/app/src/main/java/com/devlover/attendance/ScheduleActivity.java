package com.devlover.attendance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devlover.initsdk.InitAuthSDKCallback;
import com.devlover.initsdk.InitAuthSDKHelper;
import com.devlover.initsdk.LoginHelper;
import com.devlover.classes.TableClass;
import com.devlover.adapters.TableAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import us.zoom.sdk.AccountService;
import us.zoom.sdk.MeetingItem;
import us.zoom.sdk.PreMeetingError;
import us.zoom.sdk.PreMeetingService;
import us.zoom.sdk.PreMeetingServiceListener;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;

public class ScheduleActivity extends AppCompatActivity implements InitAuthSDKCallback, ZoomSDKAuthenticationListener, PreMeetingServiceListener {
    private ZoomSDK zoomSDK;
    String USerName,PassWord;
    private AccountService mAccoutnService;
    private PreMeetingService mPreMeetingService = null;
    DataBaseHelper MyDb;
    String Day,Timestring;
    TableAdapter tableAdapter;
    ListView scheduleList;
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        scheduleList = findViewById(R.id.scheduleList);
        USerName = sharedPreferences.getString("username","no");
        PassWord = sharedPreferences.getString("pword","no");
        zoomSDK = ZoomSDK.getInstance();
        MyDb = new DataBaseHelper(ScheduleActivity.this);
        InitAuthSDKHelper.getInstance().initSDK(this, this);
        Log.d("check",USerName+" "+PassWord);
        final String[] day = new String[]{"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        Day = day[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1];
        if(!zoomSDK.isLoggedIn()) {
            int login = LoginHelper.getInstance().login(USerName, PassWord);
            if(login==1){
                Toast.makeText(getApplicationContext(),"Please turn on your Internet Connection",Toast.LENGTH_SHORT).show();
            }else {
                mAccoutnService = zoomSDK.getAccountService();
                mPreMeetingService = zoomSDK.getPreMeetingService();
                if (mAccoutnService == null || mPreMeetingService == null) {
                    finish();
                }else {
                    getScheduleTable();
                }
                Log.d("check", login + "Details");
            }
        }else{
            mAccoutnService = zoomSDK.getAccountService();
            mPreMeetingService = zoomSDK.getPreMeetingService();
            Log.d("check",mAccoutnService.getAccountName());
            getScheduleTable();
        }
    }

    private void getScheduleTable() {
        final ArrayList<TableClass> myList = MyDb.scheduleTimetable(Day);
        if(!myList.isEmpty()){
            tableAdapter = new TableAdapter(getApplicationContext(),myList);
            scheduleList.setAdapter(tableAdapter);
            scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TableClass list = myList.get(position);
                    String Class = "English "+MyDb.getClassName(list.getSl_No());
                    SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                    LocalDateTime now = LocalDateTime.now();
                    Timestring = df.format(now)+" "+list.getTime()+":00";
                    try {
                        Date today = (Date)dtf.parse(Timestring);
                        ScheduleMeeting(Class,today);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void ScheduleMeeting(String className,Date classTime) {
        MeetingItem meetingItem = mPreMeetingService.createScheduleMeetingItem();
        meetingItem.setStartTime(classTime.getTime());
        meetingItem.setDurationInMinutes(40);
        meetingItem.setMeetingTopic(className);
        meetingItem.setUsePmiAsMeetingID(true);
        meetingItem.setPassword("Eng");
        meetingItem.setHostVideoOff(true);
        meetingItem.setAttendeeVideoOff(true);
        meetingItem.setAudioType(MeetingItem.AudioType.AUDIO_TYPE_VOIP);
        meetingItem.setCanJoinBeforeHost(false);
        meetingItem.setEnableWaitingRoom(true);
        meetingItem.setTimeZoneId(TimeZone.getDefault().getID());

        if(mPreMeetingService!=null){
            mPreMeetingService.addListener(this);
            PreMeetingService.ScheduleOrEditMeetingError error = mPreMeetingService.scheduleMeeting(meetingItem);
            if (error == PreMeetingService.ScheduleOrEditMeetingError.SUCCESS) {
                Toast.makeText(getApplicationContext(),"Scheduled",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"Error code = "+error,Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onListMeeting(int i, List<Long> list) {

    }

    @Override
    protected void onStop() {
        if(mPreMeetingService!=null){
            mPreMeetingService.removeListener(this);
        }
        super.onStop();
    }

    @Override
    public void onScheduleMeeting(int i, long l) {
        if (i == PreMeetingError.PreMeetingError_Success) {
            MeetingItem meetingItem = mPreMeetingService.getMeetingItemByUniqueId(l);
            String Class = meetingItem.getMeetingTopic();
            String Meeting_ID = String.valueOf(meetingItem.getMeetingNumber());
            String Password = meetingItem.getPassword();
            if(MyDb.insertMeetings(Class,Timestring,Meeting_ID,Password)) {
                Toast.makeText(this, "Schedule successfully. Meeting's unique id is " + l, Toast.LENGTH_LONG).show();
                finish();
            }else {
                Toast.makeText(this, "DB ERROR", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Schedule failed result code =" + i, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpdateMeeting(int i, long l) {

    }

    @Override
    public void onDeleteMeeting(int i) {

    }

    @Override
    public void onZoomSDKLoginResult(long l) {
        if(l == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            Toast.makeText(ScheduleActivity.this, "Logged IN", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onZoomSDKLogoutResult(long l) {

    }

    @Override
    public void onZoomIdentityExpired() {

    }

    @Override
    public void onZoomSDKInitializeResult(int i, int i1) {

    }

    @Override
    public void onZoomAuthIdentityExpired() {

    }
}