package org.ilapin.arfloor.graphics;

import android.opengl.GLES11;

import org.ilapin.arfloor.math.Coordinate3D;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Plane implements Renderable {
	private static final int NUMBER_OF_SQUARES = 10;
	private static final int SIZE_OF_SQUARE = 10;
	private static final int SIZE_OF_FLOAT = 4;
	private static final int NUMBER_OF_VERTEX_COORDINATES = 3;

	private Material mMaterial;
	private Coordinate3D mPosition = new Coordinate3D();
	private final float[] mVertices;
	private final FloatBuffer mVertexBuffer;

	public Plane(final Material material) {
		mMaterial = material;

		final float halfSize = NUMBER_OF_SQUARES * SIZE_OF_SQUARE;
		mVertices = new float[] {
				-halfSize, 0, -halfSize,
				halfSize, 0, -halfSize,
				-halfSize, 0, halfSize,
				halfSize, 0, halfSize
		};

		final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(mVertices.length * SIZE_OF_FLOAT);
		byteBuffer.order(ByteOrder.nativeOrder());
		mVertexBuffer = byteBuffer.asFloatBuffer();
		mVertexBuffer.put(mVertices);
		mVertexBuffer.position(0);
	}

	public Coordinate3D getPosition() {
		return new Coordinate3D(mPosition);
	}

	public void setPositinon(final Coordinate3D position) {
		mPosition = new Coordinate3D(position);
	}

	public void setMaterial(final Material material) {
		mMaterial = material;
	}

	@Override
	public void render() {
		GLES11.glPushMatrix();
		GLES11.glTranslatef(mPosition.getX(), mPosition.getY(), mPosition.getZ());

		/*GLES11.glRotatef(viewAngleX, 1.0f, 0.0f, 0.0f);
		GLES11.glRotatef(viewAngleY, 0.0f, 1.0f, 0.0f);
		GLES11.glRotatef(viewAngleZ, 0.0f, 0.0f, 1.0f);*/

		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glVertexPointer(NUMBER_OF_VERTEX_COORDINATES, GLES11.GL_FLOAT, 0, mVertexBuffer);
		mMaterial.apply();
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, mVertices.length / NUMBER_OF_VERTEX_COORDINATES);
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);

		GLES11.glPopMatrix();
	}
}
