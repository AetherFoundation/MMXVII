package moe.thisis.aether.bokuseru.engine.graph;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import moe.thisis.aether.bokuseru.engine.items.GameItem;

public class Transformation {

	public static Matrix4f updateGenericViewMatrix(final Vector3f position, final Vector3f rotation,
			final Matrix4f matrix) {
		// First do the rotation so camera rotates over its position
		return matrix.rotationX((float) Math.toRadians(rotation.x)).rotateY((float) Math.toRadians(rotation.y))
				.translate(-position.x, -position.y, -position.z);
	}

	private final Matrix4f modelMatrix;

	private final Matrix4f modelViewMatrix;

	private final Matrix4f modelLightViewMatrix;

	private final Matrix4f lightViewMatrix;

	private final Matrix4f orthoProjMatrix;

	private final Matrix4f ortho2DMatrix;

	private final Matrix4f orthoModelMatrix;

	public Transformation() {
		modelMatrix = new Matrix4f();
		modelViewMatrix = new Matrix4f();
		modelLightViewMatrix = new Matrix4f();
		orthoProjMatrix = new Matrix4f();
		ortho2DMatrix = new Matrix4f();
		orthoModelMatrix = new Matrix4f();
		lightViewMatrix = new Matrix4f();
	}

	public Matrix4f buildModelLightViewMatrix(final GameItem gameItem, final Matrix4f lightViewMatrix) {
		return buildModelViewMatrix(buildModelMatrix(gameItem), lightViewMatrix);
	}

	public Matrix4f buildModelLightViewMatrix(final Matrix4f modelMatrix, final Matrix4f lightViewMatrix) {
		return lightViewMatrix.mulAffine(modelMatrix, modelLightViewMatrix);
	}

	public Matrix4f buildModelMatrix(final GameItem gameItem) {
		final Quaternionf rotation = gameItem.getRotation();
		return modelMatrix.translationRotateScale(gameItem.getPosition().x, gameItem.getPosition().y,
				gameItem.getPosition().z, rotation.x, rotation.y, rotation.z, rotation.w, gameItem.getScale(),
				gameItem.getScale(), gameItem.getScale());
	}

	public Matrix4f buildModelViewMatrix(final GameItem gameItem, final Matrix4f viewMatrix) {
		return buildModelViewMatrix(buildModelMatrix(gameItem), viewMatrix);
	}

	public Matrix4f buildModelViewMatrix(final Matrix4f modelMatrix, final Matrix4f viewMatrix) {
		return viewMatrix.mulAffine(modelMatrix, modelViewMatrix);
	}

	public Matrix4f buildOrthoProjModelMatrix(final GameItem gameItem, final Matrix4f orthoMatrix) {
		return orthoMatrix.mulOrthoAffine(buildModelMatrix(gameItem), orthoModelMatrix);
	}

	public Matrix4f getLightViewMatrix() {
		return lightViewMatrix;
	}

	public final Matrix4f getOrtho2DProjectionMatrix(final float left, final float right, final float bottom,
			final float top) {
		return ortho2DMatrix.setOrtho2D(left, right, bottom, top);
	}

	public final Matrix4f getOrthoProjectionMatrix() {
		return orthoProjMatrix;
	}

	public void setLightViewMatrix(final Matrix4f lightViewMatrix) {
		this.lightViewMatrix.set(lightViewMatrix);
	}

	public Matrix4f updateLightViewMatrix(final Vector3f position, final Vector3f rotation) {
		return Transformation.updateGenericViewMatrix(position, rotation, lightViewMatrix);
	}

	public Matrix4f updateOrthoProjectionMatrix(final float left, final float right, final float bottom,
			final float top, final float zNear, final float zFar) {
		return orthoProjMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
	}
}
