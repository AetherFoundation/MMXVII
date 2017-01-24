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

	/**
	 * @return	Ambient light
	 */
	public Vector3f getAmbientLight() {
		return ambientLight;
	}

	/**
	 * @return	Directional light
	 */
	public DirectionalLight getDirectionalLight() {
		return directionalLight;
	}

	/**
	 * @return	Array of point lights
	 */
	public PointLight[] getPointLightList() {
		return pointLightList;
	}

	/**
	 * @return	Skybox light
	 */
	public Vector3f getSkyBoxLight() {
		return skyBoxLight;
	}

	/**
	 * @return	Array of spotlights
	 */
	public SpotLight[] getSpotLightList() {
		return spotLightList;
	}

	/**
	 * @param ambientLight	Ambient light to use
	 */
	public void setAmbientLight(final Vector3f ambientLight) {
		this.ambientLight = ambientLight;
	}

	/**
	 * @param directionalLight	Directional light to use
	 */
	public void setDirectionalLight(final DirectionalLight directionalLight) {
		this.directionalLight = directionalLight;
	}

	/**
	 * @param pointLightList	Point lights to use
	 */
	public void setPointLightList(final PointLight[] pointLightList) {
		this.pointLightList = pointLightList;
	}

	/**
	 * @param skyBoxLight	Skybox light to use
	 */
	public void setSkyBoxLight(final Vector3f skyBoxLight) {
		this.skyBoxLight = skyBoxLight;
	}

	/**
	 * @param spotLightList	Spotlights to use
	 */
	public void setSpotLightList(final SpotLight[] spotLightList) {
		this.spotLightList = spotLightList;
	}

}