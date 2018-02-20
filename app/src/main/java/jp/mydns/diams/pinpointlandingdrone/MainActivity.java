package jp.mydns.diams.pinpointlandingdrone;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import dji.sdk.base.*;
import dji.sdk.products.*;
import jp.mydns.diams.pinpointlandingdrone.Common.PinpointLandingDroneApplication;
import jp.mydns.diams.pinpointlandingdrone.layout.AircraftFragment;
import jp.mydns.diams.pinpointlandingdrone.layout.BeaconFragment;
import jp.mydns.diams.pinpointlandingdrone.layout.SettingFragment;

public class MainActivity extends AppCompatActivity {

    private TextView mConnectStatus_txt;
    private String mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.READ_PHONE_STATE,
                    }
                    , 1);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mConnectStatus_txt = (TextView) findViewById(R.id.main_connectStatus_textView);

        // ここにfragmentの処理を書く
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, new AircraftFragment(), AircraftFragment.class.getName());
            transaction.commit();
            mCurrentFragment = AircraftFragment.class.getName();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings && !mCurrentFragment.equals(SettingFragment.class.getName())) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SettingFragment nextFragment = new SettingFragment();
            transaction.replace(R.id.fragment_container, nextFragment, SettingFragment.class.getName());
            //transaction.addToBackStack(SettingFragment.class.getName());
            transaction.commit();
            mCurrentFragment = SettingFragment.class.getName();
            return true;
        } else if (id == R.id.action_aircraft && !mCurrentFragment.equals(AircraftFragment.class.getName())) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            AircraftFragment nextFragment = new AircraftFragment();
            transaction.replace(R.id.fragment_container, nextFragment, AircraftFragment.class.getName());
            //transaction.addToBackStack(AircraftFragment.class.getName());
            transaction.commit();
            mCurrentFragment = AircraftFragment.class.getName();
            return  true;
        } else if (id == R.id.action_beacon && !mCurrentFragment.equals(BeaconFragment.class.getName())) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BeaconFragment nextFragment = new BeaconFragment();
            transaction.replace(R.id.fragment_container, nextFragment, BeaconFragment.class.getName());
            //transaction.addToBackStack(BeaconFragment.class.getName());
            transaction.commit();
            mCurrentFragment = BeaconFragment.class.getName();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTitleBar();
        }
    };

    public void updateTitleBar() {
        if(mConnectStatus_txt == null) return;
        boolean ret = false;
        BaseProduct product = PinpointLandingDroneApplication.getProductInstance();
        if (product != null) {
            if(product.isConnected()) {
                //The product is connected
                mConnectStatus_txt.setText(PinpointLandingDroneApplication.getProductInstance().getModel() + " Connected");
                ret = true;
            } else {
                if(product instanceof Aircraft) {
                    Aircraft aircraft = (Aircraft)product;
                    if(aircraft.getRemoteController() != null && aircraft.getRemoteController().isConnected()) {
                        // The product is not connected, but the remote controller is connected
                        mConnectStatus_txt.setText("only RC Connected");
                        ret = true;
                    }
                }
            }
        }
        if(!ret) {
            // The product or the remote controller are not connected.
            mConnectStatus_txt.setText("Disconnected");
        }
    }

    @Override
    public void onAttachedToWindow() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PinpointLandingDroneApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
        super.onAttachedToWindow();
    }

    @Override
    protected void onResume() {
        Log.e(MainActivity.class.getName(), "onResume");
        super.onResume();
        updateTitleBar();
    }

    @Override
    protected void onDestroy() {
        Log.e(MainActivity.class.getName(), "onDestroy");
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
