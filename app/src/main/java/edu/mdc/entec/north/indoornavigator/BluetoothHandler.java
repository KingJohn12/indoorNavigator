package edu.mdc.entec.north.indoornavigator;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.mdc.entec.north.indoornavigator.model.CellCoordinates;
import edu.mdc.entec.north.indoornavigator.model.iBeacon;
import edu.mdc.entec.north.indoornavigator.model.iBeaconsMeasurement;
import edu.mdc.entec.north.indoornavigator.utils.SVMClassifier;

import static edu.mdc.entec.north.indoornavigator.view.MainActivity.PERMISSION_REQUEST_COARSE_LOCATION;
import static edu.mdc.entec.north.indoornavigator.view.MainActivity.REQUEST_ENABLE_BT;

public class BluetoothHandler {
    private static final String TAG = BluetoothHandler.class.getSimpleName();

    private Context mContext;
    private DeviceState deviceState;
    private SVMClassifier SVMClassifier;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private int beacon11RSSI;
    private int beacon12RSSI;
    private int beacon21RSSI;
    private int beacon22RSSI;
    private int beacon31RSSI;
    private int beacon32RSSI;


    public BluetoothHandler(final Context context, DeviceState deviceState) {
        mContext = context;
        this.deviceState = deviceState;

        // Create classifier
        SVMClassifier = new SVMClassifier(context);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //Beginning in Android 6.0 (API level 23), users grant permissions
        // to apps while the app is running, not when they install the app.
        //Obtaining dynamic permissions from the user
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // API level >23
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("This app needs location access.");
                builder.setMessage("Please grant location access to this app.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    public void onDismiss(DialogInterface dialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            ((AppCompatActivity) mContext).requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    PERMISSION_REQUEST_COARSE_LOCATION);
                    }



                });// See onRequestPermissionsResult callback method for negative behavior
                builder.show();
            }
        }

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ( (AppCompatActivity) mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            setupBLEScan();
        }
    }

    public void setupBLEScan() {

        //Android 4.3 (JELLY_BEAN_MR2) introduced platform support for Bluetooth Low Energy (Bluetooth LE) in the central role.
        // In Android 5.0 (LOLLIPOP, 21), an Android device can now act as a Bluetooth LE peripheral device. Apps can use this capability to make their presence known to nearby devices.
        // There was a new android.bluetooth.le API!!!
        if (Build.VERSION.SDK_INT >= 21) {//LOLLIPOP
            BluetoothLeScanner mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            List<ScanFilter> filters = new ArrayList<ScanFilter>();
            ScanFilter.Builder mBuilder = new ScanFilter.Builder();
            ByteBuffer mManufacturerData = ByteBuffer.allocate(23);
            ByteBuffer mManufacturerDataMask = ByteBuffer.allocate(24);
            byte[] uuid = getIdAsByte(UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B575555"));
            mManufacturerData.put(0, (byte) 0xBE);
            mManufacturerData.put(1, (byte) 0xAC);
            for (int i = 2; i <= 17; i++) {
                mManufacturerData.put(i, uuid[i - 2]);
            }
            for (int i = 0; i <= 17; i++) {
                mManufacturerDataMask.put((byte) 0x01);
            }
            mBuilder.setManufacturerData(76, mManufacturerData.array(), mManufacturerDataMask.array());
            ScanFilter mScanFilter = mBuilder.build();
            //TODO
            //filters.add(mScanFilter);

        } else { //  18 < Build.VERSION.SDK_INT < 21
            //DO nothing
        }
    }

    public void scanLeDevice(final boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // LOLLIPOP is version 21
            scanLeDevice21(enable);
        } else {
            scanLeDevice18(enable);
        }
    }

    @RequiresApi(21)
    private void scanLeDevice21(final boolean enable) {

        ScanCallback mLeScanCallback = new ScanCallback() {

            @Override
            public void onScanResult(int callbackType, ScanResult result) {

                super.onScanResult(callbackType, result);

                BluetoothDevice bluetoothDevice = result.getDevice();

                ScanRecord mScanRecord = result.getScanRecord();
                int rssi = result.getRssi();
//                        Log.i(TAG, "Address: "+ btDevice.getAddress());
//                        Log.i(TAG, "TX Power Level: " + result.getScanRecord().getTxPowerLevel());
//                        Log.i(TAG, "RSSI in DBm: " + rssi);
//                        Log.i(TAG, "Manufacturer data: "+ mScanRecord.getManufacturerSpecificData());
//                        Log.i(TAG, "device name: "+ mScanRecord.getDeviceName());
//                        Log.i(TAG, "Advertise flag: "+ mScanRecord.getAdvertiseFlags());
//                        Log.i(TAG, "service uuids: "+ mScanRecord.getServiceUuids());
//                        Log.i(TAG, "Service data: "+ mScanRecord.getServiceData());
                byte[] recordBytes = mScanRecord.getBytes();
                iBeacon ib = getIBeacon(recordBytes);
                if (ib != null) {
                    updateRSSI(ib.getUuid(), ib.getMajor(), ib.getMinor(), rssi);
                    CellCoordinates currentCellCoord = SVMClassifier.predict(new iBeaconsMeasurement(beacon11RSSI, beacon12RSSI, beacon21RSSI, beacon22RSSI, beacon31RSSI, beacon32RSSI));
                    deviceState.setCellCoordinates(currentCellCoord);

                }

            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };

        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (enable) {
            mScanning = true;
            bluetoothLeScanner.startScan(mLeScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }


    @RequiresApi(18)
    private void scanLeDevice18(boolean enable) {

        BluetoothAdapter.LeScanCallback mLeScanCallback =
                new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice bluetoothDevice, final int rssi,
                                         final byte[] scanRecord) {
                        ((AppCompatActivity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("onLeScan", bluetoothDevice.toString());

                                iBeacon ib = getIBeacon(scanRecord);

                                if (ib != null) {
                                    updateRSSI(ib.getUuid(), ib.getMajor(), ib.getMinor(), rssi);
                                    CellCoordinates currentCellCoord = SVMClassifier.predict(new iBeaconsMeasurement(beacon11RSSI, beacon12RSSI, beacon21RSSI, beacon22RSSI, beacon31RSSI, beacon32RSSI));
                                    deviceState.setCellCoordinates(currentCellCoord);
                                }
                            }
                        });
                    }
                };

        if (enable) {
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

    public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "coarse location permission granted");
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });
                builder.show();
            }
            return;
        }
    }

    public void onEnableBluetoothResult(int resultCode, Intent data) {
        //If the user does not want to enable Bluetooth, we kill the app
        if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
            ((AppCompatActivity) mContext).finish();
            return; 
        }
        
    }

    public iBeacon getIBeacon(byte[] scanRecord) {
        iBeacon ib = null;
        String record = scanRecord.toString();
        Log.i(TAG, "record: " + record);
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }
        if (patternFound) {
            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            //Here is your UUID
            String uuid = hexString.substring(0, 8) + "-" +
                    hexString.substring(8, 12) + "-" +
                    hexString.substring(12, 16) + "-" +
                    hexString.substring(16, 20) + "-" +
                    hexString.substring(20, 32);

            //Here is your Major value
            int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

            //Here is your Minor value
            int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
            Log.i(TAG, "uuid: " + hexString + ", major: " + major + ", minor: " + minor);

            ib = new iBeacon(uuid, major, minor);

        }
        return ib;

    }

    private void updateRSSI(String uuid, int major, int minor, int rssi) {
        if (uuid.equalsIgnoreCase("B9407F30-F5F8-466E-AFF9-25556B575555")) {
            if (major == 1) {
                if (minor == 1) {

                    beacon11RSSI = rssi;
                    deviceState.setBeacon11RSSI(rssi);
                } else if (minor == 2) {
                    beacon12RSSI = rssi;
                    deviceState.setBeacon12RSSI(rssi);
                }
            } else if (major == 2) {
                if (minor == 1) {
                    beacon21RSSI = rssi;
                    deviceState.setBeacon21RSSI(rssi);
                } else if (minor == 2) {
                    beacon22RSSI = rssi;
                    deviceState.setBeacon22RSSI(rssi);
                }
            } else if (major == 3) {
                if (minor == 1) {
                    beacon31RSSI = rssi;
                    deviceState.setBeacon31RSSI(rssi);
                } else if (minor == 2) {
                    beacon32RSSI = rssi;
                    deviceState.setBeacon32RSSI(rssi);
                }
            }

        }
    }

    /**
     * bytesToHex method
     * http://stackoverflow.com/a/9855338
     */
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public byte[] getIdAsByte(java.util.UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
