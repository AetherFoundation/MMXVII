package moe.thisis.aether.bokuseru.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moe.thisis.aether.bokuseru.engine.graph.InstancedMesh;
import moe.thisis.aether.bokuseru.engine.graph.Mesh;
import moe.thisis.aether.bokuseru.engine.graph.weather.Fog;
import moe.thisis.aether.bokuseru.engine.items.GameItem;

public class Scene {

	private final Map<Mesh, List<GameItem>> meshMap;

	private final Map<InstancedMesh, List<GameItem>> instancedMeshMap;

	private SceneLight sceneLight;

	private Fog fog;

	private boolean renderShadows;

	/** Scene Constructor
	 * 
	 */
	public Scene() {
		meshMap = new HashMap();
		instancedMeshMap = new HashMap();
		fog = Fog.NOFOG;
		renderShadows = true;
	}

	/** Cleanup the scene by removing all contents
	 * 
	 */
	public void cleanup() {
		for (final Mesh mesh : meshMap.keySet()) {
			mesh.cleanUp();
		}
		for (final Mesh mesh : instancedMeshMap.keySet()) {
			mesh.cleanUp();
		}
	}

	/**
	 * @return 	Fog
	 */
	public Fog getFog() {
		return fog;
	}

	/**
	 * @return	Current instanced mesh map
	 */
	public Map<InstancedMesh, List<GameItem>> getGameInstancedMeshes() {
		return instancedMeshMap;
	}

	/**
	 * @return	Current mesh map
	 */
	public Map<Mesh, List<GameItem>> getGameMeshes() {
		return meshMap;
	}

	/**
	 * @return	Current scene light
	 */
	public SceneLight getSceneLight() {
		return sceneLight;
	}

	/**
	 * @return	Status of render shadows
	 */
	public boolean isRenderShadows() {
		return renderShadows;
	}

	/**
	 * @param fog
	 *            Fog to set
	 */
	public void setFog(final Fog fog) {
		this.fog = fog;
	}

	/** Set game items inside the scene
	 * @param gameItems	Array of game items to add
	 */
	public void setGameItems(final GameItem[] gameItems) {
		// Create a map of meshes to speed up rendering
		final int numGameItems = gameItems != null ? gameItems.length : 0;
		for (int i = 0; i < numGameItems; i++) {
			final GameItem gameItem = gameItems[i];
			final Mesh[] meshes = gameItem.getMeshes();
			for (final Mesh mesh : meshes) {
				final boolean instancedMesh = mesh instanceof InstancedMesh;
				List<GameItem> list = instancedMesh ? instancedMeshMap.get(mesh) : meshMap.get(mesh);
				if (list == null) {
					list = new ArrayList<>();
					if (instancedMesh) {
						instancedMeshMap.put((InstancedMesh) mesh, list);
					} else {
						meshMap.put(mesh, list);
					}
				}
				list.add(gameItem);
			}
		}
	}

	/**
	 * @param renderShadows	Enable or disable render shadows
	 */
	public void setRenderShadows(final boolean renderShadows) {
		this.renderShadows = renderShadows;
	}

	/**
	 * @param sceneLight	Scene light to use
	 */
	public void setSceneLight(final SceneLight sceneLight) {
		this.sceneLight = sceneLight;
	}

}
