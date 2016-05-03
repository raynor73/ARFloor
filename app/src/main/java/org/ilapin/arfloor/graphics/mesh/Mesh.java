package org.ilapin.arfloor.graphics.mesh;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.ilapin.arfloor.common.DefensiveCopyIterator;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Mesh {
	private final List<Vertex> mVertices = Lists.newArrayList();
	private final List<Vertex> mNormals = Lists.newArrayList();
	private final Set<Face> mFaces = Sets.newHashSet();

	public void addVertex(final Vertex vertex) {
		mVertices.add(new Vertex(vertex));
	}

	public void addNormal(final Vertex normal) {
		mNormals.add(new Vertex(normal));
	}

	public void addFace(final Face face) {
		mFaces.add(new Face(face));
	}

	public Vertex getVertex(final int i) {
		return new Vertex(mVertices.get(i));
	}

	public Vertex getNormal(final int i) {
		return new Vertex(mNormals.get(i));
	}

	public Iterator<Vertex> getVerticesIterator() {
		return new VertexIterator(mVertices.iterator());
	}

	public Iterator<Vertex> getNormalsIterator() {
		return new VertexIterator(mNormals.iterator());
	}

	public Iterator<Face> getFacesIterator() {
		return new DefensiveCopyIterator<Face>(mFaces.iterator()) {

			@Override
			protected Face copy(final Face element) {
				return new Face(element);
			}
		};
	}

	public int getNumberOfVertices() {
		return mVertices.size();
	}

	public int getNumberOfNormals() {
		return mNormals.size();
	}

	public int getNumberOfIndices() {
		int numberOfIndices = 0;
		for (final Face face : mFaces) {
			numberOfIndices += face.getNumberOfIndexes();
		}
		return numberOfIndices;
	}
}
