package moe.thisis.aether.bokuseru.engine.items;

import java.util.ArrayList;
import java.util.List;

import moe.thisis.aether.bokuseru.engine.Utils;
import moe.thisis.aether.bokuseru.engine.graph.FontTexture;
import moe.thisis.aether.bokuseru.engine.graph.Material;
import moe.thisis.aether.bokuseru.engine.graph.Mesh;

public class TextItem extends GameItem {

	private static final float ZPOS = 0.0f;

	private static final int VERTICES_PER_QUAD = 4;

	private final FontTexture fontTexture;

	private String text;

	public TextItem(final String text, final FontTexture fontTexture) throws Exception {
		super();
		this.text = text;
		this.fontTexture = fontTexture;
		setMesh(buildMesh());
	}

	private Mesh buildMesh() {
		final List<Float> positions = new ArrayList();
		final List<Float> textCoords = new ArrayList();
		final float[] normals = new float[0];
		final List<Integer> indices = new ArrayList();
		final char[] characters = text.toCharArray();
		final int numChars = characters.length;

		float startx = 0;
		for (int i = 0; i < numChars; i++) {
			final FontTexture.CharInfo charInfo = fontTexture.getCharInfo(characters[i]);

			// Build a character tile composed by two triangles

			// Left Top vertex
			positions.add(startx); // x
			positions.add(0.0f); // y
			positions.add(TextItem.ZPOS); // z
			textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth());
			textCoords.add(0.0f);
			indices.add(i * TextItem.VERTICES_PER_QUAD);

			// Left Bottom vertex
			positions.add(startx); // x
			positions.add((float) fontTexture.getHeight()); // y
			positions.add(TextItem.ZPOS); // z
			textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth());
			textCoords.add(1.0f);
			indices.add((i * TextItem.VERTICES_PER_QUAD) + 1);

			// Right Bottom vertex
			positions.add(startx + charInfo.getWidth()); // x
			positions.add((float) fontTexture.getHeight()); // y
			positions.add(TextItem.ZPOS); // z
			textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) fontTexture.getWidth());
			textCoords.add(1.0f);
			indices.add((i * TextItem.VERTICES_PER_QUAD) + 2);

			// Right Top vertex
			positions.add(startx + charInfo.getWidth()); // x
			positions.add(0.0f); // y
			positions.add(TextItem.ZPOS); // z
			textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) fontTexture.getWidth());
			textCoords.add(0.0f);
			indices.add((i * TextItem.VERTICES_PER_QUAD) + 3);

			// Add indices por left top and bottom right vertices
			indices.add(i * TextItem.VERTICES_PER_QUAD);
			indices.add((i * TextItem.VERTICES_PER_QUAD) + 2);

			startx += charInfo.getWidth();
		}

		final float[] posArr = Utils.listToArray(positions);
		final float[] textCoordsArr = Utils.listToArray(textCoords);
		final int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
		final Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr);
		mesh.setMaterial(new Material(fontTexture.getTexture()));
		return mesh;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
		getMesh().deleteBuffers();
		setMesh(buildMesh());
	}
}