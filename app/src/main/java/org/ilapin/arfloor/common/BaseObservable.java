package org.ilapin.arfloor.common;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.Executor;

public class BaseObservable implements Observable {

	private final Map<Observer, Executor> mObservers = Maps.newHashMap();

	@Override
	public void registerObserver(final Observer observer, final boolean notifyImmediately) {
		registerObserver(observer, notifyImmediately, null);
	}

	@Override
	public void registerObserver(final Observer observer, final boolean notifyImmediately,
			final Executor executor) {
		Preconditions.checkState(!mObservers.containsKey(observer));

		mObservers.put(observer, executor);
		if (notifyImmediately) {
			notifyObserver(observer, executor);
		}
	}

	@Override
	public void unregisterObserver(final Observer observer) {
		Preconditions.checkState(mObservers.containsKey(observer));

		mObservers.remove(observer);
	}

	public void notifyObservers() {
		for (final Map.Entry<Observer, Executor> entry : mObservers.entrySet()) {
			notifyObserver(entry.getKey(), entry.getValue());
		}
	}

	private void notifyObserver(final Observer observer, final Executor executor) {
		if (executor != null) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					observer.notifyChange();
				}
			});
		} else {
			observer.notifyChange();
		}
	}
}
