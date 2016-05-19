package org.ilapin.arfloor;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.google.common.base.Preconditions;
import org.ilapin.arfloor.common.StatefullObservable;

public class PermissionsModule extends StatefullObservable<PermissionsModule.State> {
	private static final int PERMISSIONS_REQUEST_CODE = 3430; // arbitrary number

	private final String[] mRequiredPermissions = {
			Manifest.permission.ACCESS_FINE_LOCATION
	};

	public PermissionsModule() {
		super(State.INSUFFICIENT_PERMISSIONS);
	}

	public void requestPermissions(final Activity activity) {
		ActivityCompat.requestPermissions(activity, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
		changeState(State.AWAITING_PERMISSIONS_CONFIRMATION);
	}

	public void onRequestPermissionsResult(final Activity activity, final int requestCode, final String[] permissions,
			final int[] grantResults) {
		if (requestCode != PERMISSIONS_REQUEST_CODE) {
			return;
		}

		Preconditions.checkState(getState() == State.AWAITING_PERMISSIONS_CONFIRMATION);

		for (final String permission : mRequiredPermissions) {
			if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
					changeState(State.INSUFFICIENT_PERMISSIONS);
				} else {
					changeState(State.REQUIRED_PERMISSIONS_DENIED);
				}
				return;
			}
		}

		changeState(State.REQUIRED_PERMISSIONS_GRANTED);
	}

	public enum State {
		INSUFFICIENT_PERMISSIONS,
		REQUIRED_PERMISSIONS_GRANTED,
		REQUIRED_PERMISSIONS_DENIED,
		AWAITING_PERMISSIONS_CONFIRMATION
	}
}
