package moe.thisis.aether.bokuseru.engine.graph;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import moe.thisis.aether.bokuseru.engine.items.GameItem;

public class InstancedMesh extends Mesh {

	private static final int FLOAT_SIZE_BYTES = 4;

	private static final int VECTOR4F_SIZE_BYTES = 4 * InstancedMesh.FLOAT_SIZE_BYTES;

	private static final int MATRIX_SIZE_FLOATS = 4 * 4;

	private static final int MATRIX_SIZE_BYTES = InstancedMesh.MATRIX_SIZE_FLOATS * InstancedMesh.FLOAT_SIZE_BYTES;

	private static final int INSTANCE_SIZE_BYTES = (InstancedMesh.MATRIX_SIZE_BYTES * 2)
			+ (InstancedMesh.FLOAT_SIZE_BYTES * 2) + InstancedMesh.FLOAT_SIZE_BYTES;

	private static final int INSTANCE_SIZE_FLOATS = (InstancedMesh.MATRIX_SIZE_FLOATS * 2) + 3;

	private final int numInstances;

	private final int instanceDataVBO;

	private final FloatBuffer instanceDataBuffer;

	public InstancedMesh(final float[] positions, final float[] textCoords, final float[] normals, final int[] indices,
			final int numInstances) {
		super(positions, textCoords, normals, indices,
				Mesh.createEmptyIntArray((Mesh.MAX_WEIGHTS * positions.length) / 3, 0),
				Mesh.createEmptyFloatArray((Mesh.MAX_WEIGHTS * positions.length) / 3, 0));

		this.numInstances = numInstances;

		GL30.glBindVertexArray(vaoId);

		// Model View Matrix
		instanceDataVBO = GL15.glGenBuffers();
		vboIdList.add(instanceDataVBO);
		instanceDataBuffer = BufferUtils.createFloatBuffer(numInstances * InstancedMesh.INSTANCE_SIZE_FLOATS);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceDataVBO);
		int start = 5;
		int strideStart = 0;
		for (int i = 0; i < 4; i++) {
			GL20.glVertexAttribPointer(start, 4, GL11.GL_FLOAT, false, InstancedMesh.INSTANCE_SIZE_BYTES, strideStart);
			GL33.glVertexAttribDivisor(start, 1);
			start++;
			strideStart += InstancedMesh.VECTOR4F_SIZE_BYTES;
		}

		// Light view matrix
		for (int i = 0; i < 4; i++) {
			GL20.glVertexAttribPointer(start, 4, GL11.GL_FLOAT, false, InstancedMesh.INSTANCE_SIZE_BYTES, strideStart);
			GL33.glVertexAttribDivisor(start, 1);
			start++;
			strideStart += InstancedMesh.VECTOR4F_SIZE_BYTES;
		}

		// Texture offsets
		GL20.glVertexAttribPointer(start, 2, GL11.GL_FLOAT, false, InstancedMesh.INSTANCE_SIZE_BYTES, strideStart);
		GL33.glVertexAttribDivisor(start, 1);
		strideStart += InstancedMesh.FLOAT_SIZE_BYTES * 2;
		start++;

		// Selected
		GL20.glVertexAttribPointer(start, 1, GL11.GL_FLOAT, false, InstancedMesh.INSTANCE_SIZE_BYTES, strideStart);
		GL33.glVertexAttribDivisor(start, 1);
		start++;

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	@Override
	protected void endRender() {
		final int start = 5;
		final int numElements = (4 * 2) + 2;
		for (int i = 0; i < numElements; i++) {
			GL20.glDisableVertexAttribArray(start + i);
		}

		super.endRender();
	}

	@Override
	protected void initRender() {
		super.initRender();

		final int start = 5;
		final int numElements = (4 * 2) + 2;
		for (int i = 0; i < numElements; i++) {
			GL20.glEnableVertexAttribArray(start + i);
		}
	}

	private void renderChunkInstanced(final List<GameItem> gameItems, final boolean billBoard,
			final Transformation transformation, final Matrix4f viewMatrix, final Matrix4f lightViewMatrix) {
		instanceDataBuffer.clear();

		int i = 0;

		final Texture text = getMaterial().getTexture();
		for (final GameItem gameItem : gameItems) {
			final Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
			if (viewMatrix != null) {
				if (billBoard) {
					viewMatrix.transpose3x3(modelMatrix);
				}
				final Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
				modelViewMatrix.get(InstancedMesh.INSTANCE_SIZE_FLOATS * i, instanceDataBuffer);
			}
			if (lightViewMatrix != null) {
				final Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(modelMatrix,
						lightViewMatrix);
				modelLightViewMatrix.get((InstancedMesh.INSTANCE_SIZE_FLOATS * i) + InstancedMesh.MATRIX_SIZE_FLOATS,
						instanceDataBuffer);
			}
			if (text != null) {
				final int col = gameItem.getTextPos() % text.getNumCols();
				final int row = gameItem.getTextPos() / text.getNumCols();
				final float textXOffset = (float) col / text.getNumCols();
				final float textYOffset = (float) row / text.getNumRows();
				final int buffPos = (InstancedMesh.INSTANCE_SIZE_FLOATS * i) + (InstancedMesh.MATRIX_SIZE_FLOATS * 2);
				instanceDataBuffer.put(buffPos, textXOffset);
				instanceDataBuffer.put(buffPos + 1, textYOffset);
			}

			// Selected data
			final int buffPos = (InstancedMesh.INSTANCE_SIZE_FLOATS * i) + (InstancedMesh.MATRIX_SIZE_FLOATS * 2) + 2;
			instanceDataBuffer.put(buffPos, gameItem.isSelected() ? 1 : 0);

			i++;
		}

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instanceDataVBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, instanceDataBuffer, GL15.GL_DYNAMIC_DRAW);

		GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0, gameItems.size());

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public void renderListInstanced(final List<GameItem> gameItems, final boolean billBoard,
			final Transformation transformation, final Matrix4f viewMatrix, final Matrix4f lightViewMatrix) {
		initRender();

		final int chunkSize = numInstances;
		final int length = gameItems.size();
		for (int i = 0; i < length; i += chunkSize) {
			final int end = Math.min(length, i + chunkSize);
			final List<GameItem> subList = gameItems.subList(i, end);
			renderChunkInstanced(subList, billBoard, transformation, viewMatrix, lightViewMatrix);
		}

		endRender();
	}

	public void renderListInstanced(final List<GameItem> gameItems, final Transformation transformation,
			final Matrix4f viewMatrix, final Matrix4f lightViewMatrix) {
		renderListInstanced(gameItems, false, transformation, viewMatrix, lightViewMatrix);
	}
}
