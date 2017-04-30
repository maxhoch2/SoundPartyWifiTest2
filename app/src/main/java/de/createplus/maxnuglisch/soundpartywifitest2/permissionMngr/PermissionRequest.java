package de.createplus.maxnuglisch.soundpartywifitest2.permissionMngr;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Max Nuglisch on 19.04.2017.
 */
public abstract class PermissionRequest {
    private String Permission;
    private int PermissionKey;
    private boolean explinationPopUp = false;
    private String explinationText = "";
    private String explinationTitle = "";

    /**
     * @param Permition The Android Permition to request
     */
    public PermissionRequest(String Permition) {
        this.Permission = Permition;
        PermissionKey = 7;
        for (int i = 0; i < Permition.length(); i++) {
            PermissionKey = PermissionKey * 31 + Permition.charAt(i);
        }
        explinationPopUp = false;
    }

    public PermissionRequest(String Permition, String explinationTitle, String explinationText) {
        this.Permission = Permition;
        PermissionKey = 7;
        String tmp = Permition.replace("android.permission.","");
        for (int i = 0; i < tmp.length(); i++) {
            PermissionKey = PermissionKey * 31 + tmp.charAt(i);
        }
        if(PermissionKey < 0)PermissionKey = PermissionKey*(-1);
        while(PermissionKey > 65535)PermissionKey = PermissionKey/10;
        Log.e(TAG,"PermitionKey: "+PermissionKey);
        this.explinationText = explinationText;
        this.explinationTitle = explinationTitle;
        explinationPopUp = true;
    }

    protected void request(Activity CurrentActivity) {
        if (checkForPermition(CurrentActivity)) {
            if (explinationPopUp) {


                final Activity CURRENTACTIVITY = CurrentActivity;
                AlertDialog.Builder builder = new AlertDialog.Builder(CURRENTACTIVITY);
                builder.setTitle(explinationTitle)
                        .setMessage(explinationText)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                showPermissionDialog(CURRENTACTIVITY);
                            }
                        });
                builder.create().show();

            } else {

                showPermissionDialog(CurrentActivity);
            }
        } else {
            onPermissionAccepted();
        }
    }

    public boolean checkForPermition(Activity CurrentActivity){
        return ContextCompat.checkSelfPermission(CurrentActivity, Permission) != PackageManager.PERMISSION_GRANTED;
    }

    public void showPermissionDialog(Activity CurrentActivity) {
        ActivityCompat.requestPermissions(CurrentActivity,
                new String[]{Permission},
                PermissionKey);
    }

    public abstract void onPermissionAccepted();

    public abstract void onPermissionDenied();

    public int getPermissionKey() {
        return PermissionKey;
    }

    public String getPermission() {
        return Permission;
    }

    public void setPermission(String permission) {
        Permission = permission;
    }

    public void setPermissionKey(int permissionKey) {
        PermissionKey = permissionKey;
    }

    public void setExplinationText(String explinationText) {
        this.explinationText = explinationText;
        explinationPopUp = true;
    }

    public void disableExplinationPopUp() {
        explinationPopUp = false;
    }

    public void enableExplinationPopUp() {
        explinationPopUp = false;
    }
}

