package moe.thisis.aether.bokuseru.engine.graph.lights;

import org.joml.Vector3f;

public class DirectionalLight {

	public static class OrthoCoords {

		public float left;

		public float right;

		public float bottom;

		public float top;

		public float near;

		public float far;
	}

	private Vector3f color;

	private Vector3f direction;

	private float intensity;

	private final OrthoCoords orthoCords;

	private float shadowPosMult;

	public DirectionalLight(final DirectionalLight light) {
		this(new Vector3f(light.getColor()), new Vector3f(light.getDirection()), light.getIntensity());
	}

	public DirectionalLight(final Vector3f color, final Vector3f direction, final float intensity) {
		orthoCords = new OrthoCoords();
		shadowPosMult = 1;
		this.color = color;
		this.direction = direction;
		this.intensity = intensity;
		shadowPosMult = 1;
	}

	public Vector3f getColor() {
		return color;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public float getIntensity() {
		return intensity;
	}

	public OrthoCoords getOrthoCoords() {
		return orthoCords;
	}

	public float getShadowPosMult() {
		return shadowPosMult;
	}

	public void setColor(final Vector3f color) {
		this.color = color;
	}

	public void setDirection(final Vector3f direction) {
		this.direction = direction;
	}

	public void setIntensity(final float intensity) {
		this.intensity = intensity;
	}

	public void setOrthoCords(final float left, final float right, final float bottom, final float top,
			final float near, final float far) {
		orthoCords.left = left;
		orthoCords.right = right;
		orthoCords.bottom = bottom;
		orthoCords.top = top;
		orthoCords.near = near;
		orthoCords.far = far;
	}

	public void setShadowPosMult(final float shadowPosMult) {
		this.shadowPosMult = shadowPosMult;
	}
}