package moe.thisis.aether.bokuseru.engine;

public class GameEngine implements Runnable {

	public static final int TARGET_FPS = 75;

	public static final int TARGET_UPS = 30;

	private final Window window;

	private final Thread gameLoopThread;

	private final Timer timer;

	private final IGameLogic gameLogic;

	private final MouseInput mouseInput;

	private double lastFps;

	private int fps;

	private final String windowTitle;

	/**
	 * Create an engine instance without specifying window size
	 *
	 * @param windowTitle
	 *            Title of the game window
	 * @param vSync
	 *            Enable vertical sync
	 * @param opts
	 *            Additional window options
	 * @param gameLogic
	 *            Game logic to use
	 * @throws Exception
	 */
	public GameEngine(final String windowTitle, final boolean vSync, final Window.WindowOptions opts,
			final IGameLogic gameLogic) throws Exception {
		this(windowTitle, 0, 0, vSync, opts, gameLogic);
	}

	/**
	 * Create an engine instance with a specific window size
	 *
	 * @param windowTitle
	 *            Title of the game window
	 * @param width
	 *            Window width
	 * @param height
	 *            Window height
	 * @param vSync
	 *            Enable vertical sync
	 * @param opts
	 *            Additional window options
	 * @param gameLogic
	 *            Game logic to use
	 * @throws Exception
	 */
	public GameEngine(final String windowTitle, final int width, final int height, final boolean vSync,
			final Window.WindowOptions opts, final IGameLogic gameLogic) throws Exception {
		this.windowTitle = windowTitle;
		gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
		window = new Window(windowTitle, width, height, vSync, opts);
		mouseInput = new MouseInput();
		this.gameLogic = gameLogic;
		timer = new Timer();
	}

	/**
	 * Clear the state of the game logic
	 *
	 */
	protected void cleanup() {
		gameLogic.cleanup();
	}

	/**
	 * Game engine execution loop
	 *
	 */
	protected void gameLoop() {
		float elapsedTime;
		float accumulator = 0f;
		final float interval = 1f / GameEngine.TARGET_UPS;

		final boolean running = true;
		while (running && !window.windowShouldClose()) {
			elapsedTime = timer.getElapsedTime();
			accumulator += elapsedTime;

			input(); // handle user input

			while (accumulator >= interval) {
				update(interval);
				accumulator -= interval;
			}

			render(); // render the scene

			if (!window.isvSync()) {
				sync();
			}
		}
	}

	/**
	 * Initialize the game engine
	 *
	 * @throws Exception
	 */
	protected void init() throws Exception {
		window.init();
		timer.init();
		mouseInput.init(window);
		gameLogic.init(window);
		lastFps = timer.getTime();
		fps = 0;
	}

	/**
	 * Handle user input
	 *
	 */
	protected void input() {
		mouseInput.input(window);
		gameLogic.input(window, mouseInput);
	}

	/**
	 * Render the game to the window
	 *
	 */
	protected void render() {
		if (window.getWindowOptions().showFps && ((timer.getLastLoopTime() - lastFps) > 1)) {
			lastFps = timer.getLastLoopTime();
			window.setWindowTitle(windowTitle + " - " + fps + " FPS");
			fps = 0;
		}
		fps++;
		gameLogic.render(window);
		window.update();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			init();
			gameLoop();
		} catch (final Exception excp) {
			excp.printStackTrace();
		} finally {
			cleanup();
		}
	}

	/**
	 * Start the game engine Handles thread launching differently for macOS
	 */
	public void start() {
		final String osName = System.getProperty("os.name");
		if (osName.contains("Mac")) {
			gameLoopThread.run();
		} else {
			gameLoopThread.start();
		}
	}

	/**
	 * Synchronize execution to the target framerate
	 *
	 */
	private void sync() {
		final float loopSlot = 1f / GameEngine.TARGET_FPS;
		final double endTime = timer.getLastLoopTime() + loopSlot;
		while (timer.getTime() < endTime) {
			try {
				Thread.sleep(1);
			} catch (final InterruptedException ie) {
			}
		}
	}

	/**
	 * Update the game logic
	 *
	 * @param interval
	 *            Update interval
	 */
	protected void update(final float interval) {
		gameLogic.update(interval, mouseInput, window);
	}

}
