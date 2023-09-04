package raytracer.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import raytracer.core.def.Accelerator;
import raytracer.core.def.StandardObj;
import raytracer.math.Point;
import raytracer.math.Vec3;
import raytracer.geom.GeomFactory;

/**
 * Represents a model file reader for the OBJ format
 */
public class OBJReader {

	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param filename
	 *            The file to read the data from
	 * @param accelerator
	 *            The target acceleration structure
	 * @param shader
	 *            The shader which is used by all triangles
	 * @param scale
	 *            The scale factor which is responsible for scaling the model
	 * @param translate
	 *            A vector representing the translation coordinate with which
	 *            all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *             If the filename is null or the empty string, the accelerator
	 *             is null, the shader is null, the translate vector is null,
	 *             the translate vector is not finite or scale does not
	 *             represent a legal (finite) floating point number
	 */
	public static void read(final String filename,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
				if (filename == null || filename == "" || shader == null || translate == null || translate.dot(translate) > Float.POSITIVE_INFINITY) throw new IllegalArgumentException();
		read(new BufferedInputStream(new FileInputStream(filename)), accelerator, shader, scale, translate);
	}


	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param in
	 *            The InputStream of the data to be read.
	 * @param accelerator
	 *            The target acceleration structure
	 * @param shader
	 *            The shader which is used by all triangles
	 * @param scale
	 *            The scale factor which is responsible for scaling the model
	 * @param translate
	 *            A vector representing the translation coordinate with which
	 *            all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *             If the InputStream is null, the accelerator
	 *             is null, the shader is null, the translate vector is null,
	 *             the translate vector is not finite or scale does not
	 *             represent a legal (finite) floating point number
	 */
	public static void read(final InputStream in,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
		// TODO Implement this method
		Scanner sc = new Scanner(in);
		sc.useLocale(Locale.ENGLISH);
		String line;
		ArrayList<Point> vertices = new ArrayList<>();
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			if (line.isEmpty() || line.startsWith("#")) continue;
			
			String[] parts = line.split(" ");
			if (parts.length > 0) {
                    String identifier = parts[0];

                    if (identifier.equals("v")) {
                        // Parse vertex data
                        float x = Float.parseFloat(parts[1]) * scale + translate.x();
                        float y = Float.parseFloat(parts[2]) * scale + translate.y();
                        float z = Float.parseFloat(parts[3]) * scale + translate.z();

						vertices.add(new Point(x, y, z));
                    } else if (identifier.equals("f")) {
                        // Parse face data
						Point[] vertex = new Point[3];
						for (int i = 1; i < 4; i++) {
							vertex[i - 1] = vertices.get(Integer.parseInt(parts[i]) - 1 );
						}
						accelerator.add(new StandardObj(GeomFactory.createTriangle(vertex[0], vertex[1], vertex[2]), shader));
                    }
                }
		}
		// throw new UnsupportedOperationException("This method has not yet been implemented.");
	}
}
