package org.ilapin.arfloor.graphics;

import android.opengl.GLES11;

import org.ilapin.arfloor.math.Coordinate3D;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Scene implements Renderable {
	private volatile Coordinate3D mCameraAngles = new Coordinate3D();
	private volatile Coordinate3D mCameraPosition = new Coordinate3D();

	private final Set<Renderable> mBackgroundObjects = new CopyOnWriteArraySet<>();
	private final Set<Renderable> mSceneObjects = new CopyOnWriteArraySet<>();

	public void setCameraAngles(final Coordinate3D angles) {
		mCameraAngles = new Coordinate3D(angles);
	}

	public void setCameraAngleX(final float angleX) {
		mCameraAngles.setX(angleX);
	}

	public void setCameraAngleY(final float angleY) {
		mCameraAngles.setY(angleY);
	}

	public void setCameraAngleZ(final float angleZ) {
		mCameraAngles.setZ(angleZ);
	}

	public void setCameraPosition(final Coordinate3D position) {
		mCameraPosition = new Coordinate3D(position);
	}

	public void addSceneObject(final Renderable sceneObject) {
		mSceneObjects.add(sceneObject);
	}

	@Override
	public void render() {
		final Coordinate3D cameraAngles = new Coordinate3D(mCameraAngles);
		final Coordinate3D cameraPosition = new Coordinate3D(mCameraPosition);

		GLES11.glLoadIdentity();
		applyCameraRotation(cameraAngles);
		for (final Renderable renderable : mBackgroundObjects) {
			renderable.render();
		}
		GLES11.glClear(GLES11.GL_DEPTH_BUFFER_BIT);

		GLES11.glLoadIdentity();
		applyCameraRotation(cameraAngles);
		GLES11.glTranslatef(-cameraPosition.getX(), -cameraPosition.getY(), -cameraPosition.getZ());

		for (final Renderable renderable : mSceneObjects) {
			renderable.render();
		}
	}

	private void applyCameraRotation(final Coordinate3D cameraAngles) {
		GLES11.glRotatef(-cameraAngles.getX(), 1.0f, 0.0f, 0.0f);
		GLES11.glRotatef(-cameraAngles.getY(), 0.0f, 1.0f, 0.0f);
		GLES11.glRotatef(-cameraAngles.getZ(), 0.0f, 0.0f, 1.0f);
	}
}
