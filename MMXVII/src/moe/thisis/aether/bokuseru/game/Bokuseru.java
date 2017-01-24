package moe.thisis.aether.bokuseru.game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

import java.nio.ByteBuffer;

import org.joml.Vector2f;
import org.joml.Vector3f;
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

	private Hud hud;

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
	public void init(Window window) throws Exception {
		hud.init(window);
		renderer.init(window);
		soundMgr.init();

		leftButtonPressed = false;

		scene = new Scene();

		float reflectance = 0;

		float blockScale = 0.5f;
		float skyBoxScale = 100.0f;
		float extension = 2.0f;

		float startx = extension * (-skyBoxScale + blockScale);
		float startz = extension * (skyBoxScale - blockScale);
		float starty = -1.0f;
		float inc = blockScale * 2;

		float posx = startx;
		float posz = startz;
		float incy = 0.0f;

		selectDetector = new MouseBoxSelectionDetector();

		PNGDecoder decoder = new PNGDecoder(getClass().getResourceAsStream("/textures/heightmap.png"));
		int height = decoder.getHeight();
		int width = decoder.getWidth();
		ByteBuffer buf = ByteBuffer.allocateDirect(4 * width * height);
		decoder.decode(buf, width * 4, PNGDecoder.Format.RGBA);
		buf.flip();

		int instances = height * width;
		Mesh mesh = OBJLoader.loadMesh("/models/cube.obj", instances);
		Texture texture = new Texture("/textures/terrain_textures_hd.png", 2, 1);
		Material material = new Material(texture, reflectance);
		mesh.setMaterial(material);
		gameItems = new GameItem[instances];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				GameItem gameItem = new GameItem(mesh);
				gameItem.setScale(blockScale);
				int rgb = HeightMapMesh.getRGB(i, j, width, buf);
				incy = rgb / (10 * 255 * 255);
				gameItem.setPosition(posx, starty + incy, posz);
				int textPos = Math.random() > 0.5f ? 0 : 1;
				gameItem.setTextPos(textPos);
				gameItems[i * width + j] = gameItem;

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
		this.soundMgr.init();
		this.soundMgr.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
		setupSounds();
	}

	@Override
	public void input(Window window, MouseInput mouseInput) {
		cameraInc.set(0, 0, 0);
		if (window.isKeyPressed(GLFW_KEY_W)) {
			cameraInc.z = -3;
		} else if (window.isKeyPressed(GLFW_KEY_S)) {
			cameraInc.z = 3;
		}
		if (window.isKeyPressed(GLFW_KEY_A)) {
			cameraInc.x = -3;
		} else if (window.isKeyPressed(GLFW_KEY_D)) {
			cameraInc.x = 3;
		}
		if (window.isKeyPressed(GLFW_KEY_Z)) {
			cameraInc.y = -3;
		} else if (window.isKeyPressed(GLFW_KEY_X)) {
			cameraInc.y = 3;
		}
		if (window.isKeyPressed(GLFW_KEY_LEFT)) {
			angleInc -= 0.05f;
			soundMgr.playSoundSource(Sounds.BEEP.toString());
		} else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
			angleInc += 0.05f;
			soundMgr.playSoundSource(Sounds.BEEP.toString());
		} else {
			angleInc = 0;
		}

	}

	@Override
	public void render(Window window) {
		renderer.render(window, camera, scene);
		hud.render(window);
	}

	private void setupLights() {
		SceneLight sceneLight = new SceneLight();
		scene.setSceneLight(sceneLight);

		// Ambient Light
		sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
		sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

		// Directional Light
		float lightIntensity = 1.0f;
		Vector3f lightDirection = new Vector3f(0, 1, 1);
		DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
		directionalLight.setShadowPosMult(10);
		directionalLight.setOrthoCords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);
		sceneLight.setDirectionalLight(directionalLight);
	}

	private void setupSounds() throws Exception {
		SoundBuffer buffBack = new SoundBuffer("/sounds/background.ogg");
		soundMgr.addSoundBuffer(buffBack);
		SoundSource sourceBack = new SoundSource(true, true);
		sourceBack.setBuffer(buffBack.getBufferId());
		soundMgr.addSoundSource(Sounds.MUSIC.toString(), sourceBack);

		SoundBuffer buffBeep = new SoundBuffer("/sounds/beep.ogg");
		soundMgr.addSoundBuffer(buffBeep);
		SoundSource sourceBeep = new SoundSource(false, true);
		sourceBeep.setBuffer(buffBeep.getBufferId());
		soundMgr.addSoundSource(Sounds.BEEP.toString(), sourceBeep);

		soundMgr.setListener(new SoundListener(new Vector3f(0, 0, 0)));

		sourceBack.play();
	}

	@Override
	public void update(float interval, MouseInput mouseInput, Window window) {
		if (mouseInput.isRightButtonPressed()) {
			// Update camera based on mouse
			Vector2f rotVec = mouseInput.getDisplVec();
			camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
		}

		// Update camera position
		Vector3f prevPos = new Vector3f(camera.getPosition());
		camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP,
				cameraInc.z * CAMERA_POS_STEP);
		// Check if there has been a collision. If true, set the y position to
		// the maximum height
		float height = terrain != null ? terrain.getHeight(camera.getPosition()) : -Float.MAX_VALUE;
		if (camera.getPosition().y <= height) {
			camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
		}

		lightAngle += angleInc;
		if (lightAngle < 0) {
			lightAngle = 0;
		} else if (lightAngle > 180) {
			lightAngle = 180;
		}
		float zValue = (float) Math.cos(Math.toRadians(lightAngle));
		float yValue = (float) Math.sin(Math.toRadians(lightAngle));
		Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
		lightDirection.x = 0;
		lightDirection.y = yValue;
		lightDirection.z = zValue;
		lightDirection.normalize();

		// Update view matrix
		camera.updateViewMatrix();

		// Update sound listener position;
		soundMgr.updateListenerPosition(camera);

		boolean aux = mouseInput.isLeftButtonPressed();
		if (aux && !this.leftButtonPressed
				&& this.selectDetector.selectGameItem(gameItems, window, mouseInput.getCurrentPos(), camera)) {
			this.hud.incCounter();
		}
		this.leftButtonPressed = aux;
	}
}
