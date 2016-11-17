package net.derfunke.ruttetra.hmd.sense;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

class HMDSensorEventListener implements SensorEventListener {

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    public void onSensorChanged(SensorEvent evt) {
/*
        switch (evt.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER : {
                accel.x = evt.values[0];
                accel.y = evt.values[1];
                accel.z = evt.values[2];
                break;
            }
            case Sensor.TYPE_AMBIENT_TEMPERATURE : {
                temperature = evt.values[0];
                break;
            }
            case Sensor.TYPE_GAME_ROTATION_VECTOR : {
                break;
            }
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR : {
                break;
            }
            case Sensor.TYPE_GRAVITY : {
                gravity.x = evt.values[0];
                gravity.y = evt.values[1];
                gravity.z = evt.values[2];
                break;
            }
            case Sensor.TYPE_GYROSCOPE : {
                gyro.x = evt.values[0];
                gyro.y = evt.values[1];
                gyro.z = evt.values[2];
                break;
            }
            case Sensor.TYPE_LIGHT : {
                lux = evt.values[0];
                break;
            }
            case Sensor.TYPE_LINEAR_ACCELERATION : {
                linearaccel.x = evt.values[0];
                linearaccel.y = evt.values[1];
                linearaccel.z = evt.values[2];
                break;
            }
            case Sensor.TYPE_MAGNETIC_FIELD : {
                magnet.x = evt.values[0];
                magnet.y = evt.values[1];
                magnet.z = evt.values[2];
                break;
            }
            case Sensor.TYPE_PRESSURE : {
                pressure = evt.values[0];
                altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
                break;
            }
            case Sensor.TYPE_PROXIMITY : {
                proximity = evt.values[0];
                break;
            }
            case Sensor.TYPE_RELATIVE_HUMIDITY : {
                humidity = evt.values[0];
                break;
            }
            case Sensor.TYPE_ROTATION_VECTOR : {
                rotation.x = evt.values[0];
                rotation.y = evt.values[1];
                rotation.z = evt.values[2];
                break;
            }
            default : break;
        }
*/
    } // onSensorChanged
} // class