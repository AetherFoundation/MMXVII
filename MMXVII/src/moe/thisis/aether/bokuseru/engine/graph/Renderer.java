package moe.thisis.aether.bokuseru.engine.graph;

import java.util.List;
import java.util.Map;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import moe.thisis.aether.bokuseru.engine.Scene;
import moe.thisis.aether.bokuseru.engine.SceneLight;
import moe.thisis.aether.bokuseru.engine.Utils;
import moe.thisis.aether.bokuseru.engine.Window;
import moe.thisis.aether.bokuseru.engine.graph.lights.DirectionalLight;
import moe.thisis.aether.bokuseru.engine.graph.lights.PointLight;
import moe.thisis.aether.bokuseru.engine.graph.lights.SpotLight;
import moe.thisis.aether.bokuseru.engine.items.GameItem;

public class Renderer {

	private static final int MAX_POINT_LIGHTS = 5;

	private static final int MAX_SPOT_LIGHTS = 5;

	private final Transformation transformation;

	private ShadowMap shadowMap;

	private ShaderProgram depthShaderProgram;

	private ShaderProgram sceneShaderProgram;

	private final float specularPower;

	/** Renderer
	 * 
	 */
	public Renderer() {
		transformation = new Transformation();
		specularPower = 10f;
	}

	/** Clean up renderer
	 * 
	 */
	public void cleanup() {
		if (shadowMap != null) {
			shadowMap.cleanup();
		}
		if (depthShaderProgram != null) {
			depthShaderProgram.cleanup();
		}
		if (sceneShaderProgram != null) {
			sceneShaderProgram.cleanup();
		}
	}

