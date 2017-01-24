package moe.thisis.aether.bokuseru.engine.graph.particles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joml.Vector3f;

import moe.thisis.aether.bokuseru.engine.items.GameItem;

public class FlowParticleEmitter implements IParticleEmitter {

	private int maxParticles;

	private boolean active;

	private final List<GameItem> particles;

	private final Particle baseParticle;

	private long creationPeriodMillis;

	private long lastCreationTime;

	private float speedRndRange;

	private float positionRndRange;

	private float scaleRndRange;

	private long animRange;

	public FlowParticleEmitter(final Particle baseParticle, final int maxParticles, final long creationPeriodMillis) {
		particles = new ArrayList<>();
		this.baseParticle = baseParticle;
		this.maxParticles = maxParticles;
		active = false;
		lastCreationTime = 0;
		this.creationPeriodMillis = creationPeriodMillis;
	}

	@Override
	public void cleanup() {
		for (final GameItem particle : getParticles()) {
			particle.cleanup();
		}
	}

	private void createParticle() {
		final Particle particle = new Particle(getBaseParticle());
		// Add a little bit of randomness of the parrticle
		final float sign = Math.random() > 0.5d ? -1.0f : 1.0f;
		final float speedInc = sign * (float) Math.random() * speedRndRange;
		final float posInc = sign * (float) Math.random() * positionRndRange;
		final float scaleInc = sign * (float) Math.random() * scaleRndRange;
		final long updateAnimInc = (long) sign * (long) (Math.random() * animRange);
		particle.getPosition().add(posInc, posInc, posInc);
		particle.getSpeed().add(speedInc, speedInc, speedInc);
		particle.setScale(particle.getScale() + scaleInc);
		particle.setUpdateTextureMills(particle.getUpdateTextureMillis() + updateAnimInc);
		particles.add(particle);
	}

	@Override
	public Particle getBaseParticle() {
		return baseParticle;
	}

	public long getCreationPeriodMillis() {
		return creationPeriodMillis;
	}

	public int getMaxParticles() {
		return maxParticles;
	}

	@Override
	public List<GameItem> getParticles() {
		return particles;
	}

	public float getPositionRndRange() {
		return positionRndRange;
	}

	public float getScaleRndRange() {
		return scaleRndRange;
	}

	public float getSpeedRndRange() {
		return speedRndRange;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public void setAnimRange(final long animRange) {
		this.animRange = animRange;
	}

	public void setCreationPeriodMillis(final long creationPeriodMillis) {
		this.creationPeriodMillis = creationPeriodMillis;
	}

	public void setMaxParticles(final int maxParticles) {
		this.maxParticles = maxParticles;
	}

	public void setPositionRndRange(final float positionRndRange) {
		this.positionRndRange = positionRndRange;
	}

	public void setScaleRndRange(final float scaleRndRange) {
		this.scaleRndRange = scaleRndRange;
	}

	public void setSpeedRndRange(final float speedRndRange) {
		this.speedRndRange = speedRndRange;
	}

	public void update(final long elapsedTime) {
		final long now = System.currentTimeMillis();
		if (lastCreationTime == 0) {
			lastCreationTime = now;
		}
		final Iterator<? extends GameItem> it = particles.iterator();
		while (it.hasNext()) {
			final Particle particle = (Particle) it.next();
			if (particle.updateTtl(elapsedTime) < 0) {
				it.remove();
			} else {
				updatePosition(particle, elapsedTime);
			}
		}

		final int length = getParticles().size();
		if (((now - lastCreationTime) >= creationPeriodMillis) && (length < maxParticles)) {
			createParticle();
			lastCreationTime = now;
		}
	}

	/**
	 * Updates a particle position
	 *
	 * @param particle
	 *            The particle to update
	 * @param elapsedTime
	 *            Elapsed time in milliseconds
	 */
	public void updatePosition(final Particle particle, final long elapsedTime) {
		final Vector3f speed = particle.getSpeed();
		final float delta = elapsedTime / 1000.0f;
		final float dx = speed.x * delta;
		final float dy = speed.y * delta;
		final float dz = speed.z * delta;
		final Vector3f pos = particle.getPosition();
		particle.setPosition(pos.x + dx, pos.y + dy, pos.z + dz);
	}
}
