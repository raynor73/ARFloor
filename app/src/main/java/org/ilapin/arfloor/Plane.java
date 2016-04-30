package org.ilapin.arfloor;

import android.opengl.GLES11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Plane implements Renderable {
	private static final int NUMBER_OF_SQUARES = 10;
	private static final int SIZE_OF_SQUARE = 10;
	private static final int SIZE_OF_FLOAT = 4;
	private static final int NUMBER_OF_VERTEX_COORDINATES = 3;

	private Material mMaterial;
	private final float[] mVertices;
	private final FloatBuffer mVertexBuffer;

	public Plane(final Material material) {
		mMaterial = material;

		final float halfSize = NUMBER_OF_SQUARES * SIZE_OF_SQUARE;
		mVertices = new float[] {
				-halfSize, -halfSize, 0,
				halfSize, -halfSize, 0,
				-halfSize, halfSize, 0,
				halfSize, halfSize, 0
		};

		final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(mVertices.length * SIZE_OF_FLOAT);
		byteBuffer.order(ByteOrder.nativeOrder());
		mVertexBuffer = byteBuffer.asFloatBuffer();
		mVertexBuffer.put(mVertices);
		mVertexBuffer.position(0);
	}

	public void setMaterial(final Material material) {
		mMaterial = material;
	}

	@Override
	public void render() {
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glVertexPointer(NUMBER_OF_VERTEX_COORDINATES, GLES11.GL_FLOAT, 0, mVertexBuffer);
		mMaterial.apply();
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, mVertices.length / NUMBER_OF_VERTEX_COORDINATES);
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
	}
}
