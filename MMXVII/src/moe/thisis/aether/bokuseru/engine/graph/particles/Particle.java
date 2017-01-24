package moe.thisis.aether.bokuseru.engine.graph.particles;

import org.joml.Vector3f;

import moe.thisis.aether.bokuseru.engine.graph.Mesh;
import moe.thisis.aether.bokuseru.engine.graph.Texture;
import moe.thisis.aether.bokuseru.engine.items.GameItem;

public class Particle extends GameItem {

	private long updateTextureMillis;

	private long currentAnimTimeMillis;

	private Vector3f speed;

	/**
	 * Time to live for particle in milliseconds.
	 */
	private long ttl;

	private final int animFrames;

	public Particle(final Mesh mesh, final Vector3f speed, final long ttl, final long updateTextureMillis) {
		super(mesh);
		this.speed = new Vector3f(speed);
		this.ttl = ttl;
		this.updateTextureMillis = updateTextureMillis;
		currentAnimTimeMillis = 0;
		final Texture texture = getMesh().getMaterial().getTexture();
		animFrames = texture.getNumCols() * texture.getNumRows();
	}

	public Particle(final Particle baseParticle) {
		super(baseParticle.getMesh());
		final Vector3f aux = baseParticle.getPosition();
		setPosition(aux.x, aux.y, aux.z);
		setRotation(baseParticle.getRotation());
		setScale(baseParticle.getScale());
		speed = new Vector3f(baseParticle.speed);
		ttl = baseParticle.geTtl();
		updateTextureMillis = baseParticle.getUpdateTextureMillis();
		currentAnimTimeMillis = 0;
		animFrames = baseParticle.getAnimFrames();
	}

	public int getAnimFrames() {
		return animFrames;
	}

	public Vector3f getSpeed() {
		return speed;
	}

	public long geTtl() {
		return ttl;
	}

	public long getUpdateTextureMillis() {
		return updateTextureMillis;
	}

	public void setSpeed(final Vector3f speed) {
		this.speed = speed;
	}

	public void setTtl(final long ttl) {
		this.ttl = ttl;
	}

	public void setUpdateTextureMills(final long updateTextureMillis) {
		this.updateTextureMillis = updateTextureMillis;
	}

	/**
	 * Updates the Particle's TTL
	 *
	 * @param elapsedTime
	 *            Elapsed Time in milliseconds
	 * @return The Particle's TTL
	 */
	public long updateTtl(final long elapsedTime) {
		ttl -= elapsedTime;
		currentAnimTimeMillis += elapsedTime;
		if ((currentAnimTimeMillis >= getUpdateTextureMillis()) && (animFrames > 0)) {
			currentAnimTimeMillis = 0;
			int pos = getTextPos();
			pos++;
			if (pos < animFrames) {
				setTextPos(pos);
			} else {
				setTextPos(0);
			}
		}
		return ttl;
	}

}