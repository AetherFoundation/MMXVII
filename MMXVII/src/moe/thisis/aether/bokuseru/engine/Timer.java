package moe.thisis.aether.bokuseru.engine;

public class Timer {

	private double lastLoopTime;

	/**
	 * @return	Elapsed time
	 */
	public float getElapsedTime() {
		final double time = getTime();
		final float elapsedTime = (float) (time - lastLoopTime);
		lastLoopTime = time;
		return elapsedTime;
	}

	/**
	 * @return	Last loop time
	 */
	public double getLastLoopTime() {
		return lastLoopTime;
	}

	/**
	 * @return Current time in seconds
	 */
	public double getTime() {
		return System.nanoTime() / 1000_000_000.0;
	}

	/** Initialize timer
	 * 
	 */
	public void init() {
		lastLoopTime = getTime();
	}
}