	/** Clear render
	 * 
	 */
	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
	}

	/** Initialize renderer
	 * @param window	Window to render to
	 * @throws Exception
	 */
	public void init(final Window window) throws Exception {
		shadowMap = new ShadowMap();

		setupDepthShader();
		// setupSkyBoxShader();
		setupSceneShader();
		// setupParticlesShader();
	}

	/** Render scene
	 * @param window	Window to render to
	 * @param camera	Camera to render from
	 * @param scene	Scene to render
	 */
	public void render(final Window window, final Camera camera, final Scene scene) {
		clear();

		// Render depth map before view ports has been set up
		renderDepthMap(window, camera, scene);

		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

		// Update projection matrix once per render cycle
		window.updateProjectionMatrix();

		renderScene(window, camera, scene);

		// renderAxes(camera);
		renderCrossHair(window);
	}

	/** Render crosshair
	 * @param window	Window to render to
	 */
	private void renderCrossHair(final Window window) {
		if (window.getWindowOptions().compatibleProfile) {
			GL11.glPushMatrix();
			GL11.glLoadIdentity();

			final float inc = 0.05f;
			GL11.glLineWidth(2.0f);

			GL11.glBegin(GL11.GL_LINES);

			GL11.glColor3f(1.0f, 1.0f, 1.0f);

			// Horizontal line
			GL11.glVertex3f(-inc, 0.0f, 0.0f);
			GL11.glVertex3f(+inc, 0.0f, 0.0f);
			GL11.glEnd();

			// Vertical line
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(0.0f, -inc, 0.0f);
			GL11.glVertex3f(0.0f, +inc, 0.0f);
			GL11.glEnd();

			GL11.glPopMatrix();
		}
	}

	/** Render depth map
	 * @param window	Window to render to
	 * @param camera	Camera to render from
	 * @param scene	Scene to render
	 */
	private void renderDepthMap(final Window window, final Camera camera, final Scene scene) {
		if (scene.isRenderShadows()) {
			// Setup view port to match the texture size
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
			GL11.glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

			depthShaderProgram.bind();

			final DirectionalLight light = scene.getSceneLight().getDirectionalLight();
			final Vector3f lightDirection = light.getDirection();

			final float lightAngleX = (float) Math.toDegrees(Math.acos(lightDirection.z));
			final float lightAngleY = (float) Math.toDegrees(Math.asin(lightDirection.x));
			final float lightAngleZ = 0;
			final Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(
					new Vector3f(lightDirection).mul(light.getShadowPosMult()),
					new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
			final DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
			final Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left,
					orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

			depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);

			renderNonInstancedMeshes(scene, depthShaderProgram, null, lightViewMatrix);

			renderInstancedMeshes(scene, depthShaderProgram, null, lightViewMatrix);

			// Unbind
			depthShaderProgram.unbind();
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		}
	}

	/** Render instanced meshes
	 * @param scene	Scene to render
	 * @param shader	Shader to render with
	 * @param viewMatrix	View matrix
	 * @param lightViewMatrix	Light view matrix
	 */
	private void renderInstancedMeshes(final Scene scene, final ShaderProgram shader, final Matrix4f viewMatrix,
			final Matrix4f lightViewMatrix) {
		shader.setUniform("isInstanced", 1);

		// Render each mesh with the associated game Items
		final Map<InstancedMesh, List<GameItem>> mapMeshes = scene.getGameInstancedMeshes();
		for (final InstancedMesh mesh : mapMeshes.keySet()) {
			final Texture text = mesh.getMaterial().getTexture();
			if (text != null) {
				sceneShaderProgram.setUniform("numCols", text.getNumCols());
				sceneShaderProgram.setUniform("numRows", text.getNumRows());
			}

			if (viewMatrix != null) {
				shader.setUniform("material", mesh.getMaterial());
				GL13.glActiveTexture(GL13.GL_TEXTURE2);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
			}
			mesh.renderListInstanced(mapMeshes.get(mesh), transformation, viewMatrix, lightViewMatrix);
		}
	}

	/** Render lights
	 * @param viewMatrix	View matrix
	 * @param sceneLight	Scene light
	 */
	private void renderLights(final Matrix4f viewMatrix, final SceneLight sceneLight) {

		sceneShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
		sceneShaderProgram.setUniform("specularPower", specularPower);

		// Process Point Lights
		final PointLight[] pointLightList = sceneLight.getPointLightList();
		int numLights = pointLightList != null ? pointLightList.length : 0;
		for (int i = 0; i < numLights; i++) {
			// Get a copy of the point light object and transform its position
			// to view coordinates
			final PointLight currPointLight = new PointLight(pointLightList[i]);
			final Vector3f lightPos = currPointLight.getPosition();
			final Vector4f aux = new Vector4f(lightPos, 1);
			aux.mul(viewMatrix);
			lightPos.x = aux.x;
			lightPos.y = aux.y;
			lightPos.z = aux.z;
			sceneShaderProgram.setUniform("pointLights", currPointLight, i);
		}

		// Process Spot Ligths
		final SpotLight[] spotLightList = sceneLight.getSpotLightList();
		numLights = spotLightList != null ? spotLightList.length : 0;
		for (int i = 0; i < numLights; i++) {
			// Get a copy of the spot light object and transform its position
			// and cone direction to view coordinates
			final SpotLight currSpotLight = new SpotLight(spotLightList[i]);
			final Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
			dir.mul(viewMatrix);
			currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

			final Vector3f lightPos = currSpotLight.getPointLight().getPosition();
			final Vector4f aux = new Vector4f(lightPos, 1);
			aux.mul(viewMatrix);
			lightPos.x = aux.x;
			lightPos.y = aux.y;
			lightPos.z = aux.z;

			sceneShaderProgram.setUniform("spotLights", currSpotLight, i);
		}

		// Get a copy of the directional light object and transform its position
		// to view coordinates
		final DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
		final Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
		dir.mul(viewMatrix);
		currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
		sceneShaderProgram.setUniform("directionalLight", currDirLight);
	}

	/** Render non-instanced meshes
	 * @param scene	Scene to render
	 * @param shader	Shader to render with
	 * @param viewMatrix	View matrix
	 * @param lightViewMatrix	Light view matrix
	 */
	private void renderNonInstancedMeshes(final Scene scene, final ShaderProgram shader, final Matrix4f viewMatrix,
			final Matrix4f lightViewMatrix) {
		sceneShaderProgram.setUniform("isInstanced", 0);

		// Render each mesh with the associated game Items
		final Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
		for (final Mesh mesh : mapMeshes.keySet()) {
			if (viewMatrix != null) {
				shader.setUniform("material", mesh.getMaterial());
				GL13.glActiveTexture(GL13.GL_TEXTURE2);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
			}

			final Texture text = mesh.getMaterial().getTexture();
			if (text != null) {
				sceneShaderProgram.setUniform("numCols", text.getNumCols());
				sceneShaderProgram.setUniform("numRows", text.getNumRows());
			}

			mesh.renderList(mapMeshes.get(mesh), (final GameItem gameItem) -> {
				sceneShaderProgram.setUniform("selectedNonInstanced", gameItem.isSelected() ? 1.0f : 0.0f);
				final Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
				if (viewMatrix != null) {
					final Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
					sceneShaderProgram.setUniform("modelViewNonInstancedMatrix", modelViewMatrix);
				}
				final Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(modelMatrix,
						lightViewMatrix);
				sceneShaderProgram.setUniform("modelLightViewNonInstancedMatrix", modelLightViewMatrix);

			});
		}
	}

	/** Render scene
	 * @param window	Window to render to
	 * @param camera	Camera to render from
	 * @param scene	Scene to render
	 */
	public void renderScene(final Window window, final Camera camera, final Scene scene) {
		sceneShaderProgram.bind();

		final Matrix4f projectionMatrix = window.getProjectionMatrix();
		sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);
		final Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
		sceneShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
		final Matrix4f lightViewMatrix = transformation.getLightViewMatrix();
		final Matrix4f viewMatrix = camera.getViewMatrix();

		final SceneLight sceneLight = scene.getSceneLight();
		renderLights(viewMatrix, sceneLight);

		sceneShaderProgram.setUniform("fog", scene.getFog());
		sceneShaderProgram.setUniform("texture_sampler", 0);
		sceneShaderProgram.setUniform("normalMap", 1);
		sceneShaderProgram.setUniform("shadowMap", 2);
		sceneShaderProgram.setUniform("renderShadow", scene.isRenderShadows() ? 1 : 0);

		renderNonInstancedMeshes(scene, sceneShaderProgram, viewMatrix, lightViewMatrix);

		renderInstancedMeshes(scene, sceneShaderProgram, viewMatrix, lightViewMatrix);

		sceneShaderProgram.unbind();
	}

	/** Set up depth shader
	 * @throws Exception
	 */
	private void setupDepthShader() throws Exception {
		depthShaderProgram = new ShaderProgram();
		depthShaderProgram.createVertexShader(Utils.loadResource("/shaders/depth_vertex.vs"));
		depthShaderProgram.createFragmentShader(Utils.loadResource("/shaders/depth_fragment.fs"));
		depthShaderProgram.link();

		depthShaderProgram.createUniform("isInstanced");
		depthShaderProgram.createUniform("jointsMatrix");
		depthShaderProgram.createUniform("modelLightViewNonInstancedMatrix");
		depthShaderProgram.createUniform("orthoProjectionMatrix");
	}

	/** Set up scene shader
	 * @throws Exception
	 */
	private void setupSceneShader() throws Exception {
		// Create shader
		sceneShaderProgram = new ShaderProgram();
		sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/scene_vertex.vs"));
		sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/scene_fragment.fs"));
		sceneShaderProgram.link();

		// Create uniforms for modelView and projection matrices
		sceneShaderProgram.createUniform("projectionMatrix");
		sceneShaderProgram.createUniform("modelViewNonInstancedMatrix");
		sceneShaderProgram.createUniform("texture_sampler");
		sceneShaderProgram.createUniform("normalMap");
		// Create uniform for material
		sceneShaderProgram.createMaterialUniform("material");
		// Create lighting related uniforms
		sceneShaderProgram.createUniform("specularPower");
		sceneShaderProgram.createUniform("ambientLight");
		sceneShaderProgram.createPointLightListUniform("pointLights", Renderer.MAX_POINT_LIGHTS);
		sceneShaderProgram.createSpotLightListUniform("spotLights", Renderer.MAX_SPOT_LIGHTS);
		sceneShaderProgram.createDirectionalLightUniform("directionalLight");
		sceneShaderProgram.createFogUniform("fog");

		// Create uniforms for shadow mapping
		sceneShaderProgram.createUniform("shadowMap");
		sceneShaderProgram.createUniform("orthoProjectionMatrix");
		sceneShaderProgram.createUniform("modelLightViewNonInstancedMatrix");
		sceneShaderProgram.createUniform("renderShadow");

		// Create uniform for joint matrices
		sceneShaderProgram.createUniform("jointsMatrix");

		sceneShaderProgram.createUniform("isInstanced");
		sceneShaderProgram.createUniform("numCols");
		sceneShaderProgram.createUniform("numRows");

		sceneShaderProgram.createUniform("selectedNonInstanced");
	}
}
