package net.derfunke.ruttetra.hmd;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import com.google.vrtoolkit.cardboard.*;

import net.derfunke.ruttetra.hmd.shader.Shader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

// JR mucking about below
import android.os.Handler;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import net.derfunke.ruttetra.hmd.sandbox.SensorPlayground;

// JR mucking about above

public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer, OnFrameAvailableListener {

    private static final String TAG = "MainActivity";
    private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    private Camera camera;

    private FloatBuffer vertexBuffer, textureVerticesBuffer, vertexBuffer2;
    private ShortBuffer drawListBuffer, buf2;
    private int mProgram;
    private int mPositionHandle, mPositionHandle2;
    private int mColorHandle;
    private int mTextureCoordHandle;

    // JR mucking about below
    private BluetoothAdapter reBluetoothAdapter;
    private BluetoothSocket reSocket;
    private BluetoothDevice reDevice;
    private OutputStream reOutputStream;
    private InputStream reInputStream;
    private Thread workerThread;
    private byte[] readBuffer;
    private int readBufferPosition;
    private int reCounter;
    private volatile boolean stopWorker;
    public float hundValue;
    public Float[] arduinoValues = {0f, 0f, 0f, 0f, 0f};
    // JR mucking about above

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 2;
    static float squareVertices[] = { // in counterclockwise order:
    	-1.0f, -1.0f,   // 0.left - mid
    	 1.0f, -1.0f,   // 1. right - mid
    	-1.0f, 1.0f,   // 2. left - top
    	 1.0f, 1.0f,   // 3. right - top
    };
    
    

    
    private short drawOrder[] =  {0, 2, 1, 1, 2, 3 }; // order to draw vertices
    private short drawOrder2[] = {2, 0, 3, 3, 0, 1}; // order to draw vertices

    static float textureVertices[] = {
	 0.0f, 1.0f,  // A. left-bottom
	   1.0f, 1.0f,  // B. right-bottom
	   0.0f, 0.0f,  // C. left-top
	   1.0f, 0.0f   // D. right-top  
   };

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private ByteBuffer indexBuffer;    // Buffer for index-array
    
    private int texture;


    private CardboardOverlayView mOverlayView;


    Shader shader;

	private CardboardView cardboardView;
	private SurfaceTexture surface;
	private float[] mView;
	private float[] mCamera;

	public void startCamera(int texture)
    {
        surface = new SurfaceTexture(texture);
        surface.setOnFrameAvailableListener(this);

        camera = Camera.open();

        try
        {
            camera.setPreviewTexture(surface);
            camera.startPreview();
        }
        catch (IOException ioe)
        {
            Log.w("MainActivity","CAM LAUNCH FAILED");
        }
    }
	
    static private int createTexture()
    {
        int[] texture = new int[1];

        GLES20.glGenTextures(1,texture, 0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
             GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);        
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
             GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
     GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,
             GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
     GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,
             GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

	
    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader
     * @param type The type of shader we will be creating.
     * @return
     */
//    private int loadGLShader(int type, String code) {
//        int shader = GLES20.glCreateShader(type);
//        GLES20.glShaderSource(shader, code);
//        GLES20.glCompileShader(shader);
//
//        // Get the compilation status.
//        final int[] compileStatus = new int[1];
//        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
//
//        // If the compilation failed, delete the shader.
//        if (compileStatus[0] == 0) {
//            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
//            GLES20.glDeleteShader(shader);
//            shader = 0;
//        }
//
//        if (shader == 0) {
//            throw new RuntimeException("Error creating shader.");
//        }
//
//        return shader;
//    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     * @param func
     */
    private static void checkGLError(String func) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, func + ": glError " + error);
            throw new RuntimeException(func + ": glError " + error);
        }
    }

    /**
     * Sets the view to our CardboardView and initializes the transformation matrices we will use
     * to render our scene.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_ui);
        cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);

        shader = new Shader();

//        mModelCube = new float[16];
        mCamera = new float[16];
        mView = new float[16];

        // JR mucking about below
        // is this the place to start BT stuff ?
        boolean found = false;
        try {
            found = findBT();
            // JR:  we are only opening the device if one is found !!
            if (found) {
                openBT();
            }
        }
        catch (IOException ex) { }

        mOverlayView = (CardboardOverlayView)findViewById(R.id.overlay);
        if (found) {
            mOverlayView.presentText("CONNECTING TO UMWELT (via bluetooth)");
        }
        else {
            mOverlayView.presentText("CONNECTING TO UMWELT");
    }
        // JR mucking about above
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
    }

    protected void prepareRenderBuffers() {
        ByteBuffer bb = ByteBuffer.allocateDirect(squareVertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareVertices);
        vertexBuffer.position(0);


        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);


        ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        textureVerticesBuffer = bb2.asFloatBuffer();
        textureVerticesBuffer.put(textureVertices);
        textureVerticesBuffer.position(0);
    }

    /**
     * Creates the buffers we use to store information about the 3D world. OpenGL doesn't use Java
     * arrays, but rather needs data in a format it can understand. Hence we use ByteBuffers.
     * @param config The EGL configuration used when creating the surface.
     */
    @Override
    public void onSurfaceCreated(EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f); // Dark background so text shows up well

        this.prepareRenderBuffers();

        // load shader sources from resources and compile shader program
        String srcVertexShader = RawResourceReader.readRawTextResource(this, R.raw.base_vertex_shader);
        String srcFragmentShader = RawResourceReader.readRawTextResource(this, R.raw.base_fragment_shader);

        shader.loadfromString(srcFragmentShader, srcVertexShader);

