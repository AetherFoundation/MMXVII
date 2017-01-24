package moe.thisis.aether.bokuseru.engine.graph.weather;

import org.joml.Vector3f;

public class Fog {

	public static Fog NOFOG = new Fog();

	private boolean active;

	private Vector3f colour;

	private float density;

	public Fog() {
		active = false;
		colour = new Vector3f(0, 0, 0);
		density = 0;
	}

	public Fog(final boolean active, final Vector3f colour, final float density) {
		this.colour = colour;
		this.density = density;
		this.active = active;
	}

	/**
	 * @return the colour
	 */
	public Vector3f getColour() {
		return colour;
	}

	/**
	 * @return the density
	 */
	public float getDensity() {
		return density;
	}

	/**
	 * @return the state
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            state to set
	 */
	public void setActive(final boolean active) {
		this.active = active;
	}

	/**
	 * @param colour
	 *            the colour to set
	 */
	public void setColour(final Vector3f colour) {
		this.colour = colour;
	}

	/**
	 * @param density
	 *            the density to set
	 */
	public void setDensity(final float density) {
		this.density = density;
	}
}
