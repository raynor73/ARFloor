package org.ilapin.arfloor.common;

import java.util.concurrent.Executor;

public interface Observable {

	void registerObserver(Observer observer, boolean notifyImmediately);

	void registerObserver(Observer observer, boolean notifyImmediately, Executor executorService);

	void unregisterObserver(Observer observer);
}
