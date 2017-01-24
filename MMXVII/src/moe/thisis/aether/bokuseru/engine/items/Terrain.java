package moe.thisis.aether.bokuseru.engine.items;

import java.nio.ByteBuffer;

import org.joml.Vector3f;

import de.matthiasmann.twl.utils.PNGDecoder;
import moe.thisis.aether.bokuseru.engine.graph.HeightMapMesh;

public class Terrain {

	static class Box2D {

		public float x;

		public float y;

		public float width;

		public float height;

		public Box2D(final float x, final float y, final float width, final float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public boolean contains(final float x2, final float y2) {
			return (x2 >= x) && (y2 >= y) && (x2 < (x + width)) && (y2 < (y + height));
		}
	}

	private final GameItem[] gameItems;

	private final int terrainSize;

	private final int verticesPerCol;

	private final int verticesPerRow;

	private final HeightMapMesh heightMapMesh;

	/**
	 * It will hold the bounding box for each terrain block
	 */
	private final Box2D[][] boundingBoxes;

	/**
	 * A Terrain is composed by blocks, each block is a GameItem constructed
	 * from a HeightMap.
	 *
	 * @param terrainSize
	 *            The number of blocks will be terrainSize * terrainSize
	 * @param scale
	 *            The scale to be applied to each terrain block
	 * @param minY
	 *            The minimum y value, before scaling, of each terrain block
	 * @param maxY
	 *            The maximum y value, before scaling, of each terrain block
	 * @param heightMapFile
	 * @param textureFile
	 * @param textInc
	 * @throws Exception
	 */
	public Terrain(final int terrainSize, final float scale, final float minY, final float maxY,
			final String heightMapFile, final String textureFile, final int textInc) throws Exception {
		this.terrainSize = terrainSize;
		gameItems = new GameItem[terrainSize * terrainSize];

		final PNGDecoder decoder = new PNGDecoder(getClass().getResourceAsStream(heightMapFile));
		final int height = decoder.getHeight();
		final int width = decoder.getWidth();
		final ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
		decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
		buf.flip();

		// The number of vertices per column and row
		verticesPerCol = width - 1;
		verticesPerRow = height - 1;

		heightMapMesh = new HeightMapMesh(minY, maxY, buf, width, height, textureFile, textInc);
		boundingBoxes = new Box2D[terrainSize][terrainSize];
		for (int row = 0; row < terrainSize; row++) {
			for (int col = 0; col < terrainSize; col++) {
				final float xDisplacement = (col - (((float) terrainSize - 1) / 2)) * scale
						* HeightMapMesh.getXLength();
				final float zDisplacement = (row - (((float) terrainSize - 1) / 2)) * scale
						* HeightMapMesh.getZLength();

				final GameItem terrainBlock = new GameItem(heightMapMesh.getMesh());
				terrainBlock.setScale(scale);
				terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
				gameItems[(row * terrainSize) + col] = terrainBlock;

				boundingBoxes[row][col] = getBoundingBox(terrainBlock);
			}
		}
	}

	/**
	 * Gets the bounding box of a terrain block
	 *
	 * @param terrainBlock
	 *            A GameItem instance that defines the terrain block
	 * @return The boundingg box of the terrain block
	 */
	private Box2D getBoundingBox(final GameItem terrainBlock) {
		final float scale = terrainBlock.getScale();
		final Vector3f position = terrainBlock.getPosition();

		final float topLeftX = (HeightMapMesh.STARTX * scale) + position.x;
		final float topLeftZ = (HeightMapMesh.STARTZ * scale) + position.z;
		final float width = Math.abs(HeightMapMesh.STARTX * 2) * scale;
		final float height = Math.abs(HeightMapMesh.STARTZ * 2) * scale;
		final Box2D boundingBox = new Box2D(topLeftX, topLeftZ, width, height);
		return boundingBox;
	}

	protected float getDiagonalZCoord(final float x1, final float z1, final float x2, final float z2, final float x) {
		final float z = (((z1 - z2) / (x1 - x2)) * (x - x1)) + z1;
		return z;
	}

	public GameItem[] getGameItems() {
		return gameItems;
	}

	public float getHeight(final Vector3f position) {
		float result = Float.MIN_VALUE;
		// For each terrain block we get the bounding box, translate it to view
		// coodinates
		// and check if the position is contained in that bounding box
		Box2D boundingBox = null;
		boolean found = false;
		GameItem terrainBlock = null;
		for (int row = 0; (row < terrainSize) && !found; row++) {
			for (int col = 0; (col < terrainSize) && !found; col++) {
				terrainBlock = gameItems[(row * terrainSize) + col];
				boundingBox = boundingBoxes[row][col];
				found = boundingBox.contains(position.x, position.z);
			}
		}

		// If we have found a terrain block that contains the position we need
		// to calculate the height of the terrain on that position
		if (found) {
			final Vector3f[] triangle = getTriangle(position, boundingBox, terrainBlock);
			result = interpolateHeight(triangle[0], triangle[1], triangle[2], position.x, position.z);
		}

		return result;
	}

	protected Vector3f[] getTriangle(final Vector3f position, final Box2D boundingBox, final GameItem terrainBlock) {
		// Get the column and row of the heightmap associated to the current
		// position
		final float cellWidth = boundingBox.width / verticesPerCol;
		final float cellHeight = boundingBox.height / verticesPerRow;
		final int col = (int) ((position.x - boundingBox.x) / cellWidth);
		final int row = (int) ((position.z - boundingBox.y) / cellHeight);

		final Vector3f[] triangle = new Vector3f[3];
		triangle[1] = new Vector3f(boundingBox.x + (col * cellWidth), getWorldHeight(row + 1, col, terrainBlock),
				boundingBox.y + ((row + 1) * cellHeight));
		triangle[2] = new Vector3f(boundingBox.x + ((col + 1) * cellWidth), getWorldHeight(row, col + 1, terrainBlock),
				boundingBox.y + (row * cellHeight));
		if (position.z < getDiagonalZCoord(triangle[1].x, triangle[1].z, triangle[2].x, triangle[2].z, position.x)) {
			triangle[0] = new Vector3f(boundingBox.x + (col * cellWidth), getWorldHeight(row, col, terrainBlock),
					boundingBox.y + (row * cellHeight));
		} else {
			triangle[0] = new Vector3f(boundingBox.x + ((col + 1) * cellWidth),
					getWorldHeight(row + 2, col + 1, terrainBlock), boundingBox.y + ((row + 1) * cellHeight));
		}

		return triangle;
	}

	protected float getWorldHeight(final int row, final int col, final GameItem gameItem) {
		final float y = heightMapMesh.getHeight(row, col);
		return (y * gameItem.getScale()) + gameItem.getPosition().y;
	}

	protected float interpolateHeight(final Vector3f pA, final Vector3f pB, final Vector3f pC, final float x,
			final float z) {
		// Plane equation ax+by+cz+d=0
		final float a = ((pB.y - pA.y) * (pC.z - pA.z)) - ((pC.y - pA.y) * (pB.z - pA.z));
		final float b = ((pB.z - pA.z) * (pC.x - pA.x)) - ((pC.z - pA.z) * (pB.x - pA.x));
		final float c = ((pB.x - pA.x) * (pC.y - pA.y)) - ((pC.x - pA.x) * (pB.y - pA.y));
		final float d = -((a * pA.x) + (b * pA.y) + (c * pA.z));
		// y = (-d -ax -cz) / b
		final float y = (-d - (a * x) - (c * z)) / b;
		return y;
	}
}
