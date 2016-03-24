/*
 * Created on Jan 1, 2005
 */
package tools3d.utils.leafnode;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.J3DBuffer;
import javax.media.j3d.TriangleArray;
import javax.media.j3d.Shape3D;

import tools3d.utils.SimpleShaderAppearance;
import tools3d.utils.Utils3D;

/**
 * @author Administrator
 *
 */
public class Cube extends Shape3D
{

	private static final float[] verts = {
			// front face
			1.0f, -1.0f, 1.0f, //1
			1.0f, 1.0f, 1.0f, //2
			-1.0f, 1.0f, 1.0f, //3
			1.0f, -1.0f, 1.0f, //1
			-1.0f, 1.0f, 1.0f, //3
			-1.0f, -1.0f, 1.0f, //4
			// back face
			-1.0f, -1.0f, -1.0f, //1
			-1.0f, 1.0f, -1.0f, //2
			1.0f, 1.0f, -1.0f, //3
			-1.0f, -1.0f, -1.0f, //1
			1.0f, 1.0f, -1.0f, //3
			1.0f, -1.0f, -1.0f, //4
			// right face
			1.0f, -1.0f, -1.0f, //1
			1.0f, 1.0f, -1.0f, //2
			1.0f, 1.0f, 1.0f, //3
			1.0f, -1.0f, -1.0f, //1
			1.0f, 1.0f, 1.0f, //3
			1.0f, -1.0f, 1.0f, //4
			// left face
			-1.0f, -1.0f, 1.0f, //1
			-1.0f, 1.0f, 1.0f, //2
			-1.0f, 1.0f, -1.0f, //3
			-1.0f, -1.0f, 1.0f, //1
			-1.0f, 1.0f, -1.0f, //3
			-1.0f, -1.0f, -1.0f, //4
			// top face
			1.0f, 1.0f, 1.0f, //1
			1.0f, 1.0f, -1.0f, //2
			-1.0f, 1.0f, -1.0f, //3
			1.0f, 1.0f, 1.0f, //1
			-1.0f, 1.0f, -1.0f, //3
			-1.0f, 1.0f, 1.0f, //4			
			// bottom face
			-1.0f, -1.0f, 1.0f, //1
			-1.0f, -1.0f, -1.0f, //2
			1.0f, -1.0f, -1.0f, //3
			-1.0f, -1.0f, 1.0f, //1
			1.0f, -1.0f, -1.0f, //3
			1.0f, -1.0f, 1.0f, };//4

	private static final float[] colors = {
			// front face (red)
			1.0f, 0.0f, 0.0f, //1
			1.0f, 0.0f, 0.0f, //2
			1.0f, 0.0f, 0.0f, //3
			1.0f, 0.0f, 0.0f, //1
			1.0f, 0.0f, 0.0f, //3
			1.0f, 0.0f, 0.0f, //4
			// back face (green)
			0.0f, 1.0f, 0.0f, //1
			0.0f, 1.0f, 0.0f, //2
			0.0f, 1.0f, 0.0f, //3
			0.0f, 1.0f, 0.0f, //1
			0.0f, 1.0f, 0.0f, //3
			0.0f, 1.0f, 0.0f, //4			
			// right face (blue)
			0.0f, 0.0f, 1.0f, //1
			0.0f, 0.0f, 1.0f, //2
			0.0f, 0.0f, 1.0f, //3
			0.0f, 0.0f, 1.0f, //1
			0.0f, 0.0f, 1.0f, //3
			0.0f, 0.0f, 1.0f, //4
			// left face (yellow)
			1.0f, 1.0f, 0.0f, //1
			1.0f, 1.0f, 0.0f, //2
			1.0f, 1.0f, 0.0f, //3
			1.0f, 1.0f, 0.0f, //1
			1.0f, 1.0f, 0.0f, //3
			1.0f, 1.0f, 0.0f, //4
			// top face (magenta)
			1.0f, 0.0f, 1.0f, //1
			1.0f, 0.0f, 1.0f, //2
			1.0f, 0.0f, 1.0f, //3
			1.0f, 0.0f, 1.0f, //1
			1.0f, 0.0f, 1.0f, //3
			1.0f, 0.0f, 1.0f, //4
			// bottom face (cyan)
			0.0f, 1.0f, 1.0f, //1
			0.0f, 1.0f, 1.0f, //2
			0.0f, 1.0f, 1.0f, //3
			0.0f, 1.0f, 1.0f, //1
			0.0f, 1.0f, 1.0f, //3
			0.0f, 1.0f, 1.0f, };//4

