package org.ilapin.arfloor.graphics.mesh;

import org.ilapin.arfloor.math.Coordinate3D;

public class Vertex extends Coordinate3D {

	public Vertex() {}

	public Vertex(final float x, final float y, final float z) {
		super(x, y, z);
	}

	public Vertex(final Coordinate3D coordinate) {
		super(coordinate);
	}
}
