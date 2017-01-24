package moe.thisis.aether.bokuseru.engine.graph;

import org.joml.Vector3f;

public class Material {

	private static final Vector3f DEFAULT_COLOUR = new Vector3f(1.0f, 1.0f, 1.0f);

	private Vector3f colour;

	private float reflectance;

	private Texture texture;

	private Texture normalMap;

	public Material() {
		colour = Material.DEFAULT_COLOUR;
		reflectance = 0;
	}

	public Material(final Texture texture) {
		this();
		this.texture = texture;
	}

	public Material(final Texture texture, final float reflectance) {
		this();
		this.texture = texture;
		this.reflectance = reflectance;
	}

	public Material(final Vector3f colour, final float reflectance) {
		this();
		this.colour = colour;
		this.reflectance = reflectance;
	}

	public Vector3f getColour() {
		return colour;
	}

	public Texture getNormalMap() {
		return normalMap;
	}

	public float getReflectance() {
		return reflectance;
	}

	public Texture getTexture() {
		return texture;
	}

	public boolean hasNormalMap() {
		return normalMap != null;
	}

	public boolean isTextured() {
		return texture != null;
	}

	public void setColour(final Vector3f colour) {
		this.colour = colour;
	}

	public void setNormalMap(final Texture normalMap) {
		this.normalMap = normalMap;
	}

	public void setReflectance(final float reflectance) {
		this.reflectance = reflectance;
	}

	public void setTexture(final Texture texture) {
		this.texture = texture;
	}
}