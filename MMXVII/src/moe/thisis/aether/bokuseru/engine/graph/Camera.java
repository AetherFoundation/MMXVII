package moe.thisis.aether.bokuseru.engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

	private final Vector3f position;

	private final Vector3f rotation;

	private Matrix4f viewMatrix;

	public Camera() {
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
		viewMatrix = new Matrix4f();
	}

	public Camera(final Vector3f position, final Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

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

	public void moveRotation(final float offsetX, final float offsetY, final float offsetZ) {
		rotation.x += offsetX;
		rotation.y += offsetY;
		rotation.z += offsetZ;
	}

	public void setPosition(final float x, final float y, final float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	public void setRotation(final float x, final float y, final float z) {
		rotation.x = x;
		rotation.y = y;
		rotation.z = z;
	}

	public Matrix4f updateViewMatrix() {
		return Transformation.updateGenericViewMatrix(position, rotation, viewMatrix);
	}
}