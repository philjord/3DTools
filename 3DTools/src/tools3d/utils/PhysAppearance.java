package tools3d.utils;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GLSLShaderProgram;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shader;
import javax.media.j3d.ShaderAppearance;
import javax.media.j3d.SourceCodeShader;
import javax.vecmath.Color3f;

public class PhysAppearance extends Appearance
{
	private static final boolean USE_SHADERS = true;

	// not to be used anymore
	private PhysAppearance()
	{
	}

	public static Appearance makeAppearance()
	{
		return makeAppearance(new Color3f(1, 0, 0));
	}

	public static Appearance makeAppearance(Color3f color)
	{
		if (USE_SHADERS)
			return makeShaderAppearance(color);
		else
			return makeFFPAppearance(color);
	}

	private static Appearance makeFFPAppearance(Color3f color)
	{
		Appearance app = new Appearance();
		PolygonAttributes polyAtt = new PolygonAttributes(PolygonAttributes.POLYGON_LINE, PolygonAttributes.CULL_NONE, 0.0f);
		polyAtt.setPolygonOffset(0.1f);
		app.setPolygonAttributes(polyAtt);
		LineAttributes lineAtt = new LineAttributes(1, LineAttributes.PATTERN_SOLID, false);
		app.setLineAttributes(lineAtt);
		app.setTexture(null);
		app.setMaterial(null);
		// TODO: see bhkEntity for list of colors
		ColoringAttributes colorAtt = new ColoringAttributes(color, ColoringAttributes.FASTEST);
		app.setColoringAttributes(colorAtt);

		return app;
	}

	private static Appearance makeShaderAppearance(Color3f color)
	{
		ShaderAppearance app = new ShaderAppearance();
		PolygonAttributes polyAtt = new PolygonAttributes(PolygonAttributes.POLYGON_LINE, PolygonAttributes.CULL_NONE, 0.0f);
		polyAtt.setPolygonOffset(0.1f);
		app.setPolygonAttributes(polyAtt);
		LineAttributes lineAtt = new LineAttributes(1, LineAttributes.PATTERN_SOLID, false);
		app.setLineAttributes(lineAtt);
		app.setTexture(null);
		app.setMaterial(null);
		// TODO: see bhkEntity for list of colors
		ColoringAttributes colorAtt = new ColoringAttributes(color, ColoringAttributes.FASTEST);
		app.setColoringAttributes(colorAtt);

		String vertexProgram = "void main( void ){gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;gl_FrontColor = gl_Color;}";
		String fragmentProgram = "void main( void ){gl_FragColor = gl_Color;}";

		Shader[] shaders = new Shader[2];
		shaders[0] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_VERTEX, vertexProgram);
		shaders[1] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_FRAGMENT, fragmentProgram);

		GLSLShaderProgram shaderProgram = new GLSLShaderProgram();
		shaderProgram.setShaders(shaders);
		app.setShaderProgram(shaderProgram);
		return app;
	}
}
