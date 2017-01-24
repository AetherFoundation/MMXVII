package moe.thisis.aether.bokuseru.game;

import moe.thisis.aether.bokuseru.engine.GameEngine;
import moe.thisis.aether.bokuseru.engine.IGameLogic;
import moe.thisis.aether.bokuseru.engine.Window;

public class Main {

	/**
	 * Bokuseru Tech Demo
	 *
	 * @author Quinlan McNellen
	 * @version 1.1a
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			final boolean vSync = true; // enable vertical synchronization
			final IGameLogic gameLogic = new Bokuseru(); // specify which game
															// logic
			// the engine will run
			final Window.WindowOptions opts = new Window.WindowOptions(); // create
			// the GLFW
			// window
			opts.cullFace = true;
			opts.showFps = true;
			opts.compatibleProfile = true;
			opts.antialiasing = true; // smooth sharp edges
			final GameEngine gameEng = new GameEngine("Bokuseru", vSync, opts, gameLogic); // specify
																							// which
																							// game
																							// the
																							// engine
																							// will
																							// run
			gameEng.start();
		} catch (final Exception excp) {
			excp.printStackTrace();
			System.exit(-1);
		}
	}
}
