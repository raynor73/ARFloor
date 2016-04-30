package org.ilapin.arfloor;

import android.opengl.GLES11;

public class ColorMaterial implements Material {

	private final int mColor;

	public ColorMaterial(final int color) {
		mColor = color;
	}

	@Override
	public void apply() {
		GLES11.glColor4ub(
				(byte) ((0x00ff0000 & mColor) >> 16),
				(byte) ((0x0000ff00 & mColor) >> 8),
				(byte) (0x000000ff & mColor),
				(byte) ((0xff000000 & mColor) >> 24)
		);
	}
}
