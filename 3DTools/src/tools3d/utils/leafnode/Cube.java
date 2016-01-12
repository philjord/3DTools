/*
 * Created on Jan 1, 2005
 */
package tools3d.utils.leafnode;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.J3DBuffer;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;

import tools3d.utils.Utils3D;

/**
 * @author Administrator
 *
 */
public class Cube extends Shape3D
{

	private static final float[] verts = {
			// front face
			1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
			// back face
			-1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
			// right face
			1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
			// left face
			-1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
			// top face
			1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
			// bottom face
			-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, };

	private static final float[] colors = {
			// front face (red)
			1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
			// back face (green)
			0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			// right face (blue)
			0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
			// left face (yellow)
			1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
			// top face (magenta)
			1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
			// bottom face (cyan)
			0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, };

	/**
	 * Constructs a color cube with unit scale.  The corners of the
	 * color cube are [-1,-1,-1] and [1,1,1].
	 */
	public Cube()
	{
		QuadArray cube = new QuadArray(24,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(verts)));
		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colors)));

		this.setGeometry(cube);
	}

	/**
	 * Constructs a color cube with the specified scale.  The corners of the
	 * color cube are [-scale,-scale,-scale] and [scale,scale,scale].
	 * @param scale the scale of the cube
	 */
	public Cube(double scale)
	{
		QuadArray cube = new QuadArray(24,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i++)
			scaledVerts[i] = verts[i] * (float) scale;

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(scaledVerts)));
		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colors)));

		this.setGeometry(cube);
	}

	public Cube(double scale, float r, float g, float b)
	{
		QuadArray cube = new QuadArray(24,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i++)
			scaledVerts[i] = verts[i] * (float) scale;

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(scaledVerts)));

		float colorsSet[] = new float[24 * 3];
		for (int i = 0; i < 24; i++)
		{
			colorsSet[i * 3 + 0] = r;
			colorsSet[i * 3 + 1] = g;
			colorsSet[i * 3 + 2] = b;
		}

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colorsSet)));

		this.setGeometry(cube);
	}

	/**
		 * Constructs a color cube with the specified scale.  The corners of the
		 * color cube are [-scale,-scale,-scale] and [scale,scale,scale].
		 * @param scale the scale of the cube
		 */
	public Cube(double xScale, double yScale, double zScale)
	{
		QuadArray cube = new QuadArray(24,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i += 3)
		{
			scaledVerts[i + 0] = verts[i + 0] * (float) xScale;
			scaledVerts[i + 1] = verts[i + 1] * (float) yScale;
			scaledVerts[i + 2] = verts[i + 2] * (float) zScale;
		}

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(scaledVerts)));
		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colors)));

		this.setGeometry(cube);
	}

	public Cube(double xScale, double yScale, double zScale, float r, float g, float b)
	{
		QuadArray cube = new QuadArray(24,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i += 3)
		{
			scaledVerts[i + 0] = verts[i + 0] * (float) xScale;
			scaledVerts[i + 1] = verts[i + 1] * (float) yScale;
			scaledVerts[i + 2] = verts[i + 2] * (float) zScale;
		}

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(scaledVerts)));

		float colorsSet[] = new float[24 * 3];
		for (int i = 0; i < 24; i++)
		{
			colorsSet[i * 3 + 0] = r;
			colorsSet[i * 3 + 1] = g;
			colorsSet[i * 3 + 2] = b;
		}

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colorsSet)));

		this.setGeometry(cube);
	}

	public Cube(float xMin, float yMin, float zMin, float xMax, float yMax, float zMax)
	{
		QuadArray cube = new QuadArray(24,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[] {
				// front face
				xMax, yMin, zMax, xMax, yMax, zMax, xMin, yMax, zMax, xMin, yMin, zMax,
				// back face
				xMin, yMin, zMin, xMin, yMax, zMin, xMax, yMax, zMin, xMax, yMin, zMin,
				// right face
				xMax, yMin, zMin, xMax, yMax, zMin, xMax, yMax, zMax, xMax, yMin, zMax,
				// left face
				xMin, yMin, zMax, xMin, yMax, zMax, xMin, yMax, zMin, xMin, yMin, zMin,
				// top face
				xMax, yMax, zMax, xMax, yMax, zMin, xMin, yMax, zMin, xMin, yMax, zMax,
				// bottom face
				xMin, yMin, zMax, xMin, yMin, zMin, xMax, yMin, zMin, xMax, yMin, zMax, };

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(scaledVerts)));
		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colors)));

		this.setGeometry(cube);
	}
}
