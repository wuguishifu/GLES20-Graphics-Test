package com.bramerlabs.graphicstest.engine.render_objects;

import android.opengl.GLES20;

import com.bramerlabs.graphicstest.engine.math.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private ShortBuffer drawListBuffer;

    // number of coords per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private static final float[] cubeCoords = {
            // front face
            -0.2f, -0.2f,  0.2f,
             0.2f, -0.2f,  0.2f,
             0.2f,  0.2f,  0.2f,
            -0.2f,  0.2f,  0.2f,
            // back face
            -0.2f, -0.2f, -0.2f,
             0.2f, -0.2f, -0.2f,
             0.2f,  0.2f, -0.2f,
            -0.2f,  0.2f, -0.2f,
            // right face
             0.2f, -0.2f,  0.2f,
             0.2f,  0.2f,  0.2f,
             0.2f, -0.2f, -0.2f,
             0.2f,  0.2f, -0.2f,
            // left face
            -0.2f, -0.2f,  0.2f,
            -0.2f,  0.2f,  0.2f,
            -0.2f, -0.2f, -0.2f,
            -0.2f,  0.2f, -0.2f,
            // top face
             0.2f,  0.2f,  0.2f,
            -0.2f,  0.2f,  0.2f,
             0.2f,  0.2f, -0.2f,
            -0.2f,  0.2f, -0.2f,
            // bottom face
            -0.2f, -0.2f,  0.2f,
             0.2f, -0.2f,  0.2f,
            -0.2f, -0.2f, -0.2f,
             0.2f, -0.2f, -0.2f,
    };

    // color of the shape
    private final float[] color = {
            95/255f, 109/255f, 201/255f, 1.0f
    };

    // the index list
    private final short[] drawOrder = {
            // front face
            0,      1,      2,
            0,      3,      3,
            // back face
            5,      4,      7,
            5,      7,      6,
            // right face
            8,      10,     11,
            8,      11,     9,
            // left face
            14,     12,     13,
            14,     13,     15,
            // top face
            17,     16,     18,
            17,     18,     19,
            // bottom face
            20,     22,     23,
            20,     23,     21,
    };

    // the normal vectors at each vertex
    private static final float[] normalCoords = {
            // front face
             0,  0,  1,
             0,  0,  1,
             0,  0,  1,
             0,  0,  1,
            // back face
             0,  0, -1,
             0,  0, -1,
             0,  0, -1,
             0,  0, -1,
            // right face
             1,  0,  0,
             1,  0,  0,
             1,  0,  0,
             1,  0,  0,
            // left face
            -1,  0,  0,
            -1,  0,  0,
            -1,  0,  0,
            -1,  0,  0,
            // top face
             0,  1,  0,
             0,  1,  0,
             0,  1,  0,
             0,  1,  0,
            // bottom face
             0, -1,  0,
             0, -1,  0,
             0, -1,  0,
             0, -1,  0,
    };

    // a pointer to the shader program
    private final int shaderProgram;

    /**
     * default constructor
     */
    public Cube(int shaderProgram) {
        this.shaderProgram = shaderProgram;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(cubeCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);

        // initialize vertex byte buffer for normal coordinates
        ByteBuffer nb = ByteBuffer.allocateDirect(normalCoords.length * 4);
        nb.order(ByteOrder.nativeOrder());
        normalBuffer = nb.asFloatBuffer();
        normalBuffer.put(normalCoords);
        normalBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(shaderProgram);
    }

    private final int vertexCount = cubeCoords.length / COORDS_PER_VERTEX;
    private final int normalCount = normalCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final int normalStride = COORDS_PER_VERTEX * 4;

    /**
     * draw the cube
     * @param vpMatrix - the view-projection matrix
     */
    public void draw(float[] vpMatrix, Vector3f viewPosition) {
        // add program to use OpenGL ES environment
        GLES20.glUseProgram(shaderProgram);

        // get handle to shader members
        // position handle
        int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");
        // color handle
        int colorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor");

        // enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        // prepare the normal coordinate data
        // set the color for drawing the triangles
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        // set the view position

        // get handle to shape's transformation matrix
        int vpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        // pass uMVPMatrix information
        GLES20.glUniformMatrix4fv(vpMatrixHandle, 1, false, vpMatrix, 0);
        // draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // disable the vertex array afterwards
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

}
