package com.devlover.attendance;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.TimeZone;

public class ScheduledActivity extends AppCompatActivity {
    DataBaseHelper MyDb;
    ListView ScheduledListView;
    ArrayList<String> invites;
    String USerName;
    ArrayList<ArrayList> invite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled);
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        USerName = sharedPreferences.getString("uname","no");
        ScheduledListView = findViewById(R.id.ScheduledListView);
        MyDb = new DataBaseHelper(ScheduledActivity.this);
        function();
    }

    private void function(){
        invites = new ArrayList<>();
        String[] link = {"Topic","Time","Meeting ID","Passcode"};
        try {
            invite = MyDb.getScheduledmeeting();
            if(!invite.isEmpty()){
                for(ArrayList<String> index:invite){
                    String Text = USerName+" is inviting you to a scheduled Zoom Meeting."+"\n\n";
                    for(int i=0;i<4;i++){
                        Text = Text+link[i]+" : "+index.get(i)+"\n";
                    }
                    Text = Text+"\n"+ TimeZone.getDefault().getID()+"\n\n"+"Join Zoom Meeting\n"+"https://us04web.zoom.us/j/"+index.get(2)+"?pwd=eUg3YUt0MHNTSTdGNXFxeHdVM2R0dz09";
                    Log.d("check",Text);
                    invites.add(Text);
                    adapter();
                }
            }else {
                Toast.makeText(getApplicationContext(),"Empty List",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Please Schedule a Meeting",Toast.LENGTH_SHORT).show();
        }
    }

    private void adapter(){
        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this, R.layout.simple_view1,R.id.simpleTview, invites);
        ScheduledListView.setAdapter(Adapter);
        ScheduledListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder Alert = new AlertDialog.Builder(ScheduledActivity.this);
                Alert.setMessage("Do you want to...").setNegativeButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = invites.get(position);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.setPackage("com.whatsapp");
                        intent.putExtra(Intent.EXTRA_TEXT,text);
                        try {
                            startActivity(intent);
                        }catch (ActivityNotFoundException e){
                            Toast.makeText(getApplicationContext(),"Whatsapp is not installed in this Device",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<String> get = invite.get(position);
                        if(MyDb.deleteMeetings(get.get(0))){
                            invites.clear();
                            function();
                        }else {
                            Toast.makeText(getApplicationContext(),"Could not delete",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog alert = Alert.create();
                alert.show();
            }
        });
    }
}