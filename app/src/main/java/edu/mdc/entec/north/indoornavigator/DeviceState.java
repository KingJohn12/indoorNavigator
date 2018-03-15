package edu.mdc.entec.north.indoornavigator;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.mdc.entec.north.indoornavigator.model.CellCoordinates;
import edu.mdc.entec.north.indoornavigator.model.Room;
import edu.mdc.entec.north.indoornavigator.view.MainActivity;

public class DeviceState {

    private float XAcceleration;
    private float YAcceleration;
    private float ZAcceleration;
    private float XRotation;
    private float YRotation;
    private float ZRotation;
    private float XMagneticField;
    private float YMagneticField;
    private float ZMagneticField;


    private int beacon11RSSI;
    private int beacon12RSSI;
    private int beacon21RSSI;
    private int beacon22RSSI;
    private int beacon31RSSI;
    private int beacon32RSSI;


    private CellCoordinates cellCoordinates;

    private Room room;

    private Context mContext;

    private List<DeviceSensorObserver> sensorObservers;
    private List<DeviceLocationObserver> locationObservers;


    public DeviceState(Context context) {
        mContext = context;
        sensorObservers = new ArrayList<>();
        locationObservers = new ArrayList<>();
    }

    public void registerDeviceSensorObserver(DeviceSensorObserver sensorObserver){
        sensorObservers.add(sensorObserver);
    }

    public void unregisterDeviceSensorObserver(DeviceSensorObserver sensorObserver){
        sensorObservers.remove(sensorObserver);
    }

    public void registerDeviceLocationObserver(DeviceLocationObserver locationObserver){
        locationObservers.add(locationObserver);
    }

    public void unregisterDeviceLocationObserver(DeviceLocationObserver locationObserver){
        locationObservers.remove(locationObserver);
    }

    private void notifySensorObservers(){
        for(DeviceSensorObserver observer : sensorObservers){
            observer.updateSensedState();
        }
    }

    private void notifyLocationObservers(){
        for(DeviceLocationObserver observer : locationObservers){
            observer.updateLocation();
        }
    }

    public void setAcceleration(float XAcceleration, float YAcceleration, float ZAcceleration) {
        this.XAcceleration = XAcceleration;
        this.YAcceleration = YAcceleration;
        this.ZAcceleration = ZAcceleration;
        notifySensorObservers();
    }

    public float getXAcceleration() {
        return XAcceleration;
    }

    public float getYAcceleration() {
        return YAcceleration;
    }

    public float getZAcceleration() {
        return ZAcceleration;
    }

    public void setRotation(float XRotation, float YRotation, float ZRotation) {
        this.XRotation = XRotation;
        this.YRotation = YRotation;
        this.ZRotation = ZRotation;
        notifySensorObservers();
    }

    public float getXRotation() {
        return XRotation;
    }

    public float getYRotation() {
        return YRotation;
    }


    public float getZRotation() {
        return ZRotation;
    }

    public void setMagneticField(float XMagneticField, float YMagneticField, float ZMagneticField) {
        this.XMagneticField = XMagneticField;
        this.YMagneticField = YMagneticField;
        this.ZMagneticField = ZMagneticField;
        notifySensorObservers();
    }

    public float getXMagneticField() {
        return XMagneticField;
    }

    public float getYMagneticField() {
        return YMagneticField;
    }

    public float getZMagneticField() {
        return ZMagneticField;
    }

    public int getBeacon11RSSI() {
        return beacon11RSSI;
    }

    public void setBeacon11RSSI(int beacon11RSSI) {
        this.beacon11RSSI = beacon11RSSI;
    }

    public int getBeacon12RSSI() {
        return beacon12RSSI;
    }

    public void setBeacon12RSSI(int beacon12RSSI) {
        this.beacon12RSSI = beacon12RSSI;
    }

    public int getBeacon21RSSI() {
        return beacon21RSSI;
    }

    public void setBeacon21RSSI(int beacon21RSSI) {
        this.beacon21RSSI = beacon21RSSI;
    }

    public int getBeacon22RSSI() {
        return beacon22RSSI;
    }

    public void setBeacon22RSSI(int beacon22RSSI) {
        this.beacon22RSSI = beacon22RSSI;
    }

    public int getBeacon31RSSI() {
        return beacon31RSSI;
    }

    public void setBeacon31RSSI(int beacon31RSSI) {
        this.beacon31RSSI = beacon31RSSI;
    }

    public int getBeacon32RSSI() {
        return beacon32RSSI;
    }

    public void setBeacon32RSSI(int beacon32RSSI) {
        this.beacon32RSSI = beacon32RSSI;
    }

    public CellCoordinates getCellCoordinates() {
        return cellCoordinates;
    }

    public Room getRoom() {
        return room;
    }


    public void setCellCoordinates(CellCoordinates cellCoordinates) {
        this.cellCoordinates = cellCoordinates;
        room = ((MainActivity) mContext).getDb().getRoomAtCoordinates(cellCoordinates);
        notifyLocationObservers();
    }

    @Override
    public String toString() {
        return "DeviceState{" +
                "XAcceleration=" + XAcceleration +
                ", YAcceleration=" + YAcceleration +
                ", ZAcceleration=" + ZAcceleration +
                ", XRotation=" + XRotation +
                ", YRotation=" + YRotation +
                ", ZRotation=" + ZRotation +
                ", XMagneticField=" + XMagneticField +
                ", YMagneticField=" + YMagneticField +
                ", ZMagneticField=" + ZMagneticField +
                ", beacon11RSSI=" + beacon11RSSI +
                ", beacon12RSSI=" + beacon12RSSI +
                ", beacon21RSSI=" + beacon21RSSI +
                ", beacon22RSSI=" + beacon22RSSI +
                ", beacon31RSSI=" + beacon31RSSI +
                ", beacon32RSSI=" + beacon32RSSI +
                ", cellCoordinates=" + cellCoordinates +
                ", room=" + room +
                '}';
    }
}
