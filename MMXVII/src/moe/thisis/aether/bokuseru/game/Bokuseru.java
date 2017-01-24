package moe.thisis.aether.bokuseru.game;

import java.nio.ByteBuffer;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL11;

import de.matthiasmann.twl.utils.PNGDecoder;
import moe.thisis.aether.bokuseru.engine.IGameLogic;
import moe.thisis.aether.bokuseru.engine.MouseInput;
import moe.thisis.aether.bokuseru.engine.Scene;
import moe.thisis.aether.bokuseru.engine.SceneLight;
import moe.thisis.aether.bokuseru.engine.Window;
import moe.thisis.aether.bokuseru.engine.graph.Camera;
import moe.thisis.aether.bokuseru.engine.graph.HeightMapMesh;
import moe.thisis.aether.bokuseru.engine.graph.Material;
import moe.thisis.aether.bokuseru.engine.graph.Mesh;
import moe.thisis.aether.bokuseru.engine.graph.Renderer;
import moe.thisis.aether.bokuseru.engine.graph.Texture;
import moe.thisis.aether.bokuseru.engine.graph.lights.DirectionalLight;
import moe.thisis.aether.bokuseru.engine.items.GameItem;
import moe.thisis.aether.bokuseru.engine.items.Terrain;
import moe.thisis.aether.bokuseru.engine.loaders.obj.OBJLoader;
import moe.thisis.aether.bokuseru.engine.sound.SoundBuffer;
import moe.thisis.aether.bokuseru.engine.sound.SoundListener;
import moe.thisis.aether.bokuseru.engine.sound.SoundManager;
import moe.thisis.aether.bokuseru.engine.sound.SoundSource;

public class Bokuseru implements IGameLogic {

	private enum Sounds {
		MUSIC, BEEP
	}

	private static final float MOUSE_SENSITIVITY = 0.5f;

	private static final float CAMERA_POS_STEP = 0.10f;

	private final Vector3f cameraInc;

	private final Renderer renderer;

	private final SoundManager soundMgr;

	private final Camera camera;

	private Scene scene;

	private final Hud hud;

	private Terrain terrain;

	private float angleInc;

	private float lightAngle;

	private MouseBoxSelectionDetector selectDetector;

	private boolean leftButtonPressed;;

	private GameItem[] gameItems;

	public Bokuseru() {
		renderer = new Renderer();
		hud = new Hud();
		soundMgr = new SoundManager();
		camera = new Camera();
		cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
		angleInc = 0;
		lightAngle = 45;
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
		soundMgr.cleanup();

		scene.cleanup();
		if (hud != null) {
			hud.cleanup();
		}
	}

