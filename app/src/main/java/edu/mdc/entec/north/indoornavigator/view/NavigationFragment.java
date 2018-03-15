package edu.mdc.entec.north.indoornavigator.view;


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import edu.mdc.entec.north.indoornavigator.BluetoothHandler;
import edu.mdc.entec.north.indoornavigator.DeviceLocationObserver;
import edu.mdc.entec.north.indoornavigator.DeviceSensorObserver;
import edu.mdc.entec.north.indoornavigator.DeviceState;
import edu.mdc.entec.north.indoornavigator.R;
import edu.mdc.entec.north.indoornavigator.SensorHandler;


public class NavigationFragment extends Fragment
        implements DeviceLocationObserver {
    private static final String TAG = NavigationFragment.class.getSimpleName();


    //UI components
    private Button scanButton;
    private TextView XTextView;
    private TextView YTextView;
    private TextView roomTextView;
    private ProgressBar progressBar;
    private ImageView mapImageView;
    private Drawable marker;

    private static final int numCellsX = 50;
    private static final int numCellsY = 30;
    private static final int mapWidth = 652;
    private static final int mapHeight = 266;
    private static final int mapOffsetX = 26;
    private static final int mapOffsetY = 90;

    private DeviceState deviceState;
    private SensorHandler sensorHandler;
    private BluetoothHandler bluetoothHandler;

    //App state
    private boolean isScanning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init app state
        isScanning = false;
    }


    public NavigationFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        deviceState = ((MainActivity) context).getDeviceState();
        deviceState.registerDeviceLocationObserver(this);
        sensorHandler = ((MainActivity) context).getSensorHandler();
        bluetoothHandler = ((MainActivity) context).getBluetoothHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
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
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        mapImageView = (ImageView) getView().findViewById(R.id.mapImageView);

        //Make a new marker drawable
        marker = ContextCompat.getDrawable(getActivity(), R.drawable.pin);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "on resuming");

        XTextView.setText(R.string.undetermined);
        YTextView.setText(R.string.undetermined);
        roomTextView.setText(R.string.undetermined);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        doScan(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
    public void updateLocation() {
        if (deviceState.getCellCoordinates() != null) {
            XTextView.setText(String.valueOf(deviceState.getCellCoordinates().getX()));
            YTextView.setText(String.valueOf(deviceState.getCellCoordinates().getY()));

            if (deviceState.getRoom() != null) {
                roomTextView.setText(String.valueOf(deviceState.getRoom().getRoomNumber()));
            } else {
                roomTextView.setText(R.string.undetermined);
            }
            addMarker(deviceState.getCellCoordinates().getX(), deviceState.getCellCoordinates().getY());
        } else {
            XTextView.setText(R.string.undetermined);
            YTextView.setText(R.string.undetermined);
        }
    }

    private void addMarker(int X, int Y) {
        mapImageView.getOverlay().remove(marker);
        Rect markerBounds = new Rect(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        double cellWidth = mapWidth / (double) numCellsX;
        markerBounds.offset(X * mapWidth / numCellsX + mapOffsetX - (markerBounds.width() / 2),
                Y * mapHeight / numCellsY + mapOffsetY - markerBounds.height());//translate
        //markerBounds.offset( X + mapOffsetX - (markerBounds.width() /2),
        // Y + mapOffsetY - markerBounds.height());//translate
        marker.setBounds(markerBounds);
        mapImageView.getOverlay().add(marker);
    }

}
