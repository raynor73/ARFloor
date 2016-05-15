package org.ilapin.arfloor.graphics;

import java.io.IOException;
import java.io.InputStream;

public class CelestialSphere implements Renderable {
	private final RenderableMesh mNorthPoleMarker;
	private final RenderableMesh mSouthPoleMarker;

	public CelestialSphere(final InputStream turnedInsideSphereInputStream, final InputStream sphereInputStream)
			throws IOException {
//		mNorthPoleMarker = new RenderableMesh()
	}

	public void setCelestialSphereMaterial(final Material material) {

	}

	public void setNorthPoleMarkerMaterial(final Material material) {

	}

	public void setSouthPoleMarkerMaterial(final Material material) {

	}

	@Override
	public void render() {
		/*GLES11.glPushMatrix();
		GLES11.glScalef(1000, 1000, 1000);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);

		GLES11.glVertexPointer(NUMBER_OF_VERTEX_COORDINATES, GLES11.GL_FLOAT, 0, mVertexBuffer);
		GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, mNormalBuffer);
		mMaterial.apply();
		GLES11.glDrawElements(GLES11.GL_TRIANGLES, mNumberOfVertices, GLES11.GL_UNSIGNED_SHORT, mIndexBuffer);

		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY | GLES11.GL_NORMAL_ARRAY);
		GLES11.glPopMatrix();*/
	}
}
