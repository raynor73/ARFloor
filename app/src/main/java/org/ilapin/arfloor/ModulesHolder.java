package org.ilapin.arfloor;

import android.content.Context;
import com.google.common.base.Preconditions;

public class ModulesHolder {
	private static ModulesHolder sInstance;

	private final GpsModule mGpsModule;
	private final PermissionsModule mPermissionsModule;
	private final MainThreadExecutor mMainThreadExecutor;

	public static void createInstance(final Context context) {
		Preconditions.checkState(sInstance == null);

		sInstance = new ModulesHolder(context);
	}

	public static ModulesHolder getInstance() {
		Preconditions.checkState(sInstance != null);

		return sInstance;
	}

	private ModulesHolder(final Context context) {
		mGpsModule = new GpsModule(context);
		mPermissionsModule = new PermissionsModule();
		mMainThreadExecutor = new MainThreadExecutor();
	}

	public GpsModule getGpsModule() {
		return mGpsModule;
	}

	public PermissionsModule getPermissionsModule() {
		return mPermissionsModule;
	}

	public MainThreadExecutor getMainThreadExecutor() {
		return mMainThreadExecutor;
	}
}
