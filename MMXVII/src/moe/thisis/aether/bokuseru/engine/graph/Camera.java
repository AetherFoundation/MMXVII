package moe.thisis.aether.bokuseru.engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

	private final Vector3f position;

	private final Vector3f rotation;

	private Matrix4f viewMatrix;

	/** Empty Camera Constructor
	 * 
	 */
	public Camera() {
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
		viewMatrix = new Matrix4f();
	}

	/** Camera Constructor
	 * @param position	Camera position
	 * @param rotation	Camera rotation
	 */
	public Camera(final Vector3f position, final Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
	}

	/**
	 * @return	Camera position
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @return	Camera rotation
	 */
	public Vector3f getRotation() {
		return rotation;
	}

	/**
	 * @return	View matrix
	 */
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	/** Move the relative camera position
	 * @param offsetX	X axis offset
	 * @param offsetY	Y axis offset
	 * @param offsetZ	Z axis offset
	 */
	public void movePosition(final float offsetX, final float offsetY, final float offsetZ) {
		if (offsetZ != 0) {
			position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
			position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
		}
		if (offsetX != 0) {
			position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
			position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
		}
		position.y += offsetY;
	}

	/** Move the relative camera rotation
	 * @param offsetX	X rotational offset
	 * @param offsetY	Y rotational offset
	 * @param offsetZ	Z rotational offset
	 */
	public void moveRotation(final float offsetX, final float offsetY, final float offsetZ) {
		rotation.x += offsetX;
		rotation.y += offsetY;
		rotation.z += offsetZ;
	}

	/** Move the absolute camera position
	 * @param x	X axis position
	 * @param y	Y axis position
	 * @param z	Z axis position
	 */
	public void setPosition(final float x, final float y, final float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	/** Move the absolute camera rotation
	 * @param x X axis rotation
	 * @param y	Y axis rotation
	 * @param z	Z axis rotation
	 */
	public void setRotation(final float x, final float y, final float z) {
		rotation.x = x;
		rotation.y = y;
		rotation.z = z;
	}

	/** Update the view matrix
	 * @return Updated view matrix
	 */
	public Matrix4f updateViewMatrix() {
		return Transformation.updateGenericViewMatrix(position, rotation, viewMatrix);
	}
}