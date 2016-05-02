package org.ilapin.arfloor.common;

import java.util.Iterator;

public abstract class DefensiveCopyIterator<T> implements Iterator<T> {

	private final Iterator<T> mIterator;

	public DefensiveCopyIterator(final Iterator<T> iterator) {
		mIterator = iterator;
	}

	@Override
	public boolean hasNext() {
		return mIterator.hasNext();
	}

	@Override
	public T next() {
		return copy(mIterator.next());
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	protected abstract T copy(final T element);
}
