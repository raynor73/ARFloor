package org.ilapin.arfloor.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import org.ilapin.arfloor.MainThreadExecutor;
import org.ilapin.arfloor.ModulesHolder;
import org.ilapin.arfloor.PermissionsModule;
import org.ilapin.arfloor.common.Observer;

import java.util.concurrent.Executor;

public class BaseActivity extends AppCompatActivity {

	private final PermissionsModule mPermissionsModule = ModulesHolder.getInstance().getPermissionsModule();
	private final MainThreadExecutor mMainThreadExecutor = ModulesHolder.getInstance().getMainThreadExecutor();

	private final Observer mPermissionsModuleObserver = new Observer() {

		@Override
		public void notifyChange() {
			if (mPermissionsModule.getState() == PermissionsModule.State.INSUFFICIENT_PERMISSIONS) {
				mPermissionsModule.requestPermissions(BaseActivity.this);
			}
		}
	};

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPermissionsModule.registerObserver(mPermissionsModuleObserver, true, mMainThreadExecutor);
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
			@NonNull final int[] grantResults) {
		mPermissionsModule.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
	}

	public Executor getMainThreadExecutor() {
		return mMainThreadExecutor;
	}
}
