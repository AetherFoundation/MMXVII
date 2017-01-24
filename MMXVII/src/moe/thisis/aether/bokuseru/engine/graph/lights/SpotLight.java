package moe.thisis.aether.bokuseru.engine.graph.lights;

import org.joml.Vector3f;

public class SpotLight {

	private PointLight pointLight;

	private Vector3f coneDirection;

	private float cutOff;

	public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
		this.pointLight = pointLight;
		this.coneDirection = coneDirection;
		setCutOffAngle(cutOffAngle);
	}

	public SpotLight(SpotLight spotLight) {
		this(new PointLight(spotLight.getPointLight()), new Vector3f(spotLight.getConeDirection()),
				spotLight.getCutOff());
	}

	public Vector3f getConeDirection() {
		return coneDirection;
	}

	public float getCutOff() {
		return cutOff;
	}

	public PointLight getPointLight() {
		return pointLight;
	}

	public void setConeDirection(Vector3f coneDirection) {
		this.coneDirection = coneDirection;
	}

	public void setCutOff(float cutOff) {
		this.cutOff = cutOff;
	}

	public final void setCutOffAngle(float cutOffAngle) {
		this.setCutOff((float) Math.cos(Math.toRadians(cutOffAngle)));
	}

	public void setPointLight(PointLight pointLight) {
		this.pointLight = pointLight;
	}

}