	@Override
	public void init(final Window window) throws Exception {
		hud.init(window);
		renderer.init(window);
		soundMgr.init();

		leftButtonPressed = false;

		scene = new Scene();

		final float reflectance = 0;

		final float blockScale = 0.5f;
		final float skyBoxScale = 100.0f;
		final float extension = 2.0f;

		final float startx = extension * (-skyBoxScale + blockScale);
		final float startz = extension * (skyBoxScale - blockScale);
		final float starty = -1.0f;
		final float inc = blockScale * 2;

		float posx = startx;
		float posz = startz;
		float incy = 0.0f;

		selectDetector = new MouseBoxSelectionDetector();

		final PNGDecoder decoder = new PNGDecoder(getClass().getResourceAsStream("/textures/heightmap.png"));
		final int height = decoder.getHeight();
		final int width = decoder.getWidth();
		final ByteBuffer buf = ByteBuffer.allocateDirect(4 * width * height);
		decoder.decode(buf, width * 4, PNGDecoder.Format.RGBA);
		buf.flip();

		final int instances = height * width;
		final Mesh mesh = OBJLoader.loadMesh("/models/cube.obj", instances);
		final Texture texture = new Texture("/textures/terrain_textures_hd.png", 2, 1);
		final Material material = new Material(texture, reflectance);
		mesh.setMaterial(material);
		gameItems = new GameItem[instances];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				final GameItem gameItem = new GameItem(mesh);
				gameItem.setScale(blockScale);
				final int rgb = HeightMapMesh.getRGB(i, j, width, buf);
				incy = rgb / (10 * 255 * 255);
				gameItem.setPosition(posx, starty + incy, posz);
				final int textPos = Math.random() > 0.5f ? 0 : 1;
				gameItem.setTextPos(textPos);
				gameItems[(i * width) + j] = gameItem;

				posx += inc;
			}
			posx = startx;
			posz -= inc;
		}
		scene.setGameItems(gameItems);

		// Shadows
		scene.setRenderShadows(false);

		// Fog
		// Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
		// scene.setFog(new Fog(true, fogColour, 0.02f));

		// Setup Lights
		setupLights();

		camera.getPosition().x = 0.25f;
		camera.getPosition().y = 6.5f;
		camera.getPosition().z = 6.5f;
		camera.getRotation().x = 25;
		camera.getRotation().y = -1;

		// Sounds
		soundMgr.init();
		soundMgr.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
		setupSounds();
	}

	@Override
	public void input(final Window window, final MouseInput mouseInput) {
		cameraInc.set(0, 0, 0);
		if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
			cameraInc.z = -3;
		} else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
			cameraInc.z = 3;
		}
		if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
			cameraInc.x = -3;
		} else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
			cameraInc.x = 3;
		}
		if (window.isKeyPressed(GLFW.GLFW_KEY_Z)) {
			cameraInc.y = -3;
		} else if (window.isKeyPressed(GLFW.GLFW_KEY_X)) {
			cameraInc.y = 3;
		}
		if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
			angleInc -= 0.05f;
			soundMgr.playSoundSource(Sounds.BEEP.toString());
		} else if (window.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
			angleInc += 0.05f;
			soundMgr.playSoundSource(Sounds.BEEP.toString());
		} else {
			angleInc = 0;
		}

	}

	@Override
	public void render(final Window window) {
		renderer.render(window, camera, scene);
		hud.render(window);
	}

	private void setupLights() {
		final SceneLight sceneLight = new SceneLight();
		scene.setSceneLight(sceneLight);

		// Ambient Light
		sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));

		// Directional Light
		final float lightIntensity = 1.0f;
		final Vector3f lightDirection = new Vector3f(0, 1, 1);
		final DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection,
				lightIntensity);
		directionalLight.setShadowPosMult(10);
		directionalLight.setOrthoCords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);
		sceneLight.setDirectionalLight(directionalLight);
	}

	private void setupSounds() throws Exception {
		final SoundBuffer buffBack = new SoundBuffer("/sounds/background.ogg");
		soundMgr.addSoundBuffer(buffBack);
		final SoundSource sourceBack = new SoundSource(true, true);
		sourceBack.setBuffer(buffBack.getBufferId());
		soundMgr.addSoundSource(Sounds.MUSIC.toString(), sourceBack);

		final SoundBuffer buffBeep = new SoundBuffer("/sounds/beep.ogg");
		soundMgr.addSoundBuffer(buffBeep);
		final SoundSource sourceBeep = new SoundSource(false, true);
		sourceBeep.setBuffer(buffBeep.getBufferId());
		soundMgr.addSoundSource(Sounds.BEEP.toString(), sourceBeep);

		soundMgr.setListener(new SoundListener(new Vector3f(0, 0, 0)));

		sourceBack.play();
	}

	@Override
	public void update(final float interval, final MouseInput mouseInput, final Window window) {
		if (mouseInput.isRightButtonPressed()) {
			// Update camera based on mouse
			final Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * Bokuseru.MOUSE_SENSITIVITY, rotVec.y * Bokuseru.MOUSE_SENSITIVITY, 0);
		}

		// Update camera position
		final Vector3f prevPos = new Vector3f(camera.getPosition());
		camera.movePosition(cameraInc.x * Bokuseru.CAMERA_POS_STEP, cameraInc.y * Bokuseru.CAMERA_POS_STEP,
				cameraInc.z * Bokuseru.CAMERA_POS_STEP);
		// Check if there has been a collision. If true, set the y position to
		// the maximum height
		final float height = terrain != null ? terrain.getHeight(camera.getPosition()) : -Float.MAX_VALUE;
		if (camera.getPosition().y <= height) {
			camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
		}

		lightAngle += angleInc;
		if (lightAngle < 0) {
			lightAngle = 0;
		} else if (lightAngle > 180) {
			lightAngle = 180;
		}
		final float zValue = (float) Math.cos(Math.toRadians(lightAngle));
		final float yValue = (float) Math.sin(Math.toRadians(lightAngle));
		final Vector3f lightDirection = scene.getSceneLight().getDirectionalLight().getDirection();
		lightDirection.x = 0;
		lightDirection.y = yValue;
		lightDirection.z = zValue;
		lightDirection.normalize();

		// Update view matrix
		camera.updateViewMatrix();

		// Update sound listener position;
		soundMgr.updateListenerPosition(camera);

		final boolean aux = mouseInput.isLeftButtonPressed();
		if (aux && !leftButtonPressed
				&& selectDetector.selectGameItem(gameItems, window, mouseInput.getCurrentPos(), camera)) {
			hud.incCounter();
		}
		leftButtonPressed = aux;
	}
}
