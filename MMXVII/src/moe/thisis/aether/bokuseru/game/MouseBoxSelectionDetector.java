package moe.thisis.aether.bokuseru.game;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import moe.thisis.aether.bokuseru.engine.Window;
import moe.thisis.aether.bokuseru.engine.graph.Camera;
import moe.thisis.aether.bokuseru.engine.items.GameItem;

public class MouseBoxSelectionDetector extends CameraBoxSelectionDetector {

	private final Matrix4f invProjectionMatrix;

	private final Matrix4f invViewMatrix;

	private final Vector3f mouseDir;

	private final Vector4f tmpVec;

	public MouseBoxSelectionDetector() {
		super();
		invProjectionMatrix = new Matrix4f();
		invViewMatrix = new Matrix4f();
		mouseDir = new Vector3f();
		tmpVec = new Vector4f();
	}

	public boolean selectGameItem(final GameItem[] gameItems, final Window window, final Vector2d mousePos,
			final Camera camera) {
		// Transform mouse coordinates into normalized space [-1, 1]
		final int wdwWitdh = window.getWidth();
		final int wdwHeight = window.getHeight();

		final float x = ((float) (2 * mousePos.x) / wdwWitdh) - 1.0f;
		final float y = 1.0f - ((float) (2 * mousePos.y) / wdwHeight);
		final float z = -1.0f;

		invProjectionMatrix.set(window.getProjectionMatrix());
		invProjectionMatrix.invert();

		tmpVec.set(x, y, z, 1.0f);
		tmpVec.mul(invProjectionMatrix);
		tmpVec.z = -1.0f;
		tmpVec.w = 0.0f;

		final Matrix4f viewMatrix = camera.getViewMatrix();
		invViewMatrix.set(viewMatrix);
		invViewMatrix.invert();
		tmpVec.mul(invViewMatrix);

		mouseDir.set(tmpVec.x, tmpVec.y, tmpVec.z);

		return selectGameItem(gameItems, camera.getPosition(), mouseDir);
	}
}
