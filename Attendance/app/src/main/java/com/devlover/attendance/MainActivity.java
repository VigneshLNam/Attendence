package com.devlover.attendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.devlover.adapters.ClassAdapter;
import com.devlover.classes.ClassList;
import com.devlover.initsdk.InitAuthSDKCallback;
import com.devlover.initsdk.InitAuthSDKHelper;
import com.devlover.initsdk.LoginHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import us.zoom.sdk.ZoomSDK;

public class MainActivity extends AppCompatActivity implements InitAuthSDKCallback,NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener {
    EditText Class_name,Class_section,Class_teacher,Class_subject;
    ClassAdapter classAdapter;
    ListView cList,cList1;
    DataBaseHelper MyDb;
    ArrayList<ClassList> classList;
    Toolbar toolbar;
    DrawerLayout drawer;
    LinearLayout slayout,Addlayout;
    String USerName,PassWord,UName;
    EditText search;
    int count,count1,count2 = 0;
    NavigationView vw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navlayout);
        InitAuthSDKHelper.getInstance().initSDK(this, this);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        USerName = sharedPreferences.getString("username","no");
        PassWord = sharedPreferences.getString("pword","no");
        UName = sharedPreferences.getString("uname","Error");
        vw = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,drawer,toolbar,R.string.open_drawer,R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View header = vw.getHeaderView(0);
        TextView tv = (TextView) header.findViewById(R.id.head);
        tv.setText(UName);
        cList = findViewById(R.id.ClassList);
        cList1 = findViewById(R.id.ClassList1);
        toolbar = findViewById(R.id.toolbar);
        slayout = findViewById(R.id.slayout);
        Addlayout = findViewById(R.id.Addlayout);
        search = (EditText)findViewById(R.id.search);
        Class_name = (EditText)findViewById(R.id.Class_name);
        Class_section = (EditText)findViewById(R.id.Class_section);
        Class_teacher = (EditText)findViewById(R.id.Class_teacher);
        Class_subject = (EditText)findViewById(R.id.Class_subject);
        Button Class_add = findViewById(R.id.Class_add);
        toolbar.inflateMenu(R.menu.tbar);
        toolbar.setOnMenuItemClickListener(this);
        MyDb = new DataBaseHelper(MainActivity.this);
        Class_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(Class_name.getText().toString()) & !TextUtils.isEmpty(Class_section.getText().toString()) & !TextUtils.isEmpty(Class_teacher.getText().toString()) & !TextUtils.isEmpty(Class_subject.getText().toString()) ){
                    if(MyDb.insert(Class_name.getText().toString(),Class_section.getText().toString(),Class_teacher.getText().toString(),Class_subject.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Sucess",Toast.LENGTH_LONG).show();
                        Addlayout.setVisibility(View.GONE);
                        count1 += 1;
                        Class_name.setText("");
                        Class_section.setText("");
                        Class_teacher.setText("");
                        Class_subject.setText("");
                        function();
                    }else{
                        Toast.makeText(getApplicationContext(),"Class & Section Exists Already!!",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Fields Cannot Be Empty!!!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
        function();

        if (vw != null) {
            vw.setNavigationItemSelectedListener(this);
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                classAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void function2(){
        final ArrayList my = ClassAdapter.classList;
        if(!ClassAdapter.classListNames.isEmpty() & !my.isEmpty()) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_view,R.id.simpleTview, ClassAdapter.classListNames);
            cList1.setAdapter(arrayAdapter);
            cList1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent report = new Intent(MainActivity.this,ClassReport.class);
                    report.putExtra("SL_No",(Integer) my.get(position));
                    String string = (String) ClassAdapter.classListNames.get(position);
                    report.putExtra("Class",string);
                    startActivity(report);
                }
            });
        }
    }

    public void function(){
        classList = MyDb.Class();
        if(!classList.isEmpty()){
            classAdapter = new ClassAdapter(MainActivity.this,classList);
            cList.setAdapter(classAdapter);
            cList.setTextFilterEnabled(true);
            cList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ClassList cllist = classList.get(position);
                    Intent intent = new Intent(MainActivity.this,StudentActivity.class);
                    intent.putExtra("Sl_No",cllist.getSl_No());
                    intent.putExtra("Class",cllist.getClassName() + " " + cllist.getSection());
                    startActivity(intent);
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Empty",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        function();
        super.onResume();
    }

    @Override
    protected void onPause() {
        function();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        function();
        super.onRestart();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case (R.id.zoomLogout):
                if(ZoomSDK.getInstance().isInitialized()) {
                    ZoomSDK zoomSDK = ZoomSDK.getInstance();
                    zoomSDK.logoutZoom();
                    SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("check_log", "false");
                    editor.putString("username", "");
                    editor.putString("pword", "");
                    editor.putString("uname", "");
                    editor.commit();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"User Not connected to Internet",Toast.LENGTH_LONG).show();
                    SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("check_log", "false");
                    editor.putString("username", "");
                    editor.putString("pword", "");
                    editor.putString("uname", "");
                    editor.commit();
                    finish();
                }
                break;
            case (R.id.zoomSchedule):
                Intent intent = new Intent(MainActivity.this,ScheduleActivity.class);
                startActivity(intent);
                break;
            case (R.id.zoomScheduled):
                Intent intent2 = new Intent(MainActivity.this,ScheduledActivity.class);
                startActivity(intent2);
                break;
            case (R.id.montReport):
                Intent montReport = new Intent(MainActivity.this,MonthlyReport.class);
                startActivity(montReport);
                break;
            case(R.id.timeTable):
                Intent intent1 = new Intent(MainActivity.this,TimetableActivity.class);
                startActivity(intent1);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

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
            case (R.id.addclass):
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
            case (R.id.report):
                if(count2%2==0){
                    function2();
                    cList.setVisibility(View.GONE);
                    cList1.setVisibility(View.VISIBLE);
                }else{
                    cList.setVisibility(View.VISIBLE);
                    cList1.setVisibility(View.GONE);
                }
                count2+=1;
                break;
        }
        return false;
    }

    @Override
    public void onZoomSDKInitializeResult(int i, int i1) {
        if(i==0){
            ZoomSDK zoomSDK = ZoomSDK.getInstance();
            int r = LoginHelper.getInstance().login(USerName,PassWord);
            Toast.makeText(getApplicationContext(),r+"  ",Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(),i+"  "+i1,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onZoomAuthIdentityExpired() {

    }
}