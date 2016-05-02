package org.ilapin.arfloor.graphics.mesh;

import com.google.common.collect.Lists;

import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MeshFactory {
	public static Mesh createFromFile(final File file) throws IOException {
		return createFromFile(file, null);
	}

	public static Mesh createFromFile(final File file, final OnVertexAddedListener listener) throws IOException {
		final Mesh mesh = new Mesh();
		final PlyReader plyReader = new PlyReaderFile(new FileInputStream(file));
		ElementReader elementReader = plyReader.nextElementReader();
		while (elementReader != null) {
			Element element = elementReader.readElement();
			while (element != null) {
				final ElementType type = element.getType();

				if (type.getName().equals("vertex")) {
					final Vertex vertex = new Vertex(
							(float) element.getDouble("x"),
							(float) element.getDouble("y"),
							(float) element.getDouble("z")
					);
					final Vertex normal = new Vertex(
							(float) element.getDouble("nx"),
							(float) element.getDouble("ny"),
							(float) element.getDouble("nz")
					);
					mesh.addVertex(vertex);
					mesh.addNormal(normal);

					if (listener != null) {
						listener.onVertexAdded(element);
					}
				} else if (type.getName().equals("face")) {
					final List<Vertex> vertices = Lists.newArrayList();
					final Iterator<Vertex> verticesIterator = mesh.getVerticesIterator();
					while (verticesIterator.hasNext()) {
						vertices.add(verticesIterator.next());
					}
					mesh.addFace(new Face(vertices, element.getIntList("vertex_indices")));
				}

				element = elementReader.readElement();
			}

			elementReader.close();
			elementReader = plyReader.nextElementReader();
		}

		return mesh;
	}

	public interface OnVertexAddedListener {
		void onVertexAdded(Element element);
	}
}
