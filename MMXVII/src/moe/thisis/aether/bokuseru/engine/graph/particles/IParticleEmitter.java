package moe.thisis.aether.bokuseru.engine.graph.particles;

import java.util.List;

import moe.thisis.aether.bokuseru.engine.items.GameItem;

public interface IParticleEmitter {

	void cleanup();

	Particle getBaseParticle();

	List<GameItem> getParticles();
}
