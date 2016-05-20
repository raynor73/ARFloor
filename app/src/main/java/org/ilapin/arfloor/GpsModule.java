package org.ilapin.arfloor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.ilapin.arfloor.common.StatefullObservable;

import java.util.Set;

public class GpsModule extends StatefullObservable<GpsModule.State> {
	private static final long DESIRED_INTERVAL = 1000; // millis
	private static final long FASTEST_INTERVAL = 1000; // millis

	private final Context mContext;
	private final Set<Listener> mListeners = Sets.newHashSet();
	private final GoogleApiClient mGoogleApiClient;

	private final GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {

		@Override
		public void onConnected(@Nullable final Bundle bundle) {
			Preconditions.checkState(getState() != State.TRACKING, getState());

			if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				changeState(State.TRACKING);
				requestLocationUpdates();
			} else {
				changeState(State.AWAITING_PERMISSIONS);
			}
		}

		@Override
		public void onConnectionSuspended(final int i) {
			Preconditions.checkState(getState() == State.TRACKING, getState());

			changeState(State.CONNECTING);
		}
	};

	private final GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener =
			new GoogleApiClient.OnConnectionFailedListener() {

		@Override
		public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
			Preconditions.checkState(getState() == State.CONNECTING, getState());

			changeState(State.NOT_TRACKING);
		}
	};

	private final LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(final Location location) {
			notifyLocation(location);
		}
	};

	public GpsModule(final Context context) {
		super(State.NOT_TRACKING);

		mContext = context;
		mGoogleApiClient = new GoogleApiClient.Builder(mContext)
				.addConnectionCallbacks(mConnectionCallbacks)
				.addOnConnectionFailedListener(mConnectionFailedListener)
				.addApi(LocationServices.API)
				.build();
	}

	public void startTracking() {
		Preconditions.checkState(getState() == State.NOT_TRACKING, getState());
		changeState(State.CONNECTING);
		mGoogleApiClient.connect();
	}

	public void stopTracking() {
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
		changeState(State.NOT_TRACKING);
	}

	public void addListener(final Listener listener) {
		Preconditions.checkState(!mListeners.contains(listener));

		mListeners.add(listener);
	}

	public void removeListener(final Listener listener) {
		Preconditions.checkState(mListeners.contains(listener));

		mListeners.remove(listener);
	}

	public void permissionsGranted() {
		if (getState() == State.AWAITING_PERMISSIONS) {
			changeState(State.TRACKING);
			requestLocationUpdates();
		}
	}

	private void notifyLocation(final Location location) {
		Preconditions.checkState(getState() == State.TRACKING, getState());

		for (final Listener listener : mListeners) {
			listener.onLocationReceived(location);
		}
	}

	@SuppressWarnings("MissingPermission")
	private void requestLocationUpdates() {
		final LocationRequest locationRequest = new LocationRequest();
		locationRequest.setInterval(DESIRED_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient,
				locationRequest,
				mLocationListener
		);
	}

	public interface Listener {

		void onLocationReceived(Location location);
	}

	public enum State {
		TRACKING, CONNECTING, AWAITING_PERMISSIONS, NOT_TRACKING
	}
}
