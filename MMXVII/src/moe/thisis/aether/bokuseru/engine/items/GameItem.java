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

	public GameItem(Mesh mesh) {
		this();
		this.meshes = new Mesh[] { mesh };
	}

	public GameItem(Mesh[] meshes) {
		this();
		this.meshes = meshes;
	}

	public void cleanup() {
		int numMeshes = this.meshes != null ? this.meshes.length : 0;
		for (int i = 0; i < numMeshes; i++) {
			this.meshes[i].cleanUp();
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

	public void setMesh(Mesh mesh) {
		this.meshes = new Mesh[] { mesh };
	}

	public void setMeshes(Mesh[] meshes) {
		this.meshes = meshes;
	}

	public final void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}

	public final void setRotation(Quaternionf q) {
		this.rotation.set(q);
	}

	public final void setScale(float scale) {
		this.scale = scale;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setTextPos(int textPos) {
		this.textPos = textPos;
	}
}