	/**
	 * Constructs a color cube with unit scale.  The corners of the
	 * color cube are [-1,-1,-1] and [1,1,1].
	 */
	public Cube()
	{
		clearCapabilities();
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(verts)));
		cube.setColorRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colors)));

		this.setGeometry(cube);
		this.setAppearance(new SimpleShaderAppearance());
	}

	/**
	 * Constructs a color cube with the specified scale.  The corners of the
	 * color cube are [-scale,-scale,-scale] and [scale,scale,scale].
	 * @param scale the scale of the cube
	 */
	public Cube(double scale)
	{
		clearCapabilities();
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i++)
			scaledVerts[i] = verts[i] * (float) scale;

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(scaledVerts)));
		cube.setColorRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colors)));

		this.setGeometry(cube);

		this.setAppearance(new SimpleShaderAppearance());
	}

	public Cube(double scale, float r, float g, float b)
	{
		clearCapabilities();
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i++)
			scaledVerts[i] = verts[i] * (float) scale;

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(scaledVerts)));

		float colorsSet[] = new float[36 * 3];
		for (int i = 0; i < 36; i++)
		{
			colorsSet[i * 3 + 0] = r;
			colorsSet[i * 3 + 1] = g;
			colorsSet[i * 3 + 2] = b;
		}

		cube.setColorRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colorsSet)));

		this.setGeometry(cube);
		this.setAppearance(new SimpleShaderAppearance());
	}

	/**
		 * Constructs a color cube with the specified scale.  The corners of the
		 * color cube are [-scale,-scale,-scale] and [scale,scale,scale].
		 * @param scale the scale of the cube
		 */
	public Cube(double xScale, double yScale, double zScale)
	{
		clearCapabilities();
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i += 3)
		{
			scaledVerts[i + 0] = verts[i + 0] * (float) xScale;
			scaledVerts[i + 1] = verts[i + 1] * (float) yScale;
			scaledVerts[i + 2] = verts[i + 2] * (float) zScale;
		}

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(scaledVerts)));
		cube.setColorRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colors)));

		this.setGeometry(cube);
		this.setAppearance(new SimpleShaderAppearance());
	}

	public Cube(double xScale, double yScale, double zScale, float r, float g, float b)
	{
		clearCapabilities();
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i += 3)
		{
			scaledVerts[i + 0] = verts[i + 0] * (float) xScale;
			scaledVerts[i + 1] = verts[i + 1] * (float) yScale;
			scaledVerts[i + 2] = verts[i + 2] * (float) zScale;
		}

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(scaledVerts)));

		float colorsSet[] = new float[36 * 3];
		for (int i = 0; i < 36; i++)
		{
			colorsSet[i * 3 + 0] = r;
			colorsSet[i * 3 + 1] = g;
			colorsSet[i * 3 + 2] = b;
		}

		cube.setColorRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colorsSet)));

		this.setGeometry(cube);
		this.setAppearance(new SimpleShaderAppearance());
	}

	public Cube(float xMin, float yMin, float zMin, float xMax, float yMax, float zMax)
	{
		clearCapabilities();
		TriangleArray cube = new TriangleArray(36,
				GeometryArray.COORDINATES | GeometryArray.COLOR_3 | GeometryArray.USE_NIO_BUFFER | GeometryArray.BY_REFERENCE);

		float scaledVerts[] = new float[] {
				// front face
				xMax, yMin, zMax, //1
				xMax, yMax, zMax, //2
				xMin, yMax, zMax, //3
				xMax, yMin, zMax, //1
				xMin, yMax, zMax, //3
				xMin, yMin, zMax, //4
				// back face
				xMin, yMin, zMin, //1
				xMin, yMax, zMin, //2
				xMax, yMax, zMin, //3				
				xMin, yMin, zMin, //1
				xMax, yMax, zMin, //3
				xMax, yMin, zMin, //4
				// right face
				xMax, yMin, zMin, //1
				xMax, yMax, zMin, //2
				xMax, yMax, zMax, //3
				xMax, yMin, zMin, //1
				xMax, yMax, zMax, //3
				xMax, yMin, zMax, //4
				// left face
				xMin, yMin, zMax, //1
				xMin, yMax, zMax, //2
				xMin, yMax, zMin, //3
				xMin, yMin, zMax, //1
				xMin, yMax, zMin, //3
				xMin, yMin, zMin, //4				
				// top face
				xMax, yMax, zMax, //1
				xMax, yMax, zMin, //2
				xMin, yMax, zMin, //3
				xMax, yMax, zMax, //1
				xMin, yMax, zMin, //3
				xMin, yMax, zMax, //4
				// bottom face
				xMin, yMin, zMax, //1
				xMin, yMin, zMin, //2
				xMax, yMin, zMin, //3
				xMin, yMin, zMax, //1
				xMax, yMin, zMin, //3
				xMax, yMin, zMax, };//4

		cube.setCoordRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(scaledVerts)));
		cube.setColorRefBuffer(new J3DBuffer(Utils3D.makeFloatBuffer(colors)));

		this.setGeometry(cube);
		this.setAppearance(new SimpleShaderAppearance());
	}

}
