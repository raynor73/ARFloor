package org.ilapin.arfloor.gps;

import org.ilapin.arfloor.common.BaseObservable;
import org.ilapin.arfloor.common.Observable;
import org.ilapin.arfloor.common.Observer;

import java.util.concurrent.ExecutorService;

public class GpsModule implements Observable {

	private final Observable mObservable = new BaseObservable();

	@Override
	public void registerObserver(final Observer observer, final boolean notifyImmediately) {
		mObservable.registerObserver(observer, notifyImmediately);
	}

	@Override
	public void registerObserver(final Observer observer, final boolean notifyImmediately,
			final ExecutorService executorService) {
		mObservable.registerObserver(observer, notifyImmediately, executorService);
	}

	@Override
	public void unregisterObserver(final Observer observer) {
		mObservable.unregisterObserver(observer);
	}
}
