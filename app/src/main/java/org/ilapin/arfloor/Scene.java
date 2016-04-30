package org.ilapin.arfloor;

import android.opengl.GLES11;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Scene implements Renderable {
	private volatile float mCameraAngleX;
	private volatile float mCameraAngleY;
	private volatile float mCameraAngleZ;
	
	private volatile float mCameraX;
	private volatile float mCameraY;
	private volatile float mCameraZ;

	private final Set<Renderable> mSceneObjects = new CopyOnWriteArraySet<>();

	public void setCameraAngles(final float x, final float y, final float z) {
		mCameraAngleX = x;
		mCameraAngleY = y;
		mCameraAngleZ = z;
	}
	
	public void setCameraPosition(final float x, final float y, final float z) {
		mCameraX = x;
		mCameraY = y;
		mCameraZ = z;
	}

	public void addSceneObject(final Renderable sceneObject) {
		mSceneObjects.add(sceneObject);
	}

	@Override
	public void render() {
		final float cameraX = mCameraX;
		final float cameraY = mCameraY;
		final float cameraZ = mCameraZ;
		
		final float cameraAngleX = mCameraAngleX;
		final float cameraAngleY = mCameraAngleY;
		final float cameraAngleZ = mCameraAngleZ;
		
		for (final Renderable renderable : mSceneObjects) {
			GLES11.glLoadIdentity();
			GLES11.glRotatef(-cameraAngleX, 1.0f, 0.0f, 0.0f);
			GLES11.glRotatef(-cameraAngleY, 0.0f, 1.0f, 0.0f);
			GLES11.glRotatef(-cameraAngleZ, 0.0f, 0.0f, 1.0f);
			GLES11.glTranslatef(-cameraX, -cameraY, -cameraZ);

			renderable.render();
		}
	}
}
