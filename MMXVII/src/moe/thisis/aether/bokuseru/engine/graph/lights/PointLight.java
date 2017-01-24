package moe.thisis.aether.bokuseru.engine.graph.lights;

import org.joml.Vector3f;

public class PointLight {

	public static class Attenuation {

		private float constant;

		private float linear;

		private float exponent;

		public Attenuation(float constant, float linear, float exponent) {
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

		public void setConstant(float constant) {
			this.constant = constant;
		}

		public void setExponent(float exponent) {
			this.exponent = exponent;
		}

		public void setLinear(float linear) {
			this.linear = linear;
		}
	}

	private Vector3f color;

	private Vector3f position;

	private float intensity;

	private Attenuation attenuation;

	public PointLight(PointLight pointLight) {
		this(new Vector3f(pointLight.getColor()), new Vector3f(pointLight.getPosition()), pointLight.getIntensity(),
				pointLight.getAttenuation());
	}

	public PointLight(Vector3f color, Vector3f position, float intensity) {
		attenuation = new Attenuation(1, 0, 0);
		this.color = color;
		this.position = position;
		this.intensity = intensity;
	}

	public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation attenuation) {
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

	public void setAttenuation(Attenuation attenuation) {
		this.attenuation = attenuation;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
}