package org.ilapin.arfloor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

	private SensorManager mSensorManager;
	private Display mDisplay;
	private NormalizedSensorVectorView mVectorView;

	private final SensorEventListener mSensorListener = new SensorEventListener() {

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
		}

		@Override
		public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
			// do nothing
		}
	};

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		mVectorView = (NormalizedSensorVectorView) findViewById(R.id.view_vector);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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
