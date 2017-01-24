package moe.thisis.aether.bokuseru.engine;

import org.joml.Vector3f;

import moe.thisis.aether.bokuseru.engine.graph.lights.DirectionalLight;
import moe.thisis.aether.bokuseru.engine.graph.lights.PointLight;
import moe.thisis.aether.bokuseru.engine.graph.lights.SpotLight;

public class SceneLight {

	private Vector3f ambientLight;

	private Vector3f skyBoxLight;

	private PointLight[] pointLightList;

	private SpotLight[] spotLightList;

	private DirectionalLight directionalLight;

	public Vector3f getAmbientLight() {
		return ambientLight;
	}

	public DirectionalLight getDirectionalLight() {
		return directionalLight;
	}

	public PointLight[] getPointLightList() {
		return pointLightList;
	}

	public Vector3f getSkyBoxLight() {
		return skyBoxLight;
	}

	public SpotLight[] getSpotLightList() {
		return spotLightList;
	}

	public void setAmbientLight(Vector3f ambientLight) {
		this.ambientLight = ambientLight;
	}

	public void setDirectionalLight(DirectionalLight directionalLight) {
		this.directionalLight = directionalLight;
	}

	public void setPointLightList(PointLight[] pointLightList) {
		this.pointLightList = pointLightList;
	}

	public void setSkyBoxLight(Vector3f skyBoxLight) {
		this.skyBoxLight = skyBoxLight;
	}

	public void setSpotLightList(SpotLight[] spotLightList) {
		this.spotLightList = spotLightList;
	}

}