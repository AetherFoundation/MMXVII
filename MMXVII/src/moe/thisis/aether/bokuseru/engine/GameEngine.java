package moe.thisis.aether.bokuseru.engine;

import moe.thisis.aether.bokuseru.engine.sound.SoundManager;

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
    
    private String windowTitle;
    
    /** Create an engine instance without specifying window size
     * @param windowTitle	Title of the game window
     * @param vSync			Enable vertical sync
     * @param opts			Additional window options
     * @param gameLogic		Game logic to use
     * @throws Exception
     */
    public GameEngine(String windowTitle, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic) throws Exception {
        this(windowTitle, 0, 0, vSync, opts, gameLogic);
    }

    /** Create an engine instance with a specific window size
     * @param windowTitle	Title of the game window
     * @param width			Window width
     * @param height		Window height
     * @param vSync			Enable vertical sync
     * @param opts			Additional window options
     * @param gameLogic		Game logic to use
     * @throws Exception
     */
    public GameEngine(String windowTitle, int width, int height, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic) throws Exception {
        this.windowTitle = windowTitle;
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        window = new Window(windowTitle, width, height, vSync, opts);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
        timer = new Timer();
    }

    /** Start the game engine
     * Handles thread launching differently for macOS
     */
    public void start() {
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            cleanup();
        }
    }

    /** Initialize the game engine
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

    /** Game engine execution loop
     * 
     */
    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input(); //handle user input

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render(); //render the scene

            if ( !window.isvSync() ) {
                sync();
            }
        }
    }

    /** Clear the state of the game logic
     * 
     */
    protected void cleanup() {
        gameLogic.cleanup();
    }
    
    /** Synchronize execution to the target framerate
     * 
     */
    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    /** Handle user input
     * 
     */
    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    /** Update the game logic
     * @param interval	Update interval
     */
    protected void update(float interval) {
        gameLogic.update(interval, mouseInput, window);
    }

    /** Render the game to the window
     * 
     */
    protected void render() {
        if ( window.getWindowOptions().showFps && timer.getLastLoopTime() - lastFps > 1 ) {
            lastFps = timer.getLastLoopTime();
            window.setWindowTitle(windowTitle + " - " + fps + " FPS");
            fps = 0;
        }
        fps++;
        gameLogic.render(window);
        window.update();
    }

}
