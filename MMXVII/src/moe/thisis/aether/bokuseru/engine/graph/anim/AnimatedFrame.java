package moe.thisis.aether.bokuseru.engine.graph.anim;

import java.util.Arrays;

import org.joml.Matrix4f;

public class AnimatedFrame {

	public static final int MAX_JOINTS = 150;

	private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

	private final Matrix4f[] localJointMatrices;

	private final Matrix4f[] jointMatrices;

	public AnimatedFrame() {
		localJointMatrices = new Matrix4f[AnimatedFrame.MAX_JOINTS];
		Arrays.fill(localJointMatrices, AnimatedFrame.IDENTITY_MATRIX);

		jointMatrices = new Matrix4f[AnimatedFrame.MAX_JOINTS];
		Arrays.fill(jointMatrices, AnimatedFrame.IDENTITY_MATRIX);
	}

	public Matrix4f[] getJointMatrices() {
		return jointMatrices;
	}

	public Matrix4f[] getLocalJointMatrices() {
		return localJointMatrices;
	}

	public void setMatrix(final int pos, final Matrix4f localJointMatrix, final Matrix4f invJointMatrix) {
		localJointMatrices[pos] = localJointMatrix;
		final Matrix4f mat = new Matrix4f(localJointMatrix);
		mat.mul(invJointMatrix);
		jointMatrices[pos] = mat;
	}
}
