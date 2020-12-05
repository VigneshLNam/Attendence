package com.devlover.initsdk;

import us.zoom.sdk.ZoomSDK;

public class LoginHelper {
    private ZoomSDK zoomSDK;
    private static LoginHelper loginHelper;

    private LoginHelper(){
        zoomSDK = ZoomSDK.getInstance();
    }

    public synchronized static LoginHelper getInstance(){
        loginHelper = new LoginHelper();
        return loginHelper;
    }

    public int login(String UserName, String PassWord){
        return zoomSDK.loginWithZoom(UserName,PassWord);
    }

    public String UserName(){
        return zoomSDK.getAccountService().getAccountName();
    }

    public boolean isLoggedIn(){
        return zoomSDK.logoutZoom();
    }

    public boolean logout(){
        return zoomSDK.logoutZoom();
    }

    public int tryAutoLoginZoom() {
        return zoomSDK.tryAutoLoginZoom();
    }
}

