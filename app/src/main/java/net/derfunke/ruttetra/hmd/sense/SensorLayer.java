package net.derfunke.ruttetra.hmd.sense;

public interface SensorLayer {
    public void accelerometer(float a, float b, float c);
    public void gyroscope(float a, float b, float c);
    public void magnetometer(float a, float b, float c);
    public void gravity(float a, float b, float c);
    public void rotation(float a, float b, float c);
    public void pressure(float val);
    public void proximity(float val);
    public void humidity(float val);
    public void signalStrength(float val);
} // iface
