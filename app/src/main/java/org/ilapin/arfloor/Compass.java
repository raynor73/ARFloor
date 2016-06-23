package org.ilapin.arfloor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Compass {
	private static final int STATISTICS_WINDOW_SIZE = 100;

	private final SensorManager mSensorManager;

	private final DescriptiveStatistics mAzimuthStatistics = new DescriptiveStatistics(STATISTICS_WINDOW_SIZE);
	private final DescriptiveStatistics mPitchStatistics = new DescriptiveStatistics(STATISTICS_WINDOW_SIZE);
	private final DescriptiveStatistics mRollStatistics = new DescriptiveStatistics(STATISTICS_WINDOW_SIZE);

	private float mAzimuth;
	private float mPitch;
	private float mRoll;

	private final float[] mMagneticFiledVector = new float[3];
	private final float[] mAccelerometerVector = new float[3];

	private final float[] mR = new float[9];
	private final float[] mI = new float[9];
	private final float[] mOrientation = new float[3];
	private final float[] mCameraRotation = new float[9];

	private final SensorEventListener mMagneticFieldListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(final SensorEvent sensorEvent) {
			System.arraycopy(sensorEvent.values, 0, mMagneticFiledVector, 0, 3);
			calculateAzimuth();
		}

		@Override
		public void onAccuracyChanged(final Sensor sensor, final int i) {
			// do nothing
		}
	};

	private final SensorEventListener mAccelerometerListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(final SensorEvent sensorEvent) {
			System.arraycopy(sensorEvent.values, 0, mAccelerometerVector, 0, 3);
			calculateAzimuth();
		}

		@Override
		public void onAccuracyChanged(final Sensor sensor, final int i) {
			// do nothing
		}
	};

	public Compass(final SensorManager sensorManager) {
		mSensorManager = sensorManager;
	}

	public void start() {
		mSensorManager.registerListener(
				mMagneticFieldListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME
		);

		mSensorManager.registerListener(
				mAccelerometerListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME
		);
	}

	public void stop() {
		mSensorManager.unregisterListener(mMagneticFieldListener);
		mSensorManager.unregisterListener(mAccelerometerListener);
	}

	public float getAzimuth() {
		return mAzimuth;
	}

	public float getPitch() {
		return mPitch;
	}

	public float getRoll() {
		return mRoll;
	}

	private void calculateAzimuth() {
		if (SensorManager.getRotationMatrix(mR, mI, mAccelerometerVector, mMagneticFiledVector)) {
			SensorManager.remapCoordinateSystem(mR, SensorManager.AXIS_X, SensorManager.AXIS_Z, mCameraRotation);
			SensorManager.getOrientation(mCameraRotation, mOrientation);

			mAzimuthStatistics.addValue(Math.toDegrees(mOrientation[0]));
			mPitchStatistics.addValue(Math.toDegrees(mOrientation[1]));
			mRollStatistics.addValue(Math.toDegrees(mOrientation[2]));

			mAzimuth = (float) mAzimuthStatistics.getMean();
			mPitch = (float) mPitchStatistics.getMean();
			mRoll = (float) mRollStatistics.getMean();
		}
	}
}
