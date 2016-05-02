package org.ilapin.arfloor.graphics.mesh;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.util.Iterator;
import java.util.List;

public class Face {
	private final List<Vertex> mVertexes;
	private final int[] mIndexes;

	public Face(final Face face) {
		mVertexes = Lists.newArrayList();
		mIndexes = new int[face.getNumberOfIndexes()];

		final Iterator<Integer> indexesIterator = face.getIndexesIterator();
		for (int i = 0; indexesIterator.hasNext(); i++) {
			final int index = indexesIterator.next();
			mIndexes[i] = index;
		}

		final Iterator<Vertex> verticesIterator = face.getVerticesIterator();
		while (verticesIterator.hasNext()) {
			final Vertex vertex = verticesIterator.next();
			mVertexes.add(new Vertex(vertex));
		}
	}

	public Face(final List<Vertex> vertexes, final int[] indexes) {
		mVertexes = Lists.newArrayList();
		mIndexes = new int[indexes.length];

		for (final Vertex vertex : vertexes) {
			mVertexes.add(new Vertex(vertex));
		}

		System.arraycopy(indexes, 0, mIndexes, 0, indexes.length);
	}

	public Iterator<Vertex> getVerticesIterator() {
		return new VertexIterator(mVertexes.iterator());
	}

	public Iterator<Integer> getIndexesIterator() {
		return Ints.asList(mIndexes).iterator();
	}

	public int getNumberOfIndexes() {
		return mIndexes.length;
	}
}
