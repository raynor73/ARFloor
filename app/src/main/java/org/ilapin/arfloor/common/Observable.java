package org.ilapin.arfloor.common;

import java.util.concurrent.ExecutorService;

public interface Observable {

	void registerObserver(Observer observer);

	void registerObserver(Observer observer, ExecutorService executorService);

	void unregisterObserver(Observer observer);
}
