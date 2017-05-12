package de.createplus.maxnuglisch.soundpartywifitest2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.io.*;
import java.lang.reflect.Method;
import java.util.LinkedList;


import de.createplus.maxnuglisch.soundpartywifitest2.backgroundservices.ScanNetwork;
import de.createplus.maxnuglisch.soundpartywifitest2.backgroundservices.ScanNetworkReciever;
import de.createplus.maxnuglisch.soundpartywifitest2.permissionMngr.PermissionManager;
import de.createplus.maxnuglisch.soundpartywifitest2.permissionMngr.PermissionRequest;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private PermissionManager permissionManager;
    public static String view = "----";
    public static  boolean isServer = false;
    public void updateContainerContent(String text){
        TextView textbox = (TextView) findViewById(R.id.textbox);
        textbox.setText(text);
        Log.e("HI","ICH BIN HIER ANGEKOMMEN");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        IntentFilter statusIntentFilterPlanData = new IntentFilter(
                ScanNetwork.Constants.BROADCAST_ACTION);

        ScanNetworkReciever ScanNetworkReciever =
                new ScanNetworkReciever(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                ScanNetworkReciever,
                statusIntentFilterPlanData);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClientList2();
                /*Snackbar.make(v, "Divices: "+MainActivity.view, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        permissionManager = new PermissionManager();
        PermissionRequest request = new PermissionRequest(Manifest.permission.WRITE_SETTINGS,"Hotspot","Setup Hotspot") {

            @Override
            public void onPermissionAccepted() {

            }

            @Override
            public void onPermissionDenied() {

            }

            @Override
            public void showPermissionDialog(Activity CurrentActivity){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + CurrentActivity.getPackageName()));
                startActivity(intent);
            }
        };
        PermissionRequest request2 = new PermissionRequest(Manifest.permission.READ_EXTERNAL_STORAGE,"STORAGE","Access Storage") {

            @Override
            public void onPermissionAccepted() {

            }

            @Override
            public void onPermissionDenied() {

            }
        };
        permissionManager.requestPermission(request,this);
        permissionManager.requestPermission(request2,this);
        Button btn = (Button) findViewById(R.id.button);
        btn.setText("Client");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isServer = true;
                Button btn = (Button) findViewById(R.id.button);
                btn.setText("Server");
            }
        });
        //configApState(this);
    }

    public void getClientList2 (){
        Context con = getApplicationContext();
        Intent mServiceIntent = new Intent(con, ScanNetwork.class);
        con.startService(mServiceIntent);
    }
    public LinkedList<String[]> getClientList() {
        LinkedList<String[]> clients = new LinkedList<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");

                //Log.e("HOTSPOT","Address"+ splitted[0]);
                if(splitted[0].matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+")){
                    String[] client = new String[3];
                    client[0] = splitted[0];//IP
                    client[1] = splitted[3];//MAC
                    client[2] = splitted[0];//WIFI
                    clients.add(client);
                }

                }

        } catch(Exception e) {

        }
        return clients;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions,grantResults);
    }



    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    // toggle wifi hotspot on or off
    public static boolean configApState(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = new WifiConfiguration();
        wificonfiguration.SSID = "HI";
        wificonfiguration.preSharedKey = "12345678";
        wificonfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wificonfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

        try {
            // if WiFi is on, turn it off
            if(isApOn(context)) {
                wifimanager.setWifiEnabled(false);
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isApOn(context));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
