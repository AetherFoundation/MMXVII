package moe.thisis.aether.bokuseru.engine.graph;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import moe.thisis.aether.bokuseru.engine.items.GameItem;

public class Mesh {

	public static final int MAX_WEIGHTS = 4;

	/** Create empty float array
	 * @param length	Array length
	 * @param defaultValue	Default array value
	 * @return	Filled array
	 */
	protected static float[] createEmptyFloatArray(final int length, final float defaultValue) {
		final float[] result = new float[length];
		Arrays.fill(result, defaultValue);
		return result;
	}

	/** Create empty int array
	 * @param length	Array length
	 * @param defaultValue	Default array value
	 * @return	Filled array
	 */
	protected static int[] createEmptyIntArray(final int length, final int defaultValue) {
		final int[] result = new int[length];
		Arrays.fill(result, defaultValue);
		return result;
	}

	protected final int vaoId;

	protected final List<Integer> vboIdList;

	private final int vertexCount;

	private Material material;

	/** Mesh constructor
	 * @param positions	Mesh positions
	 * @param textCoords	Text coordinates
	 * @param normals	Normal map
	 * @param indices	Indices
	 */
	public Mesh(final float[] positions, final float[] textCoords, final float[] normals, final int[] indices) {
		this(positions, textCoords, normals, indices,
				Mesh.createEmptyIntArray((Mesh.MAX_WEIGHTS * positions.length) / 3, 0),
				Mesh.createEmptyFloatArray((Mesh.MAX_WEIGHTS * positions.length) / 3, 0));
	}

	/** Mesh constructor
	 * @param positions	Mesh positions
	 * @param textCoords	Text coordinates
	 * @param normals	Normal map
	 * @param indices	Indices
	 * @param jointIndices	Joint indices
	 * @param weights	Weights
	 */
	public Mesh(final float[] positions, final float[] textCoords, final float[] normals, final int[] indices,
			final int[] jointIndices, final float[] weights) {
		vertexCount = indices.length;
		vboIdList = new ArrayList();

		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		// Position VBO
		int vboId = GL15.glGenBuffers();
		vboIdList.add(vboId);
		final FloatBuffer posBuffer = BufferUtils.createFloatBuffer(positions.length);
		posBuffer.put(positions).flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, posBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

		// Texture coordinates VBO
		vboId = GL15.glGenBuffers();
		vboIdList.add(vboId);
		final FloatBuffer textCoordsBuffer = BufferUtils.createFloatBuffer(textCoords.length);
		textCoordsBuffer.put(textCoords).flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textCoordsBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

		// Vertex normals VBO
		vboId = GL15.glGenBuffers();
		vboIdList.add(vboId);
		final FloatBuffer vecNormalsBuffer = BufferUtils.createFloatBuffer(normals.length);
		vecNormalsBuffer.put(normals).flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vecNormalsBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);

		// Weights
		vboId = GL15.glGenBuffers();
		vboIdList.add(vboId);
		final FloatBuffer weightsBuffer = BufferUtils.createFloatBuffer(weights.length);
		weightsBuffer.put(weights).flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, weightsBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, 0, 0);

		// Joint indices
		vboId = GL15.glGenBuffers();
		vboIdList.add(vboId);
		final IntBuffer jointIndicesBuffer = BufferUtils.createIntBuffer(jointIndices.length);
		jointIndicesBuffer.put(jointIndices).flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, jointIndicesBuffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, 0, 0);

		// Index VBO
		vboId = GL15.glGenBuffers();
		vboIdList.add(vboId);
		final IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
		indicesBuffer.put(indices).flip();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	/** Clean up a mesh
	 * 
	 */
	public void cleanUp() {
		GL20.glDisableVertexAttribArray(0);

		// Delete the VBOs
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		for (final int vboId : vboIdList) {
			GL15.glDeleteBuffers(vboId);
		}

		// Delete the texture
		final Texture texture = material.getTexture();
		if (texture != null) {
			texture.cleanup();
		}

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
	}

	/** Delete buffers
	 * 
	 */
	public void deleteBuffers() {
		GL20.glDisableVertexAttribArray(0);

		// Delete the VBOs
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		for (final int vboId : vboIdList) {
			GL15.glDeleteBuffers(vboId);
		}

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
	}

	/** End rendering
	 * 
	 */
	protected void endRender() {
		// Restore state
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL30.glBindVertexArray(0);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	/** Get material
	 * @return	Material
	 */
	public Material getMaterial() {
		return material;
	}

	/** Get VAO ID
	 * @return	VAO ID
	 */
	public final int getVaoId() {
		return vaoId;
	}

	/** Get vertex count
	 * @return	Vertex count
	 */
	public int getVertexCount() {
		return vertexCount;
	}

	/** Initialize rendering
	 * 
	 */
	protected void initRender() {
		final Texture texture = material.getTexture();
		if (texture != null) {
			// Activate first texture bank
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			// Bind the texture
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
		}
		final Texture normalMap = material.getNormalMap();
		if (normalMap != null) {
			// Activate first texture bank
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			// Bind the texture
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMap.getId());
		}

		// Draw the mesh
		GL30.glBindVertexArray(getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
	}

	/** Render mesh
	 * 
	 */
	public void render() {
		initRender();

		GL11.glDrawElements(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

		endRender();
	}

	/** Render list
	 * @param gameItems Game items
	 * @param consumer	Consumer
	 */
	public void renderList(final List<GameItem> gameItems, final Consumer<GameItem> consumer) {
		initRender();

		for (final GameItem gameItem : gameItems) {
			// Set up data requiered by gameItem
			consumer.accept(gameItem);
			// Render this game item
			GL11.glDrawElements(GL11.GL_TRIANGLES, getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}

		endRender();
	}

	/** Set mesh material
	 * @param material	Material
	 */
	public void setMaterial(final Material material) {
		this.material = material;
	}
}
