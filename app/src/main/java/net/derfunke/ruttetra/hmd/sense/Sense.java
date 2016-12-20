package net.derfunke.ruttetra.hmd.sense;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import net.derfunke.ruttetra.hmd.sandbox.SensorPlayground;
import net.derfunke.ruttetra.hmd.util.*;

import java.util.List;

public class Sense {
    SensorManager sensorManager;
    List<Sensor> sensors;
    HMDSensorEventListener listener;

    SensorPlayground playground;

    public Vector3 accel;
    public Vector3 linearaccel;
    public Vector3 rotation;
    public Vector3 orientation;
    public Vector3 gyro;
    public Vector3 magnet;
    public Vector3 gravity;
    public float lux;
    public float pressure; // mPh
    public float altitude; // metres
    public float proximity;  // 0 or 1
    public float humidity; // percent
    public float temperature; // in celsius

    public Sense(Context ctxt) {
        accel = new Vector3();
        linearaccel = new Vector3();
        rotation = new Vector3();
        orientation = new Vector3();
        gyro = new Vector3();
        magnet = new Vector3();
        gravity = new Vector3();

        playground = new SensorPlayground();

        //context = surface.getActivity();
        sensorManager = (SensorManager)ctxt.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            listener = new HMDSensorEventListener(this);
            for (Sensor s : sensors) {
                Log.d("Sense", s.getName());
                sensorManager.registerListener(listener, s, SensorManager.SENSOR_DELAY_GAME);
            } // for
        } // if

    } // ctor

    public void update() {

    }

} // class Sense
