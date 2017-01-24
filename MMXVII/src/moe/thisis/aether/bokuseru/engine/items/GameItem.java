package moe.thisis.aether.bokuseru.engine.items;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import moe.thisis.aether.bokuseru.engine.graph.Mesh;

public class GameItem {

	private boolean selected;

	private Mesh[] meshes;

	private final Vector3f position;

	private float scale;

	private final Quaternionf rotation;

	private int textPos;

	public GameItem() {
		selected = false;
		position = new Vector3f(0, 0, 0);
		scale = 1;
		rotation = new Quaternionf();
		textPos = 0;
	}

	public GameItem(final Mesh mesh) {
		this();
		meshes = new Mesh[] { mesh };
	}

	public GameItem(final Mesh[] meshes) {
		this();
		this.meshes = meshes;
	}

	public void cleanup() {
		final int numMeshes = meshes != null ? meshes.length : 0;
		for (int i = 0; i < numMeshes; i++) {
			meshes[i].cleanUp();
		}
	}

	public Mesh getMesh() {
		return meshes[0];
	}

	public Mesh[] getMeshes() {
		return meshes;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Quaternionf getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	public int getTextPos() {
		return textPos;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setMesh(final Mesh mesh) {
		meshes = new Mesh[] { mesh };
	}

	public void setMeshes(final Mesh[] meshes) {
		this.meshes = meshes;
	}

	public final void setPosition(final float x, final float y, final float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	public final void setRotation(final Quaternionf q) {
		rotation.set(q);
	}

	public final void setScale(final float scale) {
		this.scale = scale;
	}

	public void setSelected(final boolean selected) {
		this.selected = selected;
	}

	public void setTextPos(final int textPos) {
		this.textPos = textPos;
	}
}
