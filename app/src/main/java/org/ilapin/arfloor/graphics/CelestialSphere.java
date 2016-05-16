package org.ilapin.arfloor.graphics;

import android.opengl.GLES11;

import java.io.IOException;
import java.io.InputStream;

public class CelestialSphere implements Renderable {
	private final RenderableMesh mCelestialSphere;
	private final RenderableMesh mNorthPoleMarker;
	private final RenderableMesh mSouthPoleMarker;

	public CelestialSphere(final InputStream turnedInsideSphereInputStream, final InputStream sphereInputStream)
			throws IOException {
		mCelestialSphere = new RenderableMesh(turnedInsideSphereInputStream);
		mNorthPoleMarker = new RenderableMesh(sphereInputStream);
		mSouthPoleMarker = new RenderableMesh(mNorthPoleMarker);
	}

	public void setCelestialSphereMaterial(final Material material) {
		mCelestialSphere.setMaterial(material);
	}

	public void setNorthPoleMarkerMaterial(final Material material) {
		mNorthPoleMarker.setMaterial(material);
	}

	public void setSouthPoleMarkerMaterial(final Material material) {
		mSouthPoleMarker.setMaterial(material);
	}

	@Override
	public void render() {
		GLES11.glPushMatrix();
		GLES11.glScalef(1000, 1000, 1000);
		mCelestialSphere.render();
		GLES11.glPopMatrix();

		GLES11.glPushMatrix();
		GLES11.glScalef(100, 100, 100);
		GLES11.glTranslatef(-1, 1, -500);
		mSouthPoleMarker.render();
		GLES11.glPopMatrix();

		GLES11.glPushMatrix();
		GLES11.glScalef(10, 10, 10);
		GLES11.glTranslatef(1, 1, -100);
		mNorthPoleMarker.render();
		GLES11.glPopMatrix();
	}
}
