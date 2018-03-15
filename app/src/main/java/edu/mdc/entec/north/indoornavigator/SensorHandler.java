package edu.mdc.entec.north.indoornavigator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class SensorHandler implements SensorEventListener {

    private static final String TAG = SensorHandler.class.getSimpleName();

    private Context mContext;
    private DeviceState deviceState;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private Sensor gyroscope;
    private boolean accelerometerPresent;
    private boolean magnetometerPresent;
    private boolean gyroscopePresent;

    private WindowManager mWindowManager;
    private Display mDisplay;

    public SensorHandler(Context context, DeviceState deviceState) {
        mContext = context;
        this.deviceState = deviceState;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        initSensors();
    }


    private void initSensors() {
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensors) {
            Log.i(TAG, "Found sensor: " + s.toString());
        }
        mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            Log.i(TAG, "CREATED ACCELEROMETER:" + accelerometer.toString());
            accelerometerPresent = true;
        } else
            accelerometerPresent = false;
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magnetometer != null) {
            Log.i(TAG, "CREATED MAGNETOMETER:" + magnetometer.toString());
            magnetometerPresent = true;
        } else
            magnetometerPresent = false;
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope != null) {
            Log.i(TAG, "CREATED GYROSCOPE:" + gyroscope.toString());
            gyroscopePresent = true;
        } else
            gyroscopePresent = false;
    }

    public void doScan(boolean enable) {
        if (enable) {
            if (accelerometerPresent)
                mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            if (magnetometerPresent)
                mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
            if (gyroscopePresent)
                mSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);

        } else {
            mSensorManager.unregisterListener(this);
        }
    }

    public Sensor getAccelerometer() {
        return accelerometer;
    }

    public Sensor getMagnetometer() {
        return magnetometer;
    }

    public Sensor getGyroscope() {
        return gyroscope;
    }

    public boolean isAccelerometerPresent() {
        return accelerometerPresent;
    }

    public boolean isMagnetometerPresent() {
        return magnetometerPresent;
    }

    public boolean isGyroscopePresent() {
        return gyroscopePresent;
    }

    @Override
    public String toString() {
        return "SensorHandler{" +
                "mSensorManager=" + mSensorManager +
                ", accelerometer=" + accelerometer +
                ", magnetometer=" + magnetometer +
                ", gyroscope=" + gyroscope +
                ", accelerometerPresent=" + accelerometerPresent +
                ", magnetometerPresent=" + magnetometerPresent +
                ", gyroscopePresent=" + gyroscopePresent +
                '}';
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            /*
             * We need to
             * take into account how the screen is rotated with respect to the
             * sensors (which always return data in a coordinate space aligned
             * to with the screen in its native orientation).
             */
            float mSensorX = 0;
            float mSensorY = 0;
            float mSensorZ = 0;
            switch (mDisplay.getRotation()) {
                case Surface.ROTATION_0:
                    mSensorX = event.values[0];
                    mSensorY = event.values[1];
                    mSensorZ = event.values[2];
                    break;
                case Surface.ROTATION_90:
                    mSensorX = -event.values[1];
                    mSensorY = event.values[0];
                    mSensorZ = event.values[2];
                    break;
                case Surface.ROTATION_180:
                    mSensorX = -event.values[0];
                    mSensorY = -event.values[1];
                    mSensorZ = event.values[2];
                    break;
                case Surface.ROTATION_270:
                    mSensorX = event.values[1];
                    mSensorY = -event.values[0];
                    mSensorZ = event.values[2];
                    break;
            }
            deviceState.setAcceleration(mSensorX, mSensorY, mSensorZ);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float magneticFieldX = event.values[0];
            float magneticFieldY = event.values[1];
            float magneticFieldZ = event.values[2];
            deviceState.setMagneticField(magneticFieldX, magneticFieldY, magneticFieldZ);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float rotationX = event.values[0];
            float rotationY = event.values[1];
            float rotationZ = event.values[2];
            deviceState.setRotation(rotationX, rotationY, rotationZ);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
