package org.ilapin.arfloor.common;

import com.google.common.base.Preconditions;

public class StatefullObservable<T> extends BaseObservable {

	private T mState;

	public StatefullObservable(final T initialState) {
		mState = initialState;
	}

	public T getState() {
		return mState;
	}

	protected void changeState(final T newState) {
		Preconditions.checkState(mState != newState);

		mState = newState;
		notifyObservers();
	}
}