//        int vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, srcVertexShader);
//        int fragmentShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, srcFragmentShader);

//        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
//        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
//        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
//        GLES20.glLinkProgram(mProgram);
        
        texture = createTexture();
        startCamera(texture);
    }


    /**
     * Prepares OpenGL ES before we draw a frame.
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
    	float[] mtx = new float[16];
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        surface.updateTexImage();
        surface.getTransformMatrix(mtx); 
    }
	
    @Override
	public void onFrameAvailable(SurfaceTexture arg0) {
		this.cardboardView.requestRender();
		
	}    	   

    /**
     * Draws a frame for an eye. The transformation for that eye (from the camera) is passed in as
     * a parameter.
     * @param transform The transformations to apply to render this eye.
     */
    @Override
    public void onDrawEye(EyeTransform transform) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float eye = 1.0f * transform.getParams().getEye();
        //Log.d("Main", "eye = "+eye);

        shader.bind();

        shader.set("eye", eye);
        shader.set("f0", arduinoValues[0]);
        shader.set("f1", arduinoValues[1]);
        shader.set("f2", arduinoValues[2]);
        shader.set("f3", arduinoValues[3]);
        shader.set("f4", arduinoValues[4]);

        mProgram = shader.getGLId();

        GLES20.glActiveTexture(GL_TEXTURE_EXTERNAL_OES);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, texture);

//        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
//        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Reflection);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "position");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
        		false, vertexStride, vertexBuffer);
        

        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
        		false, vertexStride, textureVerticesBuffer);

        //mColorHandle = GLES20.glGetAttribLocation(mProgram, "s_texture");



        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
        					  GLES20.GL_UNSIGNED_SHORT, drawListBuffer);


        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        
        Matrix.multiplyMM(mView, 0, transform.getEyeView(), 0, mCamera, 0);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    /**
     * Increment the score, hide the object, and give feedback if the user pulls the magnet while
     * looking at the object. Otherwise, remind the user what to do.
     */
    @Override
    public void onCardboardTrigger() {
//        // Always give user feedback
//        mVibrator.vibrate(50);
    }

    // JR mucking about below

    boolean findBT()
    {
        reBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(reBluetoothAdapter == null)
        {
            // does this stuff below work ?
            mOverlayView = (CardboardOverlayView)findViewById(R.id.overlay);
            mOverlayView.presentText("no bluetooth adapter found");
            return false;
        }

        // if(!reBluetoothAdapter.isEnabled())
        // {
        //     Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //     startActivityForResult(enableBluetooth, 0);
        // }

        Set<BluetoothDevice> pairedDevices = reBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                // JR ridiculous hack, takes first paired device in list.
                    reDevice = device;
                    return true;
            }
        }
        return false;
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        reSocket = reDevice.createRfcommSocketToServiceRecord(uuid);
        reSocket.connect();
        reOutputStream = reSocket.getOutputStream();
        reInputStream = reSocket.getInputStream();

        beginListenForData();
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = reInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            reInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            // JR: need to parse data and call setter ?
                                            // sending more than five values from the arduino means trouble...
                                            // JR: really no idea what this handler does and can do..
                                            // how can this runnable call SensorPlayGround stuff ???
                                            // is anything actually ever calling those methods ?? how ???
                                            String[] dataValues = data.split("\\s+");
                                            int i = 0;
                                            for (String val: dataValues) {
                                                arduinoValues[i] = (float) Integer.parseInt(val) / 1024f;
                                                i = i + 1;
                                            }
                                            // JR: it seems as if values are being updates once every five
                                            // receives ??
                                            // the eye value gets updated for every frame it seems
                                            // the arduinovalues are received (overlay shows them updating as
                                            //  expected, when printing the actual arduinoValues in the array)
                                            // but the arduinovavlues in the array somehow do not get through
                                            // except once every five updates ?????? very strange...
                                            // mOverlayView = (CardboardOverlayView)findViewById(R.id.overlay);
                                            // mOverlayView.presentText(Float.toString(((arduinoValues[0] % 1f) * 20f) % 1f));
                                            // SensorPlayground.arduinoValues(ardValues[0], ardValues[1], ardValues[2], ardValues[3], ardValues[4]);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    // JR this never gets called, but no ill effects ?
    void closeBT() throws IOException
    {
        stopWorker = true;
        reInputStream.close();
        reOutputStream.close();
        reSocket.close();
    }
// JR mucking about above

} // MainActivity
