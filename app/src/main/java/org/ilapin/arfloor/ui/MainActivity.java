package org.ilapin.arfloor.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.opengl.GLES11;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import org.ilapin.arfloor.Compass;
import org.ilapin.arfloor.GpsModule;
import org.ilapin.arfloor.MainThreadExecutor;
import org.ilapin.arfloor.ModulesHolder;
import org.ilapin.arfloor.R;
import org.ilapin.arfloor.common.Observer;
import org.ilapin.arfloor.graphics.CelestialSphere;
import org.ilapin.arfloor.graphics.ColorMaterial;
import org.ilapin.arfloor.graphics.Plane;
import org.ilapin.arfloor.graphics.Scene;
import org.ilapin.arfloor.math.Coordinate3D;
import org.ilapin.arfloor.math.Vector3D;
import org.ilapin.arfloor.ui.widgets.NormalizedSensorVectorView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
	private static final int PERMISSIONS_REQUEST_CODE = 3034; // arbitrary

	private final GpsModule mGpsModule = ModulesHolder.getInstance().getGpsModule();
	private final MainThreadExecutor mMainThreadExecutor = ModulesHolder.getInstance().getMainThreadExecutor();

	private Compass mCompass;

	private SensorManager mSensorManager;
	private Display mDisplay;
	private NormalizedSensorVectorView mVectorView;

	private final Scene mScene = new Scene();
	private CelestialSphere mCelestialSphere;

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

			mScene.setCameraAngleY(-mCompass.getAzimuth());
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

	private final Observer mGpsModuleStateObserver = () -> {
		switch (mGpsModule.getState()) {
			case TRACKING:
				Log.d("!@#", "TRACKING");
				break;

			case CONNECTING:
				Log.d("!@#", "CONNECTING");
				break;

			case AWAITING_PERMISSIONS:
				Log.d("!@#", "AWAITING_PERMISSIONS");
				requestPermissions();
				break;

			case NOT_TRACKING:
				Log.d("!@#", "NOT_TRACKING");
				mGpsModule.startTracking();
				break;
		}
	};

	private final GpsModule.Listener mLocationListener = new GpsModule.Listener() {

		@Override
		public void onLocationReceived(final Location location) {
			mCelestialSphere.setNorthPoleAltitude((float) location.getLatitude());
		}
	};

	@SuppressWarnings({"ConstantConditions", "WrongConstant"})
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		mVectorView = (NormalizedSensorVectorView) findViewById(R.id.view_vector);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		mCompass = new Compass(mSensorManager);

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

		final InputStream turnedInsideSphereInputStream, sphereInputStream;
		try {
			turnedInsideSphereInputStream =
					new BufferedInputStream(getAssets().open("turned_inside_sphere_triangulated.ply"));
			sphereInputStream = new BufferedInputStream(getAssets().open("sphere.ply"));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		try {
			mCelestialSphere = new CelestialSphere(turnedInsideSphereInputStream, sphereInputStream);
			turnedInsideSphereInputStream.close();
			sphereInputStream.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		mCelestialSphere.setCelestialSphereMaterial(
				new ColorMaterial(ContextCompat.getColor(this, R.color.colorPrimary))
		);
		mCelestialSphere.setNorthPoleMarkerMaterial(new ColorMaterial(0xff000080));
		mCelestialSphere.setSouthPoleMarkerMaterial(new ColorMaterial(0xff800000));

		mScene.addBackgroundObject(mCelestialSphere);
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
			@NonNull final int[] grantResults) {
		Log.d("!@#", "onRequestPermissionsResult");

		if (requestCode == PERMISSIONS_REQUEST_CODE) {
			for (int i = 0; i < permissions.length; i++) {
				if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
						mGpsModule.getState() == GpsModule.State.AWAITING_PERMISSIONS) {
					if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
						mGpsModule.permissionsGranted();
					} else {
						requestPermissions();
					}
					return;
				}
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("!@#", "onStart");

		mGpsModule.registerObserver(mGpsModuleStateObserver, true, mMainThreadExecutor);
		mGpsModule.addListener(mLocationListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("!@#", "onResume");

		final Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		mSensorManager.registerListener(mSensorListener, sensor, SensorManager.SENSOR_DELAY_UI);

		mCompass.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("!@#", "onPause");

		mSensorManager.unregisterListener(mSensorListener);

		mCompass.stop();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("!@#", "onStop");

		mGpsModule.unregisterObserver(mGpsModuleStateObserver);
		if (mGpsModule.getState() == GpsModule.State.TRACKING) {
			mGpsModule.stopTracking();
		}
		mGpsModule.removeListener(mLocationListener);
	}

	private void requestPermissions() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
			ActivityCompat.requestPermissions(
					MainActivity.this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSIONS_REQUEST_CODE
			);
		}
	}
}
