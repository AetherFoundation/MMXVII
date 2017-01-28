package moe.thisis.aether.bokuseru.engine.graph;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import moe.thisis.aether.bokuseru.engine.graph.lights.DirectionalLight;
import moe.thisis.aether.bokuseru.engine.graph.lights.PointLight;
import moe.thisis.aether.bokuseru.engine.graph.lights.SpotLight;
import moe.thisis.aether.bokuseru.engine.graph.weather.Fog;

public class ShaderProgram {

	private final int programId;

	private int vertexShaderId;

	private int fragmentShaderId;

	private int geometryShaderId;

	private final Map<String, UniformData> uniforms;

	/** Shader Program
	 * @throws Exception
	 */
	public ShaderProgram() throws Exception {
		programId = GL20.glCreateProgram();
		if (programId == 0) {
			throw new Exception("Could not create Shader");
		}
		uniforms = new HashMap<>();
	}

	/** Bind shader program
	 * 
	 */
	public void bind() {
		GL20.glUseProgram(programId);
	}

	/** Clean up shader
	 * 
	 */
	public void cleanup() {
		unbind();
		if (programId != 0) {
			GL20.glDeleteProgram(programId);
		}
	}

	/** Create uniform directional light
	 * @param uniformName	Uniform name
	 * @throws Exception
	 */
	public void createDirectionalLightUniform(final String uniformName) throws Exception {
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".direction");
		createUniform(uniformName + ".intensity");
	}

	/** Create uniform fog
	 * @param uniformName	Uniform name
	 * @throws Exception
	 */
	public void createFogUniform(final String uniformName) throws Exception {
		createUniform(uniformName + ".activeFog");
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".density");
	}

	/** Create fragment shader
	 * @param shaderCode	Shader code
	 * @throws Exception
	 */
	public void createFragmentShader(final String shaderCode) throws Exception {
		fragmentShaderId = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
	}

	/** Create uniform material
	 * @param uniformName	Uniform name
	 * @throws Exception
	 */
	public void createMaterialUniform(final String uniformName) throws Exception {
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".hasTexture");
		createUniform(uniformName + ".hasNormalMap");
		createUniform(uniformName + ".reflectance");
	}

	/** Create uniform point light list
	 * @param uniformName	Uniform name
	 * @param size	List size
	 * @throws Exception
	 */
	public void createPointLightListUniform(final String uniformName, final int size) throws Exception {
		for (int i = 0; i < size; i++) {
			createPointLightUniform(uniformName + "[" + i + "]");
		}
	}

	/** Create uniform point light
	 * @param uniformName	Uniform name
	 * @throws Exception
	 */
	public void createPointLightUniform(final String uniformName) throws Exception {
		createUniform(uniformName + ".colour");
		createUniform(uniformName + ".position");
		createUniform(uniformName + ".intensity");
		createUniform(uniformName + ".att.constant");
		createUniform(uniformName + ".att.linear");
		createUniform(uniformName + ".att.exponent");
	}

	/** Create shader
	 * @param shaderCode	Shader code
	 * @param shaderType	Shader type
	 * @return	Shader
	 * @throws Exception
	 */
	protected int createShader(final String shaderCode, final int shaderType) throws Exception {
		final int shaderId = GL20.glCreateShader(shaderType);
		if (shaderId == 0) {
			throw new Exception("Error creating shader. Code: " + shaderId);
		}

		GL20.glShaderSource(shaderId, shaderCode);
		GL20.glCompileShader(shaderId);

		if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
			throw new Exception("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
		}

		GL20.glAttachShader(programId, shaderId);

		return shaderId;
	}

	public void createSpotLightListUniform(final String uniformName, final int size) throws Exception {
		for (int i = 0; i < size; i++) {
			createSpotLightUniform(uniformName + "[" + i + "]");
		}
	}

	public void createSpotLightUniform(final String uniformName) throws Exception {
		createPointLightUniform(uniformName + ".pl");
		createUniform(uniformName + ".conedir");
		createUniform(uniformName + ".cutoff");
	}

	public void createUniform(final String uniformName) throws Exception {
		final int uniformLocation = GL20.glGetUniformLocation(programId, uniformName);
		if (uniformLocation < 0) {
			throw new Exception("Could not find uniform:" + uniformName);
		}
		uniforms.put(uniformName, new UniformData(uniformLocation));
	}

	public void createVertexShader(final String shaderCode) throws Exception {
		vertexShaderId = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
	}

	public void link() throws Exception {
		GL20.glLinkProgram(programId);
		if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0) {
			throw new Exception("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
		}

		if (vertexShaderId != 0) {
			GL20.glDetachShader(programId, vertexShaderId);
		}
		if (geometryShaderId != 0) {
			GL20.glDetachShader(programId, geometryShaderId);
		}
		if (fragmentShaderId != 0) {
			GL20.glDetachShader(programId, fragmentShaderId);
		}

		GL20.glValidateProgram(programId);
		if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0) {
			System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
		}

	}

	public void setUniform(final String uniformName, final DirectionalLight dirLight) {
		setUniform(uniformName + ".colour", dirLight.getColor());
		setUniform(uniformName + ".direction", dirLight.getDirection());
		setUniform(uniformName + ".intensity", dirLight.getIntensity());
	}

	public void setUniform(final String uniformName, final float value) {
		final UniformData uniformData = uniforms.get(uniformName);
		if (uniformData == null) {
			throw new RuntimeException("Uniform [" + uniformName + "] has nor been created");
		}
		GL20.glUniform1f(uniformData.getUniformLocation(), value);
	}

	public void setUniform(final String uniformName, final Fog fog) {
		setUniform(uniformName + ".activeFog", fog.isActive() ? 1 : 0);
		setUniform(uniformName + ".colour", fog.getColour());
		setUniform(uniformName + ".density", fog.getDensity());
	}

	public void setUniform(final String uniformName, final int value) {
		final UniformData uniformData = uniforms.get(uniformName);
		if (uniformData == null) {
			throw new RuntimeException("Uniform [" + uniformName + "] has nor been created");
		}
		GL20.glUniform1i(uniformData.getUniformLocation(), value);
	}

	public void setUniform(final String uniformName, final Material material) {
		setUniform(uniformName + ".colour", material.getColour());
		setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
		setUniform(uniformName + ".hasNormalMap", material.hasNormalMap() ? 1 : 0);
		setUniform(uniformName + ".reflectance", material.getReflectance());
	}

	public void setUniform(final String uniformName, final Matrix4f value) {
		final UniformData uniformData = uniforms.get(uniformName);
		if (uniformData == null) {
			throw new RuntimeException("Uniform [" + uniformName + "] has nor been created");
		}
		// Check if float buffer has been created
		FloatBuffer fb = uniformData.getFloatBuffer();
		if (fb == null) {
			fb = BufferUtils.createFloatBuffer(16);
			uniformData.setFloatBuffer(fb);
		}
		// Dump the matrix into a float buffer
		fb = value.get(fb);
		GL20.glUniformMatrix4fv(uniformData.getUniformLocation(), false, fb);
	}

	public void setUniform(final String uniformName, final Matrix4f[] matrices) {
		final int length = matrices != null ? matrices.length : 0;
		final UniformData uniformData = uniforms.get(uniformName);
		if (uniformData == null) {
			throw new RuntimeException("Uniform [" + uniformName + "] has nor been created");
		}
		// Check if float buffer has been created
		FloatBuffer fb = uniformData.getFloatBuffer();
		if (fb == null) {
			fb = BufferUtils.createFloatBuffer(16 * length);
			uniformData.setFloatBuffer(fb);
		}
		for (int i = 0; i < length; i++) {
			matrices[i].get(16 * i, fb);
		}
		GL20.glUniformMatrix4fv(uniformData.getUniformLocation(), false, fb);
	}

	public void setUniform(final String uniformName, final PointLight pointLight) {
		setUniform(uniformName + ".colour", pointLight.getColor());
		setUniform(uniformName + ".position", pointLight.getPosition());
		setUniform(uniformName + ".intensity", pointLight.getIntensity());
		final PointLight.Attenuation att = pointLight.getAttenuation();
		setUniform(uniformName + ".att.constant", att.getConstant());
		setUniform(uniformName + ".att.linear", att.getLinear());
		setUniform(uniformName + ".att.exponent", att.getExponent());
	}

	public void setUniform(final String uniformName, final PointLight pointLight, final int pos) {
		setUniform(uniformName + "[" + pos + "]", pointLight);
	}

	public void setUniform(final String uniformName, final PointLight[] pointLights) {
		final int numLights = pointLights != null ? pointLights.length : 0;
		for (int i = 0; i < numLights; i++) {
			setUniform(uniformName, pointLights[i], i);
		}
	}

	public void setUniform(final String uniformName, final SpotLight spotLight) {
		setUniform(uniformName + ".pl", spotLight.getPointLight());
		setUniform(uniformName + ".conedir", spotLight.getConeDirection());
		setUniform(uniformName + ".cutoff", spotLight.getCutOff());
	}

	public void setUniform(final String uniformName, final SpotLight spotLight, final int pos) {
		setUniform(uniformName + "[" + pos + "]", spotLight);
	}

	public void setUniform(final String uniformName, final SpotLight[] spotLights) {
		final int numLights = spotLights != null ? spotLights.length : 0;
		for (int i = 0; i < numLights; i++) {
			setUniform(uniformName, spotLights[i], i);
		}
	}

	public void setUniform(final String uniformName, final Vector3f value) {
		final UniformData uniformData = uniforms.get(uniformName);
		if (uniformData == null) {
			throw new RuntimeException("Uniform [" + uniformName + "] has nor been created");
		}
		GL20.glUniform3f(uniformData.getUniformLocation(), value.x, value.y, value.z);
	}

	public void unbind() {
		GL20.glUseProgram(0);
	}
}
