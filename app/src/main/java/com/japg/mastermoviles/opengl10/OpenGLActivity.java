package com.japg.mastermoviles.opengl10;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import com.japg.mastermoviles.opengl10.util.RotationGestureDetector;


public class OpenGLActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener, RotationGestureDetector.OnRotationGestureListener, SwipeView.OnSwipeListener, SensorEventListener {
    private OpenGLSurfaceView glSurfaceView;
    private OpenGLRenderer mOpenGLRenderer;
    private boolean rendererSet = false;

    private boolean isTouchingScreen = false;
    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private RotationGestureDetector mRotationGestureDetector;
    private SwipeView mSwipeView;

    private SensorManager mSensorManager;
    private Sensor mGyroscopeSensor;

    private Display mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opengl);

        mGestureDetector = new GestureDetectorCompat(this, this);
        mScaleGestureDetector = new ScaleGestureDetector(this, this);
        mRotationGestureDetector = new RotationGestureDetector(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSwipeView = findViewById(R.id.swipeView);
        mSwipeView.setOnSwipeListener(this);

        mDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        glSurfaceView = findViewById(R.id.openGLSurfaceView);
        mOpenGLRenderer = new OpenGLRenderer(this);
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        //final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));
        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);
            // Para que funcione en el emulador
            glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            // Asigna nuestro renderer.
            glSurfaceView.setRenderer(mOpenGLRenderer);
            rendererSet = true;
            // Toast.makeText(this, "OpenGL ES 2.0 soportado", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Este dispositivo no soporta OpenGL ES 2.0", Toast.LENGTH_LONG).show();
            return;
        }

        glSurfaceView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isTouchingScreen = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP && event.getPointerCount() == 1) {
                    isTouchingScreen = false;
                }

                boolean retVal = v.performClick();
                retVal = mScaleGestureDetector.onTouchEvent(event) || retVal ;
                retVal = mRotationGestureDetector.onTouchEvent(event) || retVal;
                retVal = mGestureDetector.onTouchEvent(event) || retVal;
                return retVal;
            }
        });

        mSwipeView.setInstructionsText("Desliza el dedo para rotar"); // TODO: Desliza el dedo para rotar... ¿el qué?
        updateSwipeViewText(0);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        final float normDistX = distanceX / (float)glSurfaceView.getWidth();
        final float normDistY = distanceY / (float)glSurfaceView.getHeight();

        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mOpenGLRenderer.handleTouchScroll(normDistX, normDistY);
            }
        });

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        final float scaleFactor = detector.getScaleFactor();

        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mOpenGLRenderer.handleTouchScale(scaleFactor);
            }
        });

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean OnRotation(RotationGestureDetector rotationDetector) {
        final float angle = rotationDetector.getAngle();

        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mOpenGLRenderer.handleTouchRotation(angle);
            }
        });

        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isTouchingScreen || event.sensor.getType() != Sensor.TYPE_GYROSCOPE){
            return;
        }

        float sensorX = 0, sensorY = 0;
        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                sensorX = event.values[0];
                sensorY = event.values[1];
                break;
            case Surface.ROTATION_90:
                sensorX = -event.values[1];
                sensorY = event.values[0];
                break;
            case Surface.ROTATION_180:
                sensorX = -event.values[0];
                sensorY = -event.values[1];
                break;
            case Surface.ROTATION_270:
                sensorX = event.values[1];
                sensorY = -event.values[0];
                break;
        }

        final float x = sensorX;
        final float y = sensorY;

        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mOpenGLRenderer.handleGyroscopeRotation(x, y);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onSwipeEvent(float distanceX, float distanceY) {
        final float normDistX = distanceX / (float)mSwipeView.getWidth();

        glSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mOpenGLRenderer.handleSwipe(normDistX);
                updateSwipeViewText((int)mOpenGLRenderer.get2ndObjectRotationZ());
            }
        });

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGyroscopeSensor != null){
            mSensorManager.unregisterListener(this);
        }

        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGyroscopeSensor != null){
            mSensorManager.registerListener(this, mGyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

    private void updateSwipeViewText(final int rotation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int r = rotation % 360;
                if (r < 0) r = 360 - Math.abs(r);
                mSwipeView.setText(r + "º");
            }
        });
    }
}
