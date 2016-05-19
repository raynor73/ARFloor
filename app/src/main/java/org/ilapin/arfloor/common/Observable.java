package org.ilapin.arfloor.common;

import java.util.concurrent.ExecutorService;

public interface Observable {

	void registerObserver(Observer observer, boolean notifyImmediately);

	void registerObserver(Observer observer, boolean notifyImmediately, ExecutorService executorService);

	void unregisterObserver(Observer observer);
}
