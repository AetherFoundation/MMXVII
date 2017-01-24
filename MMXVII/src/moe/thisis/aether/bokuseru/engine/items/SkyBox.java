package moe.thisis.aether.bokuseru.engine.items;

import org.joml.Vector3f;

import moe.thisis.aether.bokuseru.engine.graph.Material;
import moe.thisis.aether.bokuseru.engine.graph.Mesh;
import moe.thisis.aether.bokuseru.engine.graph.Texture;
import moe.thisis.aether.bokuseru.engine.loaders.obj.OBJLoader;

public class SkyBox extends GameItem {

	public SkyBox(final String objModel, final String textureFile) throws Exception {
		super();
		final Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
		final Texture skyBoxtexture = new Texture(textureFile);
		skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
		setMesh(skyBoxMesh);
		setPosition(0, 0, 0);
	}

	public SkyBox(final String objModel, final Vector3f colour) throws Exception {
		super();
		final Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
		final Material material = new Material(colour, 0);
		skyBoxMesh.setMaterial(material);
		setMesh(skyBoxMesh);
		setPosition(0, 0, 0);
	}
}
