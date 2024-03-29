package org.ilapin.arfloor.graphics;

import android.opengl.GLES11;

import java.io.IOException;
import java.io.InputStream;

public class CelestialSphere implements Renderable {
	private final RenderableMesh mCelestialSphere;
	private final RenderableMesh mNorthPoleMarker;
	private final RenderableMesh mSouthPoleMarker;

	private volatile float mNorthPoleAltitude;

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

	public void setNorthPoleAltitude(final float northPoleAltitude) {
		mNorthPoleAltitude = northPoleAltitude;
	}

	@Override
	public void render() {
		GLES11.glPushMatrix();
		GLES11.glRotatef(mNorthPoleAltitude - 90, 1, 0, 0);

		GLES11.glPushMatrix();
		GLES11.glScalef(1000, 1000, 1000);
		mCelestialSphere.render();
		GLES11.glPopMatrix();

		GLES11.glPushMatrix();
		GLES11.glTranslatef(0, 1000, 0);
		GLES11.glScalef(50, 50, 50);
		mNorthPoleMarker.render();
		GLES11.glPopMatrix();

		GLES11.glPushMatrix();
		GLES11.glTranslatef(0, -1000, 0);
		GLES11.glScalef(50, 50, 50);
		mSouthPoleMarker.render();
		GLES11.glPopMatrix();

		GLES11.glPopMatrix();
	}
}
