package com.devlover.attendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devlover.initsdk.InitAuthSDKCallback;
import com.devlover.initsdk.InitAuthSDKHelper;
import com.devlover.initsdk.LoginHelper;

import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;

public class LoginActivity extends AppCompatActivity implements InitAuthSDKCallback, ZoomSDKAuthenticationListener {
    private ZoomSDK mZoomSDK;
    EditText Uname,Pname;
    Button Login;
    ProgressBar PBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PBar = findViewById(R.id.PBar);
        mZoomSDK = ZoomSDK.getInstance();
        mZoomSDK.addAuthenticationListener(this);
        if(!mZoomSDK.isInitialized()){
            InitAuthSDKHelper.getInstance().initSDK(this, this);
        }
        Uname = findViewById(R.id.Uname);
        Pname = findViewById(R.id.Pname);
        Login = findViewById(R.id.login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(Uname.getText().toString()) & !TextUtils.isEmpty(Pname.getText().toString())){
                    login(Uname.getText().toString(),Pname.getText().toString());
                }
            }
        });

    }

    private void login(String toString, String toString1) {
        int login = LoginHelper.getInstance().login(toString,toString1);
        if(!(login== ZoomApiError.ZOOM_API_ERROR_SUCCESS)) {
            if (login == ZoomApiError.ZOOM_API_ERROR_EMAIL_LOGIN_IS_DISABLED) {
                Toast.makeText(getApplicationContext(), "Account had disable email login ", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
            }
        }else{
            PBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onZoomSDKInitializeResult(int i, int i1) {

    }

    @Override
    public void onZoomSDKLoginResult(long l) {
        PBar.setVisibility(View.GONE);
        switch (String.valueOf(l)){
            case "0":
                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("check_log", "true");
                editor.putString("username", Uname.getText().toString());
                editor.putString("pword", Pname.getText().toString());
                editor.putString("uname", LoginHelper.getInstance().UserName());
                editor.commit();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                break;
            case "3":
                Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
                break;
            case "2":
                Toast.makeText(getApplicationContext(),"User Not Exist",Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getApplicationContext(),"User May be Disabled",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onZoomSDKLogoutResult(long l) {

    }

    @Override
    public void onZoomIdentityExpired() {

    }

    @Override
    public void onZoomAuthIdentityExpired() {

    }

    @Override
    protected void onResume() {
        if(mZoomSDK.isLoggedIn()){
            Toast.makeText(getApplicationContext(), "Already Done", Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }
}