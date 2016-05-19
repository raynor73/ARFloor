package org.ilapin.arfloor;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.ilapin.arfloor.common.StatefullObservable;

import java.util.Set;

@SuppressWarnings("MissingPermission")
public class GpsModule extends StatefullObservable<GpsModule.State> {
	private static final long DESIRED_INTERVAL = 1000; // millis
	private static final long FASTEST_INTERVAL = 1000; // millis

	private final Set<Listener> mListeners = Sets.newHashSet();
	private final GoogleApiClient mGoogleApiClient;

	private final GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {

		@Override
		public void onConnected(@Nullable final Bundle bundle) {
			Preconditions.checkState(getState() != State.TRACKING);

			changeState(State.TRACKING);

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

		@Override
		public void onConnectionSuspended(final int i) {
			Preconditions.checkState(getState() == State.TRACKING);

			changeState(State.CONNECTING);
		}
	};

	private final GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener =
			new GoogleApiClient.OnConnectionFailedListener() {

		@Override
		public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
			Preconditions.checkState(getState() == State.CONNECTING);

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

		mGoogleApiClient = new GoogleApiClient.Builder(context)
				.addConnectionCallbacks(mConnectionCallbacks)
				.addOnConnectionFailedListener(mConnectionFailedListener)
				.addApi(LocationServices.API)
				.build();
	}

	public void startTracking() {
		Preconditions.checkState(getState() == State.NOT_TRACKING);


	}

	public void addListener(final Listener listener) {
		Preconditions.checkState(!mListeners.contains(listener));

		mListeners.add(listener);
	}

	public void removeListener(final Listener listener) {
		Preconditions.checkState(mListeners.contains(listener));

		mListeners.remove(listener);
	}

	private void notifyLocation(final Location location) {
		for (final Listener listener : mListeners) {
			listener.onLocationReceived(location);
		}
	}

	public interface Listener {

		void onLocationReceived(Location location);
	}

	public enum State {
		TRACKING, CONNECTING, NOT_TRACKING
	}
}
