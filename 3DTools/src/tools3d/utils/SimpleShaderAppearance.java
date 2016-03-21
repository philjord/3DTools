package tools3d.utils;

import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GLSLShaderProgram;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shader;
import javax.media.j3d.ShaderAppearance;
import javax.media.j3d.ShaderAttributeSet;
import javax.media.j3d.ShaderAttributeValue;
import javax.media.j3d.SourceCodeShader;
import javax.vecmath.Color3f;

/**
 * Note all of these are no lighting shaders, so materials are always ignored
 * @author phil
 *
 */
public class SimpleShaderAppearance extends ShaderAppearance
{
	private static GLSLShaderProgram flatShaderProgram;
	private static GLSLShaderProgram textureShaderProgram;
	private static GLSLShaderProgram colorLineShaderProgram;

	public static String alphaTestUniforms = "uniform int alphaTestEnabled;\n" + //
			"uniform int alphaTestFunction;\n" + //
			"uniform float alphaTestValue;\n";

	public static String alphaTestMethod = "if(alphaTestEnabled != 0)\n" + //
			"{	\n" + //
			" 	if(alphaTestFunction==516)//>\n" + //
			"		if(baseMap.a<=alphaTestValue)discard;\n" + //
			"	else if(alphaTestFunction==518)//>=\n" + //
			"		if(baseMap.a<alphaTestValue)discard;\n" + //
			"	else if(alphaTestFunction==514)//==\n" + //
			"		if(baseMap.a!=alphaTestValue)discard;\n" + //
			"	else if(alphaTestFunction==517)//!=\n" + //
			"		if(baseMap.a==alphaTestValue)discard;\n" + //
			"	else if(alphaTestFunction==513)//<\n" + //
			"		if(baseMap.a>=alphaTestValue)discard;\n" + //
			"	else if(alphaTestFunction==515)//<=\n" + //
			"		if(baseMap.a>alphaTestValue)discard;\n" + //
			"	else if(alphaTestFunction==512)//never	\n" + //
			"		discard;	\n" + //
			"}\n";

	/**
	 * Polygons no texture, no single color, must have color vertex attribute
	 */
	public SimpleShaderAppearance()
	{
		this(null, false);
	}

	/**
	 * Lines with a single color no texture, ignores vertex attribute of color
	 * @param color
	 */
	public SimpleShaderAppearance(Color3f color)
	{
		this(color, false);
	}

