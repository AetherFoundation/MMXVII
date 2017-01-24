package moe.thisis.aether.bokuseru.engine.graph.lights;

import org.joml.Vector3f;

public class PointLight {

	public static class Attenuation {

		private float constant;

		private float linear;

		private float exponent;

		public Attenuation(final float constant, final float linear, final float exponent) {
			this.constant = constant;
			this.linear = linear;
			this.exponent = exponent;
		}

		public float getConstant() {
			return constant;
		}

		public float getExponent() {
			return exponent;
		}

		public float getLinear() {
			return linear;
		}

		public void setConstant(final float constant) {
			this.constant = constant;
		}

		public void setExponent(final float exponent) {
			this.exponent = exponent;
		}

		public void setLinear(final float linear) {
			this.linear = linear;
		}
	}

	private Vector3f color;

	private Vector3f position;

	private float intensity;

	private Attenuation attenuation;

	public PointLight(final PointLight pointLight) {
		this(new Vector3f(pointLight.getColor()), new Vector3f(pointLight.getPosition()), pointLight.getIntensity(),
				pointLight.getAttenuation());
	}

	public PointLight(final Vector3f color, final Vector3f position, final float intensity) {
		attenuation = new Attenuation(1, 0, 0);
		this.color = color;
		this.position = position;
		this.intensity = intensity;
	}

	public PointLight(final Vector3f color, final Vector3f position, final float intensity,
			final Attenuation attenuation) {
		this(color, position, intensity);
		this.attenuation = attenuation;
	}

	public Attenuation getAttenuation() {
		return attenuation;
	}

	public Vector3f getColor() {
		return color;
	}

	public float getIntensity() {
		return intensity;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setAttenuation(final Attenuation attenuation) {
		this.attenuation = attenuation;
	}

	public void setColor(final Vector3f color) {
		this.color = color;
	}

	public void setIntensity(final float intensity) {
		this.intensity = intensity;
	}

	public void setPosition(final Vector3f position) {
		this.position = position;
	}
}