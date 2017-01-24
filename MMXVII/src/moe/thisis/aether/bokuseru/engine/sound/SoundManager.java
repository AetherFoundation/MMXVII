package moe.thisis.aether.bokuseru.engine.sound;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;

import moe.thisis.aether.bokuseru.engine.graph.Camera;

public class SoundManager {

	private long device;

	private long context;

	private SoundListener listener;

	private final List<SoundBuffer> soundBufferList;

	private final Map<String, SoundSource> soundSourceMap;

	public SoundManager() {
		soundBufferList = new ArrayList<>();
		soundSourceMap = new HashMap<>();
	}

	public void addSoundBuffer(final SoundBuffer soundBuffer) {
		soundBufferList.add(soundBuffer);
	}

	public void addSoundSource(final String name, final SoundSource soundSource) {
		soundSourceMap.put(name, soundSource);
	}

	public void cleanup() {
		for (final SoundBuffer soundBuffer : soundBufferList) {
			soundBuffer.cleanup();
		}
		soundBufferList.clear();
		for (final SoundSource soundSource : soundSourceMap.values()) {
			soundSource.cleanup();
		}
		soundSourceMap.clear();
		if (context != MemoryUtil.NULL) {
			ALC10.alcDestroyContext(context);
		}
		if (device != MemoryUtil.NULL) {
			ALC10.alcCloseDevice(device);
		}
	}

	public SoundListener getListener() {
		return listener;
	}

	public SoundSource getSoundSource(final String name) {
		return soundSourceMap.get(name);
	}

	public void init() throws Exception {
		device = ALC10.alcOpenDevice((ByteBuffer) null);
		if (device == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		final ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		context = ALC10.alcCreateContext(device, (IntBuffer) null);
		if (context == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		ALC10.alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
	}

	public void playSoundSource(final String name) {
		final SoundSource soundSource = soundSourceMap.get(name);
		if ((soundSource != null) && !soundSource.isPlaying()) {
			soundSource.play();
		}
	}

	public void removeSoundSource(final String name) {
		soundSourceMap.remove(name);
	}

	public void setAttenuationModel(final int model) {
		AL10.alDistanceModel(model);
	}

	public void setListener(final SoundListener listener) {
		this.listener = listener;
	}

	public void updateListenerPosition(final Camera camera) {
		final Matrix4f cameraMatrix = camera.getViewMatrix();
		listener.setPosition(camera.getPosition());
		final Vector3f at = new Vector3f();
		cameraMatrix.positiveZ(at).negate();
		final Vector3f up = new Vector3f();
		cameraMatrix.positiveY(up);
		listener.setOrientation(at, up);
	}
}
