package moe.thisis.aether.bokuseru.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.lwjgl.BufferUtils;

public class Utils {

	public static boolean existsResourceFile(final String fileName) {
		boolean result;
		try (InputStream is = Utils.class.getResourceAsStream(fileName)) {
			result = is != null;
		} catch (final Exception excp) {
			result = false;
		}
		return result;
	}

	public static ByteBuffer ioResourceToByteBuffer(final String resource, final int bufferSize) throws IOException {
		ByteBuffer buffer;

		final Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
				while (fc.read(buffer) != -1) {
					;
				}
			}
		} else {
			try (InputStream source = Utils.class.getResourceAsStream(resource);
					ReadableByteChannel rbc = Channels.newChannel(source)) {
				buffer = BufferUtils.createByteBuffer(bufferSize);

				while (true) {
					final int bytes = rbc.read(buffer);
					if (bytes == -1) {
						break;
					}
					if (buffer.remaining() == 0) {
						buffer = Utils.resizeBuffer(buffer, buffer.capacity() * 2);
					}
				}
			}
		}

		buffer.flip();
		return buffer;
	}

	public static int[] listIntToArray(final List<Integer> list) {
		final int[] result = list.stream().mapToInt((final Integer v) -> v).toArray();
		return result;
	}

	public static float[] listToArray(final List<Float> list) {
		final int size = list != null ? list.size() : 0;
		final float[] floatArr = new float[size];
		for (int i = 0; i < size; i++) {
			floatArr[i] = list.get(i);
		}
		return floatArr;
	}

	public static String loadResource(final String fileName) throws Exception {
		String result;
		try (InputStream in = Utils.class.getClass().getResourceAsStream(fileName);
				Scanner scanner = new Scanner(in, "UTF-8")) {
			result = scanner.useDelimiter("\\A").next();
		}
		return result;
	}

	public static List<String> readAllLines(final String fileName) throws Exception {
		final List<String> list = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(Utils.class.getClass().getResourceAsStream(fileName)))) {
			String line;
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		}
		return list;
	}

	private static ByteBuffer resizeBuffer(final ByteBuffer buffer, final int newCapacity) {
		final ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

}
