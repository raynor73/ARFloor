package org.ilapin.arfloor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class MainThreadExecutor implements Executor {

	private final Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	public void execute(@NonNull final Runnable command) {
		if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
			command.run();
		} else {
			mHandler.post(command);
		}
	}
}
