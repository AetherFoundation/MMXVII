package moe.thisis.aether.bokuseru.engine.sound;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

public class SoundListener {

	public SoundListener() {
		this(new Vector3f(0, 0, 0));
	}

	public SoundListener(final Vector3f position) {
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}

	public void setOrientation(final Vector3f at, final Vector3f up) {
		final float[] data = new float[6];
		data[0] = at.x;
		data[1] = at.y;
		data[2] = at.z;
		data[3] = up.x;
		data[4] = up.y;
		data[5] = up.z;
		AL10.alListenerfv(AL10.AL_ORIENTATION, data);
	}

	public void setPosition(final Vector3f position) {
		AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
	}

	public void setSpeed(final Vector3f speed) {
		AL10.alListener3f(AL10.AL_VELOCITY, speed.x, speed.y, speed.z);
	}
}
