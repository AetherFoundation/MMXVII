package moe.thisis.aether.bokuseru.engine.graph;

import org.joml.Vector3f;

public class Material {

	private static final Vector3f DEFAULT_COLOUR = new Vector3f(1.0f, 1.0f, 1.0f);

	private Vector3f colour;

	private float reflectance;

	private Texture texture;

	private Texture normalMap;

	/** Material constructor
	 * 
	 */
	public Material() {
		colour = Material.DEFAULT_COLOUR;
		reflectance = 0;
	}

	/** Material constructor
	 * @param texture	Texture
	 */
	public Material(final Texture texture) {
		this();
		this.texture = texture;
	}

	/** Material constructor
	 * @param texture	Texture
	 * @param reflectance	Reflectance
	 */
	public Material(final Texture texture, final float reflectance) {
		this();
		this.texture = texture;
		this.reflectance = reflectance;
	}

	/** Material constructor
	 * @param colour	Colour
	 * @param reflectance	Reflectance
	 */
	public Material(final Vector3f colour, final float reflectance) {
		this();
		this.colour = colour;
		this.reflectance = reflectance;
	}

	/** Get current colour
	 * @return	Colour
	 */
	public Vector3f getColour() {
		return colour;
	}

	/** Get current normal map
	 * @return	Normal map
	 */
	public Texture getNormalMap() {
		return normalMap;
	}

	/** Get current reflectance
	 * @return	Reflectance
	 */
	public float getReflectance() {
		return reflectance;
	}

	/** Get current texture
	 * @return	Texture
	 */
	public Texture getTexture() {
		return texture;
	}

	/** Check if there is a normal map
	 * @return	Has normal map
	 */
	public boolean hasNormalMap() {
		return normalMap != null;
	}

	/** Check if there is a texture
	 * @return	Has texture
	 */
	public boolean isTextured() {
		return texture != null;
	}

	/** Set colour
	 * @param colour	Colour
	 */
	public void setColour(final Vector3f colour) {
		this.colour = colour;
	}

	/** Set normal map
	 * @param normalMap	Normal map
	 */
	public void setNormalMap(final Texture normalMap) {
		this.normalMap = normalMap;
	}

	/** Set reflectance
	 * @param reflectance	Reflectance
	 */
	public void setReflectance(final float reflectance) {
		this.reflectance = reflectance;
	}

	/** Set texture
	 * @param texture	Texture
	 */
	public void setTexture(final Texture texture) {
		this.texture = texture;
	}
}