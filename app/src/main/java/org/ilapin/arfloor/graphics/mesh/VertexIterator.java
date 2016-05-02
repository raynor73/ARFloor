package org.ilapin.arfloor.graphics.mesh;

import org.ilapin.arfloor.common.DefensiveCopyIterator;

import java.util.Iterator;

public class VertexIterator extends DefensiveCopyIterator<Vertex> {

	public VertexIterator(final Iterator<Vertex> iterator) {
		super(iterator);
	}

	@Override
	protected Vertex copy(final Vertex element) {
		return new Vertex(element);
	}
}
