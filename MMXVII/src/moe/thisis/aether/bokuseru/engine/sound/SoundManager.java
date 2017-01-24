package moe.thisis.aether.bokuseru.engine.sound;

import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

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

	public void addSoundBuffer(SoundBuffer soundBuffer) {
		this.soundBufferList.add(soundBuffer);
	}

	public void addSoundSource(String name, SoundSource soundSource) {
		this.soundSourceMap.put(name, soundSource);
	}

	public void cleanup() {
		for (SoundBuffer soundBuffer : soundBufferList) {
			soundBuffer.cleanup();
		}
		soundBufferList.clear();
		for (SoundSource soundSource : soundSourceMap.values()) {
			soundSource.cleanup();
		}
		soundSourceMap.clear();
		if (context != NULL) {
			alcDestroyContext(context);
		}
		if (device != NULL) {
			alcCloseDevice(device);
		}
	}

	public SoundListener getListener() {
		return this.listener;
	}

	public SoundSource getSoundSource(String name) {
		return this.soundSourceMap.get(name);
	}

	public void init() throws Exception {
		this.device = alcOpenDevice((ByteBuffer) null);
		if (device == NULL) {
			throw new IllegalStateException("Failed to open the default OpenAL device.");
		}
		ALCCapabilities deviceCaps = ALC.createCapabilities(device);
		this.context = alcCreateContext(device, (IntBuffer) null);
		if (context == NULL) {
			throw new IllegalStateException("Failed to create OpenAL context.");
		}
		alcMakeContextCurrent(context);
		AL.createCapabilities(deviceCaps);
	}

	public void playSoundSource(String name) {
		SoundSource soundSource = this.soundSourceMap.get(name);
		if (soundSource != null && !soundSource.isPlaying()) {
			soundSource.play();
		}
	}

	public void removeSoundSource(String name) {
		this.soundSourceMap.remove(name);
	}

	public void setAttenuationModel(int model) {
		alDistanceModel(model);
	}

	public void setListener(SoundListener listener) {
		this.listener = listener;
	}

	public void updateListenerPosition(Camera camera) {
		Matrix4f cameraMatrix = camera.getViewMatrix();
		listener.setPosition(camera.getPosition());
		Vector3f at = new Vector3f();
		cameraMatrix.positiveZ(at).negate();
		Vector3f up = new Vector3f();
		cameraMatrix.positiveY(up);
		listener.setOrientation(at, up);
	}
}
