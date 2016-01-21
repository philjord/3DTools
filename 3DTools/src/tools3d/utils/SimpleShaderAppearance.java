package tools3d.utils;

import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GLSLShaderProgram;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shader;
import javax.media.j3d.ShaderAppearance;
import javax.media.j3d.ShaderAttributeSet;
import javax.media.j3d.ShaderAttributeValue;
import javax.media.j3d.SourceCodeShader;
import javax.vecmath.Color3f;

public class SimpleShaderAppearance extends ShaderAppearance
{
	private static GLSLShaderProgram shaderProgram;

	/**
	 * Polygons no texture
	 */
	public SimpleShaderAppearance()
	{
		this(null, false);
	}

	/**
	 * Lines with color no texture
	 * @param color
	 */
	public SimpleShaderAppearance(Color3f color)
	{
		this(color, false);
	}

	/**
	 * Polygons texture if hasTexture
	 */
	public SimpleShaderAppearance(boolean hasTexture)
	{
		this(null, hasTexture);
	}

	/** if color is not null then a line appearance
	 * otherwise simple poly appearance
	 * @param color
	 */
	private SimpleShaderAppearance(Color3f color, boolean hasTexture)
	{
		if (color != null)
		{
			PolygonAttributes polyAtt = new PolygonAttributes(PolygonAttributes.POLYGON_LINE, PolygonAttributes.CULL_NONE, 0.0f);
			polyAtt.setPolygonOffset(0.1f);
			setPolygonAttributes(polyAtt);
			LineAttributes lineAtt = new LineAttributes(1, LineAttributes.PATTERN_SOLID, false);
			setLineAttributes(lineAtt);

			ColoringAttributes colorAtt = new ColoringAttributes(color, ColoringAttributes.FASTEST);
			setColoringAttributes(colorAtt);
		}

		if (shaderProgram == null)
		{
			shaderProgram = new GLSLShaderProgram();
			String vertexProgram = "uniform mat4 glProjectionMatrix;uniform mat4 glModelViewMatrix;";
			if (hasTexture)
				vertexProgram += "varying vec2 glTexCoord0;";
			vertexProgram += "void main( void ){gl_Position = glProjectionMatrix * glModelViewMatrix * gl_Vertex;";
			if (hasTexture)
				vertexProgram += "glTexCoord0 = gl_MultiTexCoord0.st;";
			vertexProgram += "gl_FrontColor = gl_Color;}";

			String fragmentProgram = "";
			if (hasTexture)
			{
				fragmentProgram += "varying vec2 glTexCoord0;uniform sampler2D BaseMap;";
				fragmentProgram += "void main( void ){gl_FragColor = texture2D( BaseMap, glTexCoord0.st );}";
			}
			else
			{
				fragmentProgram += "void main( void ){gl_FragColor = gl_Color;}";
			}

			Shader[] shaders = new Shader[2];
			shaders[0] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_VERTEX, vertexProgram);
			shaders[1] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_FRAGMENT, fragmentProgram);

			shaderProgram.setShaders(shaders);
			
			if(hasTexture)
				shaderProgram.setShaderAttrNames(new String[]{"BaseMap"});
		}

		setShaderProgram(shaderProgram);
		if (hasTexture)
		{
			ShaderAttributeSet shaderAttributeSet = new ShaderAttributeSet();
			shaderAttributeSet.put(new ShaderAttributeValue("BaseMap", new Integer(0)));
			setShaderAttributeSet(shaderAttributeSet);
		}

	}
}
