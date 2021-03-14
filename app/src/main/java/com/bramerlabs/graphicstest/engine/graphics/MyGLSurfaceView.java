package com.bramerlabs.graphicstest.engine.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    private final MGLRenderer renderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // create an GLES20 context
        setEGLContextClientVersion(2);

        renderer = new MGLRenderer(context);

        // set the renderer for drawing on the surfaceView
        setRenderer(renderer);

        // render only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                renderer.setCurDeltaPos(x - previousX, y - previousY);
                requestRender();
                break;
        }

        previousX = x;
        previousY = y;
        return true;
    }
}
