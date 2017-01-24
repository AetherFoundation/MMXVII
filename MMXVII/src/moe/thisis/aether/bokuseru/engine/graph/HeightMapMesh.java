package moe.thisis.aether.bokuseru.engine.graph;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import moe.thisis.aether.bokuseru.engine.Utils;

public class HeightMapMesh {

	private static final int MAX_COLOUR = 255 * 255 * 255;

	public static final float STARTX = -0.5f;

	public static final float STARTZ = -0.5f;

	/** Get RGBA data at a point
	 * @param x	X coordinate
	 * @param z	Z coordinate
	 * @param width	Width
	 * @param buffer	Input buffer
	 * @return	RGBA data
	 */
	public static int getRGB(final int x, final int z, final int width, final ByteBuffer buffer) {
		final byte r = buffer.get((x * 4) + 0 + (z * 4 * width));
		final byte g = buffer.get((x * 4) + 1 + (z * 4 * width));
		final byte b = buffer.get((x * 4) + 2 + (z * 4 * width));
		final byte a = buffer.get((x * 4) + 3 + (z * 4 * width));
		final int argb = ((0xFF & a) << 24) | ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);
		return argb;
	}

	/**
	 * @return	X length
	 */
	public static float getXLength() {
		return Math.abs(-HeightMapMesh.STARTX * 2);
	}

	/**
	 * @return	Z length
	 */
	public static float getZLength() {
		return Math.abs(-HeightMapMesh.STARTZ * 2);
	}

	private final float minY;

	private final float maxY;

	private final Mesh mesh;

	private final float[][] heightArray;

	/** Height Map Mesh Constructor
	 * @param minY	Minimum Y value
	 * @param maxY	Maximum Y value
	 * @param heightMapImage	Height map image
	 * @param width	Height map width
	 * @param height	Height map height
	 * @param textureFile	Terrain texture file
	 * @param textInc
	 * @throws Exception
	 */
	public HeightMapMesh(final float minY, final float maxY, final ByteBuffer heightMapImage, final int width,
			final int height, final String textureFile, final int textInc) throws Exception {
		this.minY = minY;
		this.maxY = maxY;

		heightArray = new float[height][width];

		final Texture texture = new Texture(textureFile);

		final float incx = HeightMapMesh.getXLength() / (width - 1);
		final float incz = HeightMapMesh.getZLength() / (height - 1);

		final List<Float> positions = new ArrayList();
		final List<Float> textCoords = new ArrayList();
		final List<Integer> indices = new ArrayList();

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				// Create vertex for current position
				positions.add(HeightMapMesh.STARTX + (col * incx)); // x
				final float currentHeight = getHeight(col, row, width, heightMapImage);
				heightArray[row][col] = currentHeight;
				positions.add(currentHeight); // y
				positions.add(HeightMapMesh.STARTZ + (row * incz)); // z

				// Set texture coordinates
				textCoords.add(((float) textInc * (float) col) / width);
				textCoords.add(((float) textInc * (float) row) / height);

				// Create indices
				if ((col < (width - 1)) && (row < (height - 1))) {
					final int leftTop = (row * width) + col;
					final int leftBottom = ((row + 1) * width) + col;
					final int rightBottom = ((row + 1) * width) + col + 1;
					final int rightTop = (row * width) + col + 1;

					indices.add(leftTop);
					indices.add(leftBottom);
					indices.add(rightTop);

					indices.add(rightTop);
					indices.add(leftBottom);
					indices.add(rightBottom);
				}
			}
		}
		final float[] posArr = Utils.listToArray(positions);
		final int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
		final float[] textCoordsArr = Utils.listToArray(textCoords);
		final float[] normalsArr = calcNormals(posArr, width, height);
		mesh = new Mesh(posArr, textCoordsArr, normalsArr, indicesArr);
		final Material material = new Material(texture, 0.0f);
		mesh.setMaterial(material);
	}

	/** Calculate normals
	 * @param posArr	Position array
	 * @param width	Width
	 * @param height	Height
	 * @return	Normals
	 */
	private float[] calcNormals(final float[] posArr, final int width, final int height) {
		final Vector3f v0 = new Vector3f();
		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		Vector3f v3 = new Vector3f();
		Vector3f v4 = new Vector3f();
		final Vector3f v12 = new Vector3f();
		final Vector3f v23 = new Vector3f();
		final Vector3f v34 = new Vector3f();
		final Vector3f v41 = new Vector3f();
		final List<Float> normals = new ArrayList<>();
		Vector3f normal = new Vector3f();
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if ((row > 0) && (row < (height - 1)) && (col > 0) && (col < (width - 1))) {
					final int i0 = (row * width * 3) + (col * 3);
					v0.x = posArr[i0];
					v0.y = posArr[i0 + 1];
					v0.z = posArr[i0 + 2];

					final int i1 = (row * width * 3) + ((col - 1) * 3);
					v1.x = posArr[i1];
					v1.y = posArr[i1 + 1];
					v1.z = posArr[i1 + 2];
					v1 = v1.sub(v0);

					final int i2 = ((row + 1) * width * 3) + (col * 3);
					v2.x = posArr[i2];
					v2.y = posArr[i2 + 1];
					v2.z = posArr[i2 + 2];
					v2 = v2.sub(v0);

					final int i3 = ((row) * width * 3) + ((col + 1) * 3);
					v3.x = posArr[i3];
					v3.y = posArr[i3 + 1];
					v3.z = posArr[i3 + 2];
					v3 = v3.sub(v0);

					final int i4 = ((row - 1) * width * 3) + (col * 3);
					v4.x = posArr[i4];
					v4.y = posArr[i4 + 1];
					v4.z = posArr[i4 + 2];
					v4 = v4.sub(v0);

					v1.cross(v2, v12);
					v12.normalize();

					v2.cross(v3, v23);
					v23.normalize();

					v3.cross(v4, v34);
					v34.normalize();

					v4.cross(v1, v41);
					v41.normalize();

					normal = v12.add(v23).add(v34).add(v41);
					normal.normalize();
				} else {
					normal.x = 0;
					normal.y = 1;
					normal.z = 0;
				}
				normal.normalize();
				normals.add(normal.x);
				normals.add(normal.y);
				normals.add(normal.z);
			}
		}
		return Utils.listToArray(normals);
	}

	/** Get height on the grid
	 * @param row	Row
	 * @param col	Column
	 * @return	Height
	 */
	public float getHeight(final int row, final int col) {
		float result = 0;
		if ((row >= 0) && (row < heightArray.length)) {
			if ((col >= 0) && (col < heightArray[row].length)) {
				result = heightArray[row][col];
			}
		}
		return result;
	}

	/** Get height at a point
	 * @param x	X coordinate
	 * @param z	Z coordinate
	 * @param width	Width
	 * @param buffer	Input buffer
	 * @return	Height
	 */
	private float getHeight(final int x, final int z, final int width, final ByteBuffer buffer) {
		final int argb = HeightMapMesh.getRGB(x, z, width, buffer);
		return minY + (Math.abs(maxY - minY) * ((float) argb / (float) HeightMapMesh.MAX_COLOUR));
	}

	/**
	 * @return	Height map mesh
	 */
	public Mesh getMesh() {
		return mesh;
	}

}
