package org.ilapin.arfloor.common;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ExecutorService;

public class BaseObservable implements Observable {

	private final Map<Observer, ExecutorService> mObservers = Maps.newHashMap();

	@Override
	public void registerObserver(final Observer observer, final boolean notifyImmediately) {
		registerObserver(observer, notifyImmediately, null);
	}

	@Override
	public void registerObserver(final Observer observer, final boolean notifyImmediately,
			final ExecutorService executorService) {
		if (mObservers.containsKey(observer)) {
			final String message = String.format(
					"Observable %s already contains observer %s",
					this.toString(),
					observer.toString()
			);
			throw new IllegalArgumentException(message);
		}
		mObservers.put(observer, executorService);
		if (notifyImmediately) {
			notifyObserver(observer, executorService);
		}
	}

	@Override
	public void unregisterObserver(final Observer observer) {
		if (!mObservers.containsKey(observer)) {
			final String message = String.format(
					"Observable %s doesn't contain observer %s",
					this.toString(),
					observer.toString()
			);
			throw new IllegalArgumentException(message);
		}
		mObservers.remove(observer);
	}

	public void notifyObservers() {
		for (final Map.Entry<Observer, ExecutorService> entry : mObservers.entrySet()) {
			notifyObserver(entry.getKey(), entry.getValue());
		}
	}

	private void notifyObserver(final Observer observer, final ExecutorService executorService) {
		if (executorService != null) {
			executorService.submit(new Runnable() {
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
