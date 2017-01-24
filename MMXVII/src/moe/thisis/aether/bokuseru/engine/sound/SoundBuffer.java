package moe.thisis.aether.bokuseru.engine.sound;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

import moe.thisis.aether.bokuseru.engine.Utils;

public class SoundBuffer {

	private final int bufferId;

	public SoundBuffer(final String file) throws Exception {
		bufferId = AL10.alGenBuffers();
		try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
			final ShortBuffer pcm = readVorbis(file, 32 * 1024, info);

			// Copy to buffer
			AL10.alBufferData(bufferId, info.channels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, pcm,
					info.sample_rate());
		}
	}

	public void cleanup() {
		AL10.alDeleteBuffers(bufferId);
	}

	public int getBufferId() {
		return bufferId;
	}

	private ShortBuffer readVorbis(final String resource, final int bufferSize, final STBVorbisInfo info)
			throws Exception {
		ByteBuffer vorbis;
		vorbis = Utils.ioResourceToByteBuffer(resource, bufferSize);

		final IntBuffer error = BufferUtils.createIntBuffer(1);
		final long decoder = STBVorbis.stb_vorbis_open_memory(vorbis, error, null);
		if (decoder == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
		}

		STBVorbis.stb_vorbis_get_info(decoder, info);

		final int channels = info.channels();

		final int lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);

		final ShortBuffer pcm = BufferUtils.createShortBuffer(lengthSamples);

		pcm.limit(STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
		STBVorbis.stb_vorbis_close(decoder);

		return pcm;
	}
}
