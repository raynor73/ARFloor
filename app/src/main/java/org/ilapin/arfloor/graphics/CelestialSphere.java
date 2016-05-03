package org.ilapin.arfloor.graphics;

import android.opengl.GLES11;
import org.ilapin.arfloor.graphics.mesh.Face;
import org.ilapin.arfloor.graphics.mesh.Mesh;
import org.ilapin.arfloor.graphics.mesh.MeshFactory;
import org.ilapin.arfloor.graphics.mesh.Vertex;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;

public class CelestialSphere implements Renderable {
	private static final int NUMBER_OF_VERTEX_COORDINATES = 3;
	private static final int NUMBER_OF_NORMAL_COORDINATES = 3;
	private static final int SIZE_OF_FLOAT = 4;
	private static final int SIZE_OF_SHORT = 2;

	private Material mMaterial;
	private final int mNumberOfVertices;
	private final FloatBuffer mVertexBuffer;
	private final FloatBuffer mNormalBuffer;
	private final ShortBuffer mIndexBuffer;

	public CelestialSphere(final Material material, final InputStream inputStream) throws IOException {
		mMaterial = material;

		final Mesh mesh = MeshFactory.createFromStream(inputStream);

		mNumberOfVertices = mesh.getNumberOfVertices();

		final float[] vertexCoordinates = new float[mNumberOfVertices * NUMBER_OF_VERTEX_COORDINATES];
		final Iterator<Vertex> verticesIterator = mesh.getVerticesIterator();
		for (int i = 0; verticesIterator.hasNext(); i++) {
			final Vertex vertex = verticesIterator.next();
			final int base = i * NUMBER_OF_VERTEX_COORDINATES;
			vertexCoordinates[base] = vertex.getX();
			vertexCoordinates[base + 1] = vertex.getY();
			vertexCoordinates[base + 2] = vertex.getZ();
		}
		final ByteBuffer verticesByteBuffer = ByteBuffer.allocateDirect(vertexCoordinates.length * SIZE_OF_FLOAT);
		verticesByteBuffer.order(ByteOrder.nativeOrder());
		mVertexBuffer = verticesByteBuffer.asFloatBuffer();
		mVertexBuffer.put(vertexCoordinates);
		mVertexBuffer.position(0);

		final float[] normalCoordinates = new float[mesh.getNumberOfNormals() * NUMBER_OF_NORMAL_COORDINATES];
		final Iterator<Vertex> normalsIterator = mesh.getNormalsIterator();
		for (int i = 0; normalsIterator.hasNext(); i++) {
			final Vertex normal = normalsIterator.next();
			final int base = i * NUMBER_OF_NORMAL_COORDINATES;
			normalCoordinates[base] = normal.getX();
			normalCoordinates[base + 1] = normal.getY();
			normalCoordinates[base + 2] = normal.getZ();
		}
		final ByteBuffer normalsByteBuffer = ByteBuffer.allocateDirect(normalCoordinates.length * SIZE_OF_FLOAT);
		normalsByteBuffer.order(ByteOrder.nativeOrder());
		mNormalBuffer = normalsByteBuffer.asFloatBuffer();
		mNormalBuffer.put(normalCoordinates);
		mNormalBuffer.position(0);

		final short[] indices = new short[mesh.getNumberOfIndices()];
		final Iterator<Short> indicesIterator = new IndicesIterator(mesh);
		for (int i = 0; indicesIterator.hasNext(); i++) {
			indices[i] = indicesIterator.next();
		}
		final ByteBuffer indicesByteBuffer = ByteBuffer.allocateDirect(indices.length * SIZE_OF_SHORT);
		indicesByteBuffer.order(ByteOrder.nativeOrder());
		mIndexBuffer = indicesByteBuffer.asShortBuffer();
		mIndexBuffer.put(indices);
		mIndexBuffer.position(0);
	}

	public void setMaterial(final Material material) {
		mMaterial = material;
	}

	@Override
	public void render() {
		GLES11.glPushMatrix();
		GLES11.glScalef(1000, 1000, 1000);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY /*| GLES11.GL_NORMAL_ARRAY*/);

		GLES11.glVertexPointer(NUMBER_OF_VERTEX_COORDINATES, GLES11.GL_FLOAT, 0, mVertexBuffer);
		GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, mNormalBuffer);
		mMaterial.apply();
		GLES11.glDrawElements(GLES11.GL_TRIANGLES, mNumberOfVertices, GLES11.GL_UNSIGNED_SHORT, mIndexBuffer);
//		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, mVertexCoordinates.length / NUMBER_OF_VERTEX_COORDINATES);

		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY | GLES11.GL_NORMAL_ARRAY);
		GLES11.glPopMatrix();
	}

	private static class IndicesIterator implements Iterator<Short> {

		private final Iterator<Face> mFacesIterator;
		private Iterator<Integer> mCurrentFaceIndicesIterator;

		public IndicesIterator(final Mesh mesh) {
			mFacesIterator = mesh.getFacesIterator();
			mCurrentFaceIndicesIterator = mFacesIterator.next().getIndexesIterator();
		}

		@Override
		public boolean hasNext() {
			return mFacesIterator.hasNext() || (mCurrentFaceIndicesIterator != null && mCurrentFaceIndicesIterator.hasNext());
		}

		@Override
		public Short next() {
			if (mCurrentFaceIndicesIterator.hasNext()) {
				return mCurrentFaceIndicesIterator.next().shortValue();
			} else {
				mCurrentFaceIndicesIterator = mFacesIterator.next().getIndexesIterator();
				return mCurrentFaceIndicesIterator.next().shortValue();
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
