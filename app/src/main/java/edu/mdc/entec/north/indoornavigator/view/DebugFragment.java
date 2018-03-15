package edu.mdc.entec.north.indoornavigator.view;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import edu.mdc.entec.north.indoornavigator.DeviceLocationObserver;
import edu.mdc.entec.north.indoornavigator.DeviceSensorObserver;
import edu.mdc.entec.north.indoornavigator.DeviceState;
import edu.mdc.entec.north.indoornavigator.R;
import edu.mdc.entec.north.indoornavigator.SensorHandler;
import edu.mdc.entec.north.indoornavigator.BluetoothHandler;


public class DebugFragment extends Fragment
        implements DeviceSensorObserver, DeviceLocationObserver {

    private static final String TAG = DebugFragment.class.getSimpleName();

    //UI components
    private Button scanButton;
    private TextView beacon11TextView;
    private TextView beacon12TextView;
    private TextView beacon21TextView;
    private TextView beacon22TextView;
    private TextView beacon31TextView;
    private TextView beacon32TextView;
    private TextView accelerationXTextView;
    private TextView accelerationYTextView;
    private TextView accelerationZTextView;
    private TextView rotationXTextView;
    private TextView rotationYTextView;
    private TextView rotationZTextView;
    private TextView magneticFieldXTextView;
    private TextView magneticFieldYTextView;
    private TextView magneticFieldZTextView;
    private TextView XTextView;
    private TextView YTextView;
    private TextView roomTextView;
    private ProgressBar progressBar;

    private DeviceState deviceState;
    private SensorHandler sensorHandler;
    private BluetoothHandler bluetoothHandler;

    //App state
    private boolean isScanning;

    public DebugFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init app state
        isScanning = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        deviceState = ((MainActivity) context).getDeviceState();
        deviceState.registerDeviceSensorObserver(this);
        deviceState.registerDeviceLocationObserver(this);
        sensorHandler = ((MainActivity) context).getSensorHandler();
        bluetoothHandler = ((MainActivity) context).getBluetoothHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_debug, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set up UI components
        scanButton = (Button) getView().findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isScanning) {
                    doScan(true);

                } else {
                    doScan(false);
                }
            }
        });
        XTextView = (TextView) getView().findViewById(R.id.XTextView);
        YTextView = (TextView) getView().findViewById(R.id.YTextView);
        roomTextView = (TextView) getView().findViewById(R.id.roomTextView);
        beacon11TextView = (TextView) getView().findViewById(R.id.beacon11TextView);
        beacon12TextView = (TextView) getView().findViewById(R.id.beacon12TextView);
        beacon21TextView = (TextView) getView().findViewById(R.id.beacon21TextView);
        beacon22TextView = (TextView) getView().findViewById(R.id.beacon22TextView);
        beacon31TextView = (TextView) getView().findViewById(R.id.beacon31TextView);
        beacon32TextView = (TextView) getView().findViewById(R.id.beacon32TextView);
        accelerationXTextView = (TextView) getView().findViewById(R.id.accelerationXTextView);
        accelerationYTextView = (TextView) getView().findViewById(R.id.accelerationYTextView);
        accelerationZTextView = (TextView) getView().findViewById(R.id.accelerationZTextView);
        rotationXTextView = (TextView) getView().findViewById(R.id.rotationXTextView);
        rotationYTextView = (TextView) getView().findViewById(R.id.rotationYTextView);
        rotationZTextView = (TextView) getView().findViewById(R.id.rotationZTextView);
        magneticFieldXTextView = (TextView) getView().findViewById(R.id.magneticFieldXTextView);
        magneticFieldYTextView = (TextView) getView().findViewById(R.id.magneticFieldYTextView);
        magneticFieldZTextView = (TextView) getView().findViewById(R.id.magneticFieldZTextView);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "on resuming");

        XTextView.setText(R.string.undetermined);
        YTextView.setText(R.string.undetermined);
        roomTextView.setText(R.string.undetermined);
        beacon11TextView.setText(R.string.not_detected);
        beacon12TextView.setText(R.string.not_detected);
        beacon21TextView.setText(R.string.not_detected);
        beacon22TextView.setText(R.string.not_detected);
        beacon31TextView.setText(R.string.not_detected);
        beacon32TextView.setText(R.string.not_detected);
        accelerationXTextView.setText(R.string.not_measured);
        accelerationYTextView.setText(R.string.not_measured);
        accelerationZTextView.setText(R.string.not_measured);
        rotationXTextView.setText(R.string.not_measured);
        rotationYTextView.setText(R.string.not_measured);
        rotationZTextView.setText(R.string.not_measured);
        magneticFieldXTextView.setText(R.string.not_measured);
        magneticFieldYTextView.setText(R.string.not_measured);
        magneticFieldZTextView.setText(R.string.not_measured);
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onPause() {
        super.onPause();
        doScan(false);

    }

    private void doScan(boolean enable) {
        if (enable) {
            isScanning = true;
            scanButton.setText(R.string.stop_scan_message);
            progressBar.setVisibility(View.VISIBLE);

        } else {
            isScanning = false;
            scanButton.setText(R.string.scan_message);
            progressBar.setVisibility(View.INVISIBLE);

        }
        bluetoothHandler.scanLeDevice(enable);
        sensorHandler.doScan(enable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void updateLocation() {
        beacon11TextView.setText(String.valueOf(deviceState.getBeacon11RSSI()));
        beacon12TextView.setText(String.valueOf(deviceState.getBeacon12RSSI()));
        beacon21TextView.setText(String.valueOf(deviceState.getBeacon21RSSI()));
        beacon22TextView.setText(String.valueOf(deviceState.getBeacon22RSSI()));
        beacon31TextView.setText(String.valueOf(deviceState.getBeacon31RSSI()));
        beacon32TextView.setText(String.valueOf(deviceState.getBeacon32RSSI()));
        if (deviceState.getCellCoordinates() != null) {
            XTextView.setText(String.valueOf(deviceState.getCellCoordinates().getX()));
            YTextView.setText(String.valueOf(deviceState.getCellCoordinates().getY()));

            if (deviceState.getRoom() != null) {
                roomTextView.setText(String.valueOf(deviceState.getRoom().getRoomNumber()));
            } else {
                roomTextView.setText(R.string.undetermined);
            }

        } else {
            XTextView.setText(R.string.undetermined);
            YTextView.setText(R.string.undetermined);
        }
    }

    @Override
    public void updateSensedState() {
        accelerationXTextView.setText(String.valueOf(deviceState.getXAcceleration()));
        accelerationYTextView.setText(String.valueOf(deviceState.getYAcceleration()));
        accelerationZTextView.setText(String.valueOf(deviceState.getZAcceleration()));

        rotationXTextView.setText(String.valueOf(deviceState.getXRotation()));
        rotationYTextView.setText(String.valueOf(deviceState.getYRotation()));
        rotationZTextView.setText(String.valueOf(deviceState.getZRotation()));

        magneticFieldXTextView.setText(String.valueOf(deviceState.getXMagneticField()));
        magneticFieldYTextView.setText(String.valueOf(deviceState.getYMagneticField()));
        magneticFieldZTextView.setText(String.valueOf(deviceState.getZMagneticField()));

    }
}
