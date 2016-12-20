package net.derfunke.ruttetra.hmd.sandbox;

import net.derfunke.ruttetra.hmd.shader.*;
import net.derfunke.ruttetra.hmd.sense.*;


public class SensorPlayground extends SensorBaseLayer {

    public SensorPlayground(Shader shdr) {
        super(shdr);
    }

    // /////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////
    // You can change the code below this point to implement your
    // own mappings between sensor data and shader parameters.
    // /////////////////////////////////////////////////////////////////////////////
    // /////////////////////////////////////////////////////////////////////////////

    public void accelerometer(float a, float b, float c) {
        // mOverlayView = (CardboardOverlayView)findViewById(R.id.overlay);
        // mOverlayView.presentText(Float.toString(a));
        v0( 1.f, (b / 16000), (c / 16000) );
    }

    public void gyroscope(float a, float b, float c) {
        v1( (float)(a * .8), (float)(b * .82), (float)(c * .7) );
    }

    public void magnetometer(float a, float b, float c) {

        v2( .0f, .0f, .0f );
    }

    public void gravity(float a, float b, float c) {

        v3(a, b, c);
    }

    public void rotation(float a, float b, float c) {

        v4(a, b, c);
    }

    public void pressure(float val) {

        f0(val);
    }

    public void proximity(float val) {

        f1(val);
    }

    public void humidity(float val) {

        f3(val);
    }

    public void signalStrength(float val) {

        f4(val);
    }

} // class
