package org.ilapin.arfloor;

import android.opengl.GLES11;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Scene implements Renderable {
	private volatile Coordinate3D mCameraAngles = new Coordinate3D();
	private volatile Coordinate3D mCameraPosition = new Coordinate3D();

	private final Set<Renderable> mSceneObjects = new CopyOnWriteArraySet<>();

	public void setCameraAngles(final Coordinate3D angles) {
		mCameraAngles = new Coordinate3D(angles);
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

		for (final Renderable renderable : mSceneObjects) {
			GLES11.glLoadIdentity();
			GLES11.glRotatef(-cameraAngles.getX(), 1.0f, 0.0f, 0.0f);
			GLES11.glRotatef(-cameraAngles.getY(), 0.0f, 1.0f, 0.0f);
			GLES11.glRotatef(-cameraAngles.getZ(), 0.0f, 0.0f, 1.0f);
			GLES11.glTranslatef(-cameraPosition.getX(), -cameraPosition.getY(), -cameraPosition.getZ());

			renderable.render();
		}
	}
}
