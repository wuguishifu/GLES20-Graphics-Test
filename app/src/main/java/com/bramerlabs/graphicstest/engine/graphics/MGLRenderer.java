package com.bramerlabs.graphicstest.engine.graphics;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.bramerlabs.graphicstest.R;
import com.bramerlabs.graphicstest.engine.file_utils.FileUtils;
import com.bramerlabs.graphicstest.engine.math.Vector3f;
import com.bramerlabs.graphicstest.engine.render_objects.Cube;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MGLRenderer implements GLSurfaceView.Renderer {

    // MVP matrix components
    private final float[] vpMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];

    // arcball camera variables
    private float dtx, dty; // change in x and y
    private final Vector3f lookingAt = new Vector3f(0, 0, 0); // the position at which the arcball camera is looking at
    private final Vector3f position = new Vector3f(0, 0, 1); // the position of the camera
    private final Vector3f rotation = new Vector3f(0, 0, 0); // the rotation of the camera
    private static final float DEFAULT_DISTANCE = 4.0f;
    private final float distance = DEFAULT_DISTANCE; // the distance the arcball camera is from the object
    private static final float DEFAULT_H_DISTANCE = 0, DEFAULT_V_DISTANCE = 0;
    private float hDistance = DEFAULT_H_DISTANCE, vDistance = DEFAULT_V_DISTANCE; // the horizontal and vertical distances from the object
    private static final float DEFAULT_V_ANGLE = 0, DEFAULT_H_ANGLE = 0;
    private float vAngle = DEFAULT_V_ANGLE, hAngle = DEFAULT_H_ANGLE; // the horizontal and vertical angles from the object

    // motion variables
    private static final float TOUCH_SENSITIVITY = 0.1f;

    // lighting variables
    private static final float[] lightPosition = {0, 3.0f, 3.0f}; // the position of light

    // object to draw
    private Cube cube;

    // the context
    private Context context;

    /**
     * default constructor
     * @param context - the context
     */
    public MGLRenderer(Context context) {
        this.context = context;
    }

    /**
     * called when the renderer creates a surface
     * @param gl - the gl context (unused - deprecated)
     * @param config - the configuration settings for the renderer
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // set the clear color - fullbright white
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        // create the shader
        String mainVertex = FileUtils.loadAsString(context.getResources().openRawResource(R.raw.main_vertex));
        String mainFragment = FileUtils.loadAsString(context.getResources().openRawResource(R.raw.main_fragment));
        int shaderProgram = GLES20.glCreateProgram();
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mainVertex);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mainFragment);
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);

        // cube to render
        cube = new Cube(shaderProgram);

    }

    /**
     * called when the renderer detects a surface change
     * @param gl - the gl context (unused - deprecated)
     * @param width - the width of the screen (view)
     * @param height - the height of the screen (view)
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // set the viewport
        GLES20.glViewport(0, 0, width, height);

        // only change the projection matrix when the viewport changes
        // compute the ratio
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 7.0f);
    }

    /**
     * called when renderer is supposed to draw the frame
     * @param gl - the gl context (unused - deprecated)
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // clear the screen to the background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


//        long time1 = SystemClock.uptimeMillis() % 4000L;
//        hAngle = 0.090f * ((int) time1);
//        long time2 = SystemClock.uptimeMillis() % 3000L;
//        vAngle = 0.090f * ((int) time2);

        if (vAngle > 180) {
            vAngle -= 180;
        }
        if (vAngle < -180) {
            vAngle += 180;
        }
        if (hAngle > 180) {
            hAngle -= 180;
        }
        if (hAngle < -180) {
            hAngle += 100;
        }

        // compute the view matrix
        // handle touch motion
        // compute new rotation
        vAngle -= dty * TOUCH_SENSITIVITY;
        hAngle += dtx * TOUCH_SENSITIVITY;
        // compute vertical and horizontal distances
        hDistance = (float) (distance * Math.cos(Math.toRadians(vAngle)));
        vDistance = (float) (distance * Math.sin(Math.toRadians(vAngle)));
        float xOffset = (float) (hDistance * Math.sin(Math.toRadians(-hAngle)));
        float zOffset = (float) (hDistance * Math.cos(Math.toRadians(-hAngle)));
        // set the new camera position based on the center
        this.position.set(lookingAt.getX() + xOffset, lookingAt.getY() - vDistance, lookingAt.getZ() + zOffset);
        // set the new camera rotation based on the center
        this.rotation.set(vAngle, -hAngle, 0);
        // compute view matrix and store it in the float array
        Matrix.setLookAtM(viewMatrix, 0, position.getX(), position.getY(), position.getZ(), lookingAt.getX(), lookingAt.getY(), lookingAt.getZ(), 0f, 1.0f, 0.0f);

        // compute the view-projection matrix
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // draw the objects using this view-projection matrix
        cube.draw(vpMatrix, this.position);
    }

    /**
     * loads a shader from a string
     * @param type - the type of shader
     * @param shaderCode - the string containing the shader code
     * @return - the pointer to the compiled shader program
     */
    public static int loadShader(int type, String shaderCode) {
        // initialize an empty shader
        int shader = GLES20.glCreateShader(type);

        // add source code to shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // return the pointer
        return shader;
    }

    /**
     * sets the current x and y position
     * @param dx - the change x position
     * @param dy - the change y position
     */
    public void setCurDeltaPos(float dx, float dy) {
        this.dtx = dx;
        this.dty = dy;
    }
}
