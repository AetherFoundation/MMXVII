package moe.thisis.aether.bokuseru.engine.items;

import org.joml.Vector3f;
import moe.thisis.aether.bokuseru.engine.items.GameItem;
import moe.thisis.aether.bokuseru.engine.graph.Material;
import moe.thisis.aether.bokuseru.engine.graph.Mesh;
import moe.thisis.aether.bokuseru.engine.loaders.obj.OBJLoader;
import moe.thisis.aether.bokuseru.engine.graph.Texture;

public class SkyBox extends GameItem {

	public SkyBox(String objModel, String textureFile) throws Exception {
		super();
		Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
		Texture skyBoxtexture = new Texture(textureFile);
		skyBoxMesh.setMaterial(new Material(skyBoxtexture, 0.0f));
		setMesh(skyBoxMesh);
		setPosition(0, 0, 0);
	}

	public SkyBox(String objModel, Vector3f colour) throws Exception {
		super();
		Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
		Material material = new Material(colour, 0);
		skyBoxMesh.setMaterial(material);
		setMesh(skyBoxMesh);
		setPosition(0, 0, 0);
	}
}
