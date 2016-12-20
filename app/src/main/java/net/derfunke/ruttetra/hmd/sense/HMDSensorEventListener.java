package net.derfunke.ruttetra.hmd.sense;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

class HMDSensorEventListener implements SensorEventListener {

    Sense sense;

    public HMDSensorEventListener(Sense sense) {
        this.sense = sense;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    public void onSensorChanged(SensorEvent evt) {
        switch (evt.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER : {
                sense.accel.x = evt.values[0];
                sense.accel.y = evt.values[1];
                sense.accel.z = evt.values[2];
                break;
            }

            case Sensor.TYPE_AMBIENT_TEMPERATURE : {
                sense.temperature = evt.values[0];
                break;
            }

            case Sensor.TYPE_GRAVITY : {
                sense.gravity.x = evt.values[0];
                sense.gravity.y = evt.values[1];
                sense.gravity.z = evt.values[2];
                break;
            }

            case Sensor.TYPE_GYROSCOPE : {
                sense.gyro.x = evt.values[0];
                sense.gyro.y = evt.values[1];
                sense.gyro.z = evt.values[2];
                break;
            }

            case Sensor.TYPE_LIGHT : {
                sense.lux = evt.values[0];
                break;
            }

            case Sensor.TYPE_LINEAR_ACCELERATION : {
                sense.linearaccel.x = evt.values[0];
                sense.linearaccel.y = evt.values[1];
                sense.linearaccel.z = evt.values[2];
                break;
            }

            case Sensor.TYPE_MAGNETIC_FIELD : {
                sense.magnet.x = evt.values[0];
                sense.magnet.y = evt.values[1];
                sense.magnet.z = evt.values[2];
                break;
            }

            case Sensor.TYPE_PRESSURE : {
                sense.pressure = evt.values[0];
                sense.altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, sense.pressure);
                break;
            }

            case Sensor.TYPE_PROXIMITY : {
                sense.proximity = evt.values[0];
                break;
            }

            case Sensor.TYPE_RELATIVE_HUMIDITY : {
                sense.humidity = evt.values[0];
                break;
            }

            case Sensor.TYPE_ROTATION_VECTOR : {
                sense.rotation.x = evt.values[0];
                sense.rotation.y = evt.values[1];
                sense.rotation.z = evt.values[2];
                break;
            }

            default : break;
        }
    } // onSensorChanged
} // class