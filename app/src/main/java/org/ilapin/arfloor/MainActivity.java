package org.ilapin.arfloor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	private SensorManager mSensorManager;

	private NormalizedVectorView mVectorView;

	private final SensorEventListener mSensorListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(final SensorEvent event) {
			mVectorView.setVector(-event.values[0], event.values[1], event.values[2]);
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

		mVectorView = (NormalizedVectorView) findViewById(R.id.view_vector);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		final Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(mSensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mSensorListener);
	}
}
