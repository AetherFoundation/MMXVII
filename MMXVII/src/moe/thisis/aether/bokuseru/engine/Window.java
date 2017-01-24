package moe.thisis.aether.bokuseru.engine;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class Window {

	public static class WindowOptions {

		public boolean cullFace;

		public boolean showTriangles;

		public boolean showFps;

		public boolean compatibleProfile;

		public boolean antialiasing;
	}

	/**
	 * Field of View in Radians
	 */
	private static final float FOV = (float) Math.toRadians(60.0f);

	private static final float Z_NEAR = 0.01f;

	private static final float Z_FAR = 1000.f;

	private final String title;

	private int width;

	private int height;

	private long windowHandle;

	private GLFWErrorCallback errorCallback;

	private GLFWKeyCallback keyCallback;

	private GLFWWindowSizeCallback windowSizeCallback;

	private boolean resized;

	private boolean vSync;

	private final WindowOptions opts;

	private final Matrix4f projectionMatrix;

	public Window(final String title, final int width, final int height, final boolean vSync,
			final WindowOptions opts) {
		this.title = title;
		this.width = width;
		this.height = height;
		this.vSync = vSync;
		resized = false;
		this.opts = opts;
		projectionMatrix = new Matrix4f();
	}

	public int getHeight() {
		return height;
	}

	public WindowOptions getOptions() {
		return opts;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public String getTitle() {
		return title;
	}

	public int getWidth() {
		return width;
	}

	public long getWindowHandle() {
		return windowHandle;
	}

	public WindowOptions getWindowOptions() {
		return opts;
	}

	public String getWindowTitle() {
		return title;
	}

	public void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		GLFW.glfwDefaultWindowHints(); // optional, the current window hints are
										// already the default
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE); // the window
																// will stay
																// hidden
		// after creation
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE); // the window
																// will be
																// resizable
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
		if (opts.compatibleProfile) {
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE);
		} else {
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
		}

		boolean maximized = false;
		// If no size has been specified set it to maximized state
		if ((width == 0) || (height == 0)) {
			// Set up a fixed width and height so window initialization does not
			// fail
			width = 100;
			height = 100;
			GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
			maximized = true;
		}

		// Create the window
		windowHandle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		if (windowHandle == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Setup resize callback
		GLFW.glfwSetWindowSizeCallback(windowHandle, windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(final long window, final int width, final int height) {
				Window.this.width = width;
				Window.this.height = height;
				Window.this.setResized(true);
			}
		});

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		GLFW.glfwSetKeyCallback(windowHandle, keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(final long window, final int key, final int scancode, final int action, final int mods) {
				if ((key == GLFW.GLFW_KEY_ESCAPE) && (action == GLFW.GLFW_RELEASE)) {
					GLFW.glfwSetWindowShouldClose(window, true);
				}
			}
		});

		if (!maximized) {
			// Get the resolution of the primary monitor
			final GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			// Center our window
			GLFW.glfwSetWindowPos(windowHandle, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		}

		// Make the OpenGL context current
		GLFW.glfwMakeContextCurrent(windowHandle);

		if (isvSync()) {
			// Enable v-sync
			GLFW.glfwSwapInterval(1);
		}

		// Make the window visible
		GLFW.glfwShowWindow(windowHandle);

		GL.createCapabilities();

		// Set the clear color
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		if (opts.showTriangles) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}

		// Support for transparencies
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if (opts.cullFace) {
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
		}

		// Antialiasing
		if (opts.antialiasing) {
			GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
		}
	}

	public boolean isKeyPressed(final int keyCode) {
		return GLFW.glfwGetKey(windowHandle, keyCode) == GLFW.GLFW_PRESS;
	}

	public boolean isResized() {
		return resized;
	}

	public boolean isvSync() {
		return vSync;
	}

	public void setClearColor(final float r, final float g, final float b, final float alpha) {
		GL11.glClearColor(r, g, b, alpha);
	}

	public void setResized(final boolean resized) {
		this.resized = resized;
	}

	public void setvSync(final boolean vSync) {
		this.vSync = vSync;
	}

	public void setWindowTitle(final String title) {
		GLFW.glfwSetWindowTitle(windowHandle, title);
	}

	public void update() {
		GLFW.glfwSwapBuffers(windowHandle);
		GLFW.glfwPollEvents();
	}

	public Matrix4f updateProjectionMatrix() {
		final float aspectRatio = (float) width / (float) height;
		return projectionMatrix.setPerspective(Window.FOV, aspectRatio, Window.Z_NEAR, Window.Z_FAR);
	}

	public boolean windowShouldClose() {
		return GLFW.glfwWindowShouldClose(windowHandle);
	}
}
