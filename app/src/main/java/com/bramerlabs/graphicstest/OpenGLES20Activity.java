package com.bramerlabs.graphicstest;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.bramerlabs.graphicstest.engine.graphics.MyGLSurfaceView;

public class OpenGLES20Activity extends Activity {

    private GLSurfaceView glView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create a GLSurfaceView instance and set it as the ContentView for this activity
        glView = new MyGLSurfaceView(this);
        setContentView(glView);
    }

}