	/**
	 * Polygons  if hasTexture is true a texture otherwise vertex attribute colors for face color
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
		if (hasTexture)
		{
			RenderingAttributes ra = new RenderingAttributes();
			setRenderingAttributes(ra);

			if (textureShaderProgram == null)
			{
				textureShaderProgram = new GLSLShaderProgram() {
					public String toString()
					{
						return "SimpleShaderAppearance textureShaderProgram";
					}
				};
				String vertexProgram = ShaderSourceIO.DESKTOP_SHADERS ? "#version 100\n" : "#version 120\n";
				vertexProgram += "attribute vec4 glVertex;\n";
				vertexProgram += "attribute vec2 glMultiTexCoord0;\n";
				vertexProgram += "uniform mat4 glModelViewProjectionMatrix;\n";
				vertexProgram += "varying vec2 glTexCoord0;\n";
				vertexProgram += "void main( void ){\n";
				vertexProgram += "gl_Position = glModelViewProjectionMatrix * glVertex;\n";
				vertexProgram += "glTexCoord0 = glMultiTexCoord0.st;\n";
				vertexProgram += "}";

				String fragmentProgram = ShaderSourceIO.DESKTOP_SHADERS ? "#version 100\n" : "#version 120\n";
				fragmentProgram += "precision mediump float;\n";
				fragmentProgram += alphaTestUniforms;
				fragmentProgram += "varying vec2 glTexCoord0;\n";
				fragmentProgram += "uniform sampler2D BaseMap;\n";
				fragmentProgram += "void main( void ){\n ";
				fragmentProgram += "vec4 baseMap = texture2D( BaseMap, glTexCoord0.st );\n";
				fragmentProgram += alphaTestMethod;
				fragmentProgram += "gl_FragColor = baseMap;\n";
				fragmentProgram += "}";

				textureShaderProgram.setShaders(makeShaders(vertexProgram, fragmentProgram));
				textureShaderProgram.setShaderAttrNames(new String[] { "BaseMap" });
			}

			setShaderProgram(textureShaderProgram);

			ShaderAttributeSet shaderAttributeSet = new ShaderAttributeSet();
			shaderAttributeSet.put(new ShaderAttributeValue("BaseMap", new Integer(0)));
			setShaderAttributeSet(shaderAttributeSet);

		}
		else
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

				RenderingAttributes ra = new RenderingAttributes();
				ra.setIgnoreVertexColors(true);
				setRenderingAttributes(ra);

				Material mat = new Material();
				setMaterial(mat);

				if (colorLineShaderProgram == null)
				{
					colorLineShaderProgram = new GLSLShaderProgram() {
						public String toString()
						{
							return "SimpleShaderAppearance colorLineShaderProgram";
						}
					};
					String vertexProgram = ShaderSourceIO.DESKTOP_SHADERS ? "#version 100\n" : "#version 120\n";
					vertexProgram += "attribute vec4 glVertex;\n";
					vertexProgram += "attribute vec4 glColor;\n";
					vertexProgram += "uniform int ignoreVertexColors;\n";
					vertexProgram += "uniform vec4 objectColor;\n";
					vertexProgram += "uniform mat4 glModelViewProjectionMatrix;\n";
					vertexProgram += "varying vec4 glFrontColor;\n";
					vertexProgram += "void main( void ){\n";
					vertexProgram += "gl_Position = glModelViewProjectionMatrix * glVertex;\n";
					vertexProgram += "if( ignoreVertexColors != 0 )\n";
					vertexProgram += "	glFrontColor = objectColor;\n";
					vertexProgram += "else\n";
					vertexProgram += "	glFrontColor = glColor;\n";
					vertexProgram += "}";

					String fragmentProgram = ShaderSourceIO.DESKTOP_SHADERS ? "#version 100\n" : "#version 120\n";
					fragmentProgram += "precision mediump float;\n";
					fragmentProgram += "varying vec4 glFrontColor;\n";
					fragmentProgram += "void main( void ){\n";
					fragmentProgram += "gl_FragColor = glFrontColor;\n";
					fragmentProgram += "}";

					colorLineShaderProgram.setShaders(makeShaders(vertexProgram, fragmentProgram));
				}

				setShaderProgram(colorLineShaderProgram);

			}
			else
			{
				RenderingAttributes ra = new RenderingAttributes();
				setRenderingAttributes(ra);

				if (flatShaderProgram == null)
				{
					flatShaderProgram = new GLSLShaderProgram() {
						public String toString()
						{
							return "SimpleShaderAppearance flatShaderProgram";
						}
					};
					String vertexProgram = ShaderSourceIO.DESKTOP_SHADERS ? "#version 100\n" : "#version 120\n";
					vertexProgram += "attribute vec4 glVertex;\n";
					vertexProgram += "attribute vec4 glColor;\n";
					vertexProgram += "uniform int ignoreVertexColors;\n";
					vertexProgram += "uniform vec4 objectColor;\n";
					vertexProgram += "uniform mat4 glModelViewProjectionMatrix;\n";
					vertexProgram += "varying vec4 glFrontColor;\n";
					vertexProgram += "void main( void ){\n";
					vertexProgram += "gl_Position = glModelViewProjectionMatrix * glVertex;\n";
					vertexProgram += "if( ignoreVertexColors != 0 )\n";
					vertexProgram += "	glFrontColor = objectColor;\n";
					vertexProgram += "else\n";
					vertexProgram += "	glFrontColor = glColor;\n";
					vertexProgram += "}";

					String fragmentProgram = ShaderSourceIO.DESKTOP_SHADERS ? "#version 100\n" : "#version 120\n";
					fragmentProgram += "precision mediump float;\n";
					fragmentProgram += "varying vec4 glFrontColor;\n";
					fragmentProgram += "void main( void ){\n";
					fragmentProgram += "gl_FragColor = glFrontColor;\n";
					fragmentProgram += "}";

					flatShaderProgram.setShaders(makeShaders(vertexProgram, fragmentProgram));

				}

				setShaderProgram(flatShaderProgram);

			}
		}

	}

	private static Shader[] makeShaders(String vertexProgram, String fragmentProgram)
	{
		Shader[] shaders = new Shader[2];
		shaders[0] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_VERTEX, vertexProgram) {
			public String toString()
			{
				return "vertexProgram";
			}
		};
		shaders[1] = new SourceCodeShader(Shader.SHADING_LANGUAGE_GLSL, Shader.SHADER_TYPE_FRAGMENT, fragmentProgram) {
			public String toString()
			{
				return "fragmentProgram";
			}
		};
		return shaders;
	}
}
