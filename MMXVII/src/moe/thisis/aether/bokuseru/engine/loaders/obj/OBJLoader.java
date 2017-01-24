package moe.thisis.aether.bokuseru.engine.loaders.obj;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import moe.thisis.aether.bokuseru.engine.Utils;
import moe.thisis.aether.bokuseru.engine.graph.InstancedMesh;
import moe.thisis.aether.bokuseru.engine.graph.Mesh;

public class OBJLoader {

	protected static class Face {

		/**
		 * List of idxGroup groups for a face triangle (3 vertices per face).
		 */
		private IdxGroup[] idxGroups = new IdxGroup[3];

		public Face(final String v1, final String v2, final String v3) {
			idxGroups = new IdxGroup[3];
			// Parse the lines
			idxGroups[0] = parseLine(v1);
			idxGroups[1] = parseLine(v2);
			idxGroups[2] = parseLine(v3);
		}

		public IdxGroup[] getFaceVertexIndices() {
			return idxGroups;
		}

		private IdxGroup parseLine(final String line) {
			final IdxGroup idxGroup = new IdxGroup();

			final String[] lineTokens = line.split("/");
			final int length = lineTokens.length;
			idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
			if (length > 1) {
				// It can be empty if the obj does not define text coords
				final String textCoord = lineTokens[1];
				idxGroup.idxTextCoord = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE;
				if (length > 2) {
					idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
				}
			}

			return idxGroup;
		}
	}

	protected static class IdxGroup {

		public static final int NO_VALUE = -1;

		public int idxPos;

		public int idxTextCoord;

		public int idxVecNormal;

		public IdxGroup() {
			idxPos = IdxGroup.NO_VALUE;
			idxTextCoord = IdxGroup.NO_VALUE;
			idxVecNormal = IdxGroup.NO_VALUE;
		}
	}

	public static Mesh loadMesh(final String fileName) throws Exception {
		return OBJLoader.loadMesh(fileName, 1);
	}

	public static Mesh loadMesh(final String fileName, final int instances) throws Exception {
		final List<String> lines = Utils.readAllLines(fileName);

		final List<Vector3f> vertices = new ArrayList<>();
		final List<Vector2f> textures = new ArrayList<>();
		final List<Vector3f> normals = new ArrayList<>();
		final List<Face> faces = new ArrayList<>();

		for (final String line : lines) {
			final String[] tokens = line.split("\\s+");
			switch (tokens[0]) {
			case "v":
				// Geometric vertex
				final Vector3f vec3f = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]),
						Float.parseFloat(tokens[3]));
				vertices.add(vec3f);
				break;
			case "vt":
				// Texture coordinate
				final Vector2f vec2f = new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
				textures.add(vec2f);
				break;
			case "vn":
				// Vertex normal
				final Vector3f vec3fNorm = new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]),
						Float.parseFloat(tokens[3]));
				normals.add(vec3fNorm);
				break;
			case "f":
				final Face face = new Face(tokens[1], tokens[2], tokens[3]);
				faces.add(face);
				break;
			default:
				// Ignore other lines
				break;
			}
		}
		return OBJLoader.reorderLists(vertices, textures, normals, faces, instances);
	}

	private static void processFaceVertex(final IdxGroup indices, final List<Vector2f> textCoordList,
			final List<Vector3f> normList, final List<Integer> indicesList, final float[] texCoordArr,
			final float[] normArr) {

		// Set index for vertex coordinates
		final int posIndex = indices.idxPos;
		indicesList.add(posIndex);

		// Reorder texture coordinates
		if (indices.idxTextCoord >= 0) {
			final Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
			texCoordArr[posIndex * 2] = textCoord.x;
			texCoordArr[(posIndex * 2) + 1] = 1 - textCoord.y;
		}
		if (indices.idxVecNormal >= 0) {
			// Reorder vectornormals
			final Vector3f vecNorm = normList.get(indices.idxVecNormal);
			normArr[posIndex * 3] = vecNorm.x;
			normArr[(posIndex * 3) + 1] = vecNorm.y;
			normArr[(posIndex * 3) + 2] = vecNorm.z;
		}
	}

	private static Mesh reorderLists(final List<Vector3f> posList, final List<Vector2f> textCoordList,
			final List<Vector3f> normList, final List<Face> facesList, final int instances) {

		final List<Integer> indices = new ArrayList();
		// Create position array in the order it has been declared
		final float[] posArr = new float[posList.size() * 3];
		int i = 0;
		for (final Vector3f pos : posList) {
			posArr[i * 3] = pos.x;
			posArr[(i * 3) + 1] = pos.y;
			posArr[(i * 3) + 2] = pos.z;
			i++;
		}
		final float[] textCoordArr = new float[posList.size() * 2];
		final float[] normArr = new float[posList.size() * 3];

		for (final Face face : facesList) {
			final IdxGroup[] faceVertexIndices = face.getFaceVertexIndices();
			for (final IdxGroup indValue : faceVertexIndices) {
				OBJLoader.processFaceVertex(indValue, textCoordList, normList, indices, textCoordArr, normArr);
			}
		}
		final int[] indicesArr = Utils.listIntToArray(indices);
		Mesh mesh;
		if (instances > 1) {
			mesh = new InstancedMesh(posArr, textCoordArr, normArr, indicesArr, instances);
		} else {
			mesh = new Mesh(posArr, textCoordArr, normArr, indicesArr);
		}
		return mesh;
	}
}
