package net.derfunke.ruttetra.hmd.sense;

import net.derfunke.ruttetra.hmd.shader.*;

abstract public class SensorBaseLayer {

    Shader _shader;

    public SensorBaseLayer(Shader shader) {
        this._shader = shader;
    }


    protected void set(String name, float a, float b, float c) {
        this._shader.set(name, a, b, c);
    }

    protected void set(String name, float a) {
        this._shader.set(name, a);
    }


    protected void v0(float a, float b, float c) {
        set("v0", a, b, c);
    }

    protected void v1(float a, float b, float c) {
        set("v1", a, b, c);
    }

    protected void v2(float a, float b, float c) {
        set("v2", a, b, c);
    }

    protected void v3(float a, float b, float c) {
        set("v3", a, b, c);
    }

    protected void v4(float a, float b, float c) {
        set("v4", a, b, c);
    }

    protected void f0(float a) {
        set("f0", a);
    }

    protected void f1(float a) {
        set("f1", a);
    }

    protected void f2(float a) {
        set("f2", a);
    }

    protected void f3(float a) {
        set("f3", a);
    }

    protected void f4(float a) {
        set("f4", a);
    }

    abstract public void accelerometer(float a, float b, float c);
    abstract public void gyroscope(float a, float b, float c);
    abstract public void magnetometer(float a, float b, float c);
    abstract public void gravity(float a, float b, float c);
    abstract public void rotation(float a, float b, float c);
    abstract public void pressure(float val);
    abstract public void proximity(float val);
    abstract public void humidity(float val);
    abstract public void signalStrength(float val);

}
