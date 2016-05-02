package org.ilapin.arfloor.graphics;

import android.opengl.GLES11;

import java.nio.FloatBuffer;

public class CelestialSphere implements Renderable {

	private Material mMaterial;
	private final FloatBuffer mVertexBuffer;

	public CelestialSphere(final Material material) {
		mMaterial = material;
	}

	public void setMaterial(final Material material) {
		mMaterial = material;
	}

	@Override
	public void render() {
//		GLES11.glVertexPointer(NUMBER_OF_VERTEX_COORDINATES, GLES11.GL_FLOAT, 0, mVertexBuffer);
		GLES11.glNormalPointer();
	}
}
