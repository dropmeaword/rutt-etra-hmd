package net.derfunke.ruttetra.hmd.shader;

import android.opengl.GLES20;
import android.util.Log;

import java.util.HashMap;

public class Shader {

    private static final String TAG = "Shader";

    String srcVertex, srcFragment; // store sources of vertex and fragment shader

    int glidVertex;
    int glidFragment;

    int glidProgram;

    // HashMap for storing uniform/attribute handles.
    private final HashMap<String, Integer> shaderLocationMap = new HashMap<String, Integer>();

    public Shader() {
    }

    protected int loadfromString(int type, String code) {
        int glid = GLES20.glCreateShader(type);

        GLES20.glShaderSource(glid, code);
        GLES20.glCompileShader(glid);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(glid, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(glid));
            GLES20.glDeleteShader(glid);
            glid = 0;
        }

        if (glid == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return glid;
    }

    public void loadfromString(String fragment, String vertex) {
        srcVertex = vertex;
        srcFragment = fragment;

        glidVertex = this.loadfromString(GLES20.GL_VERTEX_SHADER, vertex);
        glidFragment = this.loadfromString(GLES20.GL_FRAGMENT_SHADER, fragment);
        this.attach();
    }

    protected void attach() {
        glidProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(glidProgram, glidVertex);     // add the vertex shader to program
        GLES20.glAttachShader(glidProgram, glidFragment);   // add the fragment shader to program
        GLES20.glLinkProgram(glidProgram);
    }

    public int getGLId() { return glidProgram; };

    public void delete() {
        GLES20.glDeleteShader(glidFragment);
        GLES20.glDeleteShader(glidVertex);
        GLES20.glDeleteProgram(glidProgram);
        glidProgram = glidVertex = glidFragment = 0;
    }

    public void bind() {
        GLES20.glUseProgram( glidProgram );
    }

    public void unbind() {
        // nothing
    }

    /**
     * Get id for given handle name. This method checks for both attribute and
     * uniform handles.
     *
     * @param name
     *            Name of handle.
     * @return Id for given handle or -1 if none found.
     */
    public int getLocation(String name) {

        // first look in our map
        if (shaderLocationMap.containsKey(name)) {
            return shaderLocationMap.get(name);
        }

        // otherwise query the GL stack
        int handle = GLES20.glGetAttribLocation(glidProgram, name);
        if (handle == -1) {
            handle = GLES20.glGetUniformLocation(glidProgram, name);
        }
        if (handle == -1) {
            // One should never leave log messages but am not going to follow
            // this rule. This line comes handy if you see repeating 'not found'
            // messages on LogCat - usually for typos otherwise annoying to
            // spot from shader code.
            Log.d("GlslShader", "Could not get attrib location for " + name);
        } else {
            shaderLocationMap.put(name, handle);
        }

        return handle;
    }

    // SHORT-HAND uniform/attrbute setters, if the one you are looking for is not implemented
    // just use the int loc = shader.getLocation("name") and GLES20.glUniformXXX(loc, ...)

    public void set(String name, float v) {
        GLES20.glUniform1f(getLocation(name), v);
    }

    public void set(String name, int v) {
        GLES20.glUniform1f(getLocation(name), v);
    }

    public void set(String name, float a, float b, float c) {
        GLES20.glUniform3f(getLocation(name), a, b, c);
    }
}


//import java.util.HashMap;
//
//        import android.opengl.GLES20;
//        import android.util.Log;
//
///**
// * Helper class for handling shaders.
// */
//public final class ObjShader {
//
//    // Shader program handles.
//    private int mProgram = 0;
//    private int mShaderFragment = 0;
//    // HashMap for storing uniform/attribute handles.
//    private final HashMap<String, Integer> mShaderHandleMap = new HashMap<String, Integer>();
//    private int mShaderVertex = 0;
//
//    /**
//     * Deletes program and shaders associated with it.
//     */
//    public void deleteProgram() {
//        GLES20.glDeleteShader(mShaderFragment);
//        GLES20.glDeleteShader(mShaderVertex);
//        GLES20.glDeleteProgram(mProgram);
//        mProgram = mShaderVertex = mShaderFragment = 0;
//    }
//
//    /**
//     * Get id for given handle name. This method checks for both attribute and
//     * uniform handles.
//     *
//     * @param name
//     *            Name of handle.
//     * @return Id for given handle or -1 if none found.
//     */
//    public int getHandle(String name) {
//        if (mShaderHandleMap.containsKey(name)) {
//            return mShaderHandleMap.get(name);
//        }
//        int handle = GLES20.glGetAttribLocation(mProgram, name);
//        if (handle == -1) {
//            handle = GLES20.glGetUniformLocation(mProgram, name);
//        }
//        if (handle == -1) {
//            // One should never leave log messages but am not going to follow
//            // this rule. This line comes handy if you see repeating 'not found'
//            // messages on LogCat - usually for typos otherwise annoying to
//            // spot from shader code.
//            Log.d("GlslShader", "Could not get attrib location for " + name);
//        } else {
//            mShaderHandleMap.put(name, handle);
//        }
//        return handle;
//    }
//
//    /**
//     * Get array of ids with given names. Returned array is sized to given
//     * amount name elements.
//     *
//     * @param names
//     *            List of handle names.
//     * @return array of handle ids.
//     */
//    public int[] getHandles(String... names) {
//        int[] res = new int[names.length];
//        for (int i = 0; i < names.length; ++i) {
//            res[i] = getHandle(names[i]);
//        }
//        return res;
//    }
//
//    /**
//     * Helper method for compiling a shader.
//     *
//     * @param shaderType
//     *            Type of shader to compile
//     * @param source
//     *            String presentation for shader
//     * @return id for compiled shader
//     */
//    private int loadShader(int shaderType, String source) throws Exception {
//        int shader = GLES20.glCreateShader(shaderType);
//        if (shader != 0) {
//            GLES20.glShaderSource(shader, source);
//            GLES20.glCompileShader(shader);
//            int[] compiled = new int[1];
//            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
//            if (compiled[0] == 0) {
//                String error = GLES20.glGetShaderInfoLog(shader);
//                GLES20.glDeleteShader(shader);
//                throw new Exception(error);
//            }
//        }
//        return shader;
//    }
//
//    /**
//     * Compiles vertex and fragment shaders and links them into a program one
//     * can use for rendering. Once OpenGL context is lost and onSurfaceCreated
//     * is called, there is no need to reset existing GlslShader objects but one
//     * can simply reload shader.
//     *
//     * @param vertexSource
//     *            String presentation for vertex shader
//     * @param fragmentSource
//     *            String presentation for fragment shader
//     */
//    public void setProgram(String vertexSource, String fragmentSource)
//            throws Exception {
//        mShaderVertex = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
//        mShaderFragment = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
//        int program = GLES20.glCreateProgram();
//        if (program != 0) {
//            GLES20.glAttachShader(program, mShaderVertex);
//            GLES20.glAttachShader(program, mShaderFragment);
//            GLES20.glLinkProgram(program);
//            int[] linkStatus = new int[1];
//            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
//            if (linkStatus[0] != GLES20.GL_TRUE) {
//                String error = GLES20.glGetProgramInfoLog(program);
//                deleteProgram();
//                throw new Exception(error);
//            }
//        }
//        mProgram = program;
//        mShaderHandleMap.clear();
//    }
//
//    /**
//     * Activates this shader program.
//     */
//    public void useProgram() {
//        GLES20.glUseProgram(mProgram);
//    }
//
//}
