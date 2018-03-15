package edu.mdc.entec.north.indoornavigator.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import edu.mdc.entec.north.indoornavigator.BluetoothHandler;
import edu.mdc.entec.north.indoornavigator.DeviceState;
import edu.mdc.entec.north.indoornavigator.R;
import edu.mdc.entec.north.indoornavigator.SensorHandler;
import edu.mdc.entec.north.indoornavigator.model.db.DatabaseHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String mTitle;

    private DeviceState deviceState;
    private SensorHandler sensorHandler;
    private BluetoothHandler bluetoothHandler;


    private DatabaseHandler db;

    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public static final int REQUEST_ENABLE_BT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo);
        mTitle = getString(R.string.app_name);
        getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setSubtitle(R.string.subtitle);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Fragment fragment = new ContactsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, "contacts")
                .addToBackStack("contacts")
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "on resuming");


        //Init database
        db = new DatabaseHandler(this);
        db.createDataBase();
        db.openDataBase();


        //Init deviceState
        deviceState = new DeviceState(this);

        //init Bluetooth
        bluetoothHandler = new BluetoothHandler(this, deviceState);

        //Init Sensors
        sensorHandler = new SensorHandler(this, deviceState);

    }

    @Override
    public void onDestroy() {
        if(db != null)
            db.close();
        super.onDestroy();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            mTitle = getString(R.string.title_debug);
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(mTitle);
            Fragment fragment = new DebugFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, "debug")
                    .addToBackStack("debug")
                    .commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String tag = null;
        switch (id) {
            case R.id.nav_navigation:
                mTitle = getString(R.string.title_navigation);
                fragment = new NavigationFragment();
                tag = "navigation";
                break;
            case R.id.nav_contacts:
                mTitle = getString(R.string.title_contacts);
                fragment = new ContactsFragment();
                tag = "contacts";
                break;
            case R.id.nav_debug:
                mTitle = getString(R.string.title_debug);
                fragment = new DebugFragment();
                tag = "debug";
                break;
            case R.id.nav_share:
                mTitle = getString(R.string.title_share);
                //TODO
                break;
            case R.id.nav_send:
                mTitle = getString(R.string.title_send);
                //TODO
                break;
            default:
                mTitle = getString(R.string.title_contacts);
                fragment = new ContactsFragment();
                tag = "contacts";
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(mTitle);

        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .addToBackStack(tag)
                    .commit();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                bluetoothHandler.onRequestPermissionsResult(permissions, grantResults);
                break;
            default:

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                bluetoothHandler.onEnableBluetoothResult(resultCode, data);
                break;
            default:
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public DeviceState getDeviceState() {
        return deviceState;
    }

    public SensorHandler getSensorHandler() {
        return sensorHandler;
    }

    public BluetoothHandler getBluetoothHandler() {
        return bluetoothHandler;
    }

    public DatabaseHandler getDb() {
        return db;
    }
}
