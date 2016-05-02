package org.ilapin.arfloor.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES11;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import org.ilapin.arfloor.R;
import org.ilapin.arfloor.graphics.ColorMaterial;
import org.ilapin.arfloor.graphics.Plane;
import org.ilapin.arfloor.graphics.Scene;
import org.ilapin.arfloor.math.Coordinate3D;
import org.ilapin.arfloor.math.Vector3D;
import org.ilapin.arfloor.ui.widgets.NormalizedSensorVectorView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

	private SensorManager mSensorManager;
	private Display mDisplay;
	private NormalizedSensorVectorView mVectorView;

	private final Scene mScene = new Scene();

	private final SensorEventListener mSensorListener = new SensorEventListener() {
		private final Vector3D mI = new Vector3D(1, 0, 0);

		@SuppressWarnings("SuspiciousNameCombination")
		@Override
		public void onSensorChanged(final SensorEvent event) {
			final float sensorX;
			final float sensorY;
			switch (mDisplay.getRotation()) {
				default:
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

			mVectorView.setVector(-sensorX, -sensorY, -event.values[2]);

			final Vector3D normalizedGravityYZ = Vector3D.normalize(new Vector3D(sensorY, event.values[2], 0));
			final float angleX = (float) (Math.acos(Vector3D.dotProduct(normalizedGravityYZ, mI)) * 180.0f / Math.PI);
			mScene.setCameraAngleX(-Math.signum(event.values[2]) * angleX);

			final Vector3D normalizedGravityXY = Vector3D.normalize(new Vector3D(sensorY, sensorX, 0));
			final float angleZ = (float) (Math.acos(Vector3D.dotProduct(normalizedGravityXY, mI)) * 180.0f / Math.PI);
			mScene.setCameraAngleZ(Math.signum(sensorX) * angleZ);
		}

		@Override
		public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
			// do nothing
		}
	};

	private final GLSurfaceView.Renderer mRenderer = new GLSurfaceView.Renderer() {

		@Override
		public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
			GLES11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			GLES11.glClearDepthf(1.0f);
			GLES11.glEnable(GLES11.GL_DEPTH_TEST);
			GLES11.glDepthFunc(GLES11.GL_LEQUAL);
			GLES11.glHint(GLES11.GL_PERSPECTIVE_CORRECTION_HINT, GLES11.GL_NICEST);
			GLES11.glShadeModel(GLES11.GL_SMOOTH);
			GLES11.glDisable(GLES11.GL_DITHER);
			GLES11.glEnable(GLES11.GL_BLEND);
			GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE_MINUS_SRC_ALPHA);
		}

		@Override
		public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
			final float aspect = (float) width / height;
			GLES11.glViewport(0, 0, width, height);
			GLES11.glMatrixMode(GLES11.GL_PROJECTION);
			GLES11.glLoadIdentity();
			GLU.gluPerspective(gl, 45, aspect, 0.1f, 10000.f);
			GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
			GLES11.glLoadIdentity();
		}

		@Override
		public void onDrawFrame(final GL10 gl) {
			GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT | GLES11.GL_DEPTH_BUFFER_BIT);
			mScene.render();
		}
	};

	@SuppressWarnings("ConstantConditions")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		mVectorView = (NormalizedSensorVectorView) findViewById(R.id.view_vector);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		final SeekBar angleSeekBar = (SeekBar) findViewById(R.id.view_angle);
		angleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
				mScene.setCameraAngles(new Coordinate3D(0, progress, 0));
			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
				// do nothing
			}

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				// do nothing
			}
		});

		final SeekBar cameraZSeekBar = (SeekBar) findViewById(R.id.view_camera_z);
		cameraZSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
				mScene.setCameraPosition(new Coordinate3D(0, 0, progress));
			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
				// do nothing
			}

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
				// do nothing
			}
		});

		final GLSurfaceView glView = new GLSurfaceView(this);
		final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		final float size320dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320, displayMetrics);
		final float size200dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, displayMetrics);
		final LinearLayout.LayoutParams glViewLayoutParams = new LinearLayout.LayoutParams(
				(int) size320dp, (int) size200dp
		);
		glViewLayoutParams.gravity = Gravity.CENTER;
		glView.setLayoutParams(glViewLayoutParams);
		glView.setRenderer(mRenderer);

		((ViewGroup) findViewById(R.id.view_content_container)).addView(glView);

		final Plane plane = new Plane(new ColorMaterial(0xff008000));
		plane.setPositinon(new Coordinate3D(0, -10, 0));
		mScene.addSceneObject(plane);
	}

	@Override
	protected void onResume() {
		super.onResume();

		final Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		mSensorManager.registerListener(mSensorListener, sensor, SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mSensorListener);
	}
}
