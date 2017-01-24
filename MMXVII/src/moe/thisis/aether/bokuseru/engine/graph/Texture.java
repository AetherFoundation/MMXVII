package moe.thisis.aether.bokuseru.engine.graph;

import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Texture {

	private final int id;

	private final int width;

	private final int height;

	private int numRows = 1;

	private int numCols = 1;

	public Texture(final InputStream is) throws Exception {
		try {
			// Load Texture file
			final PNGDecoder decoder = new PNGDecoder(is);

			width = decoder.getWidth();
			height = decoder.getHeight();

			// Load texture contents into a byte buffer
			final ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();

			// Create a new OpenGL texture
			id = GL11.glGenTextures();
			// Bind the texture
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

			// Tell OpenGL how to unpack the RGBA bytes. Each component is 1
			// byte size
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			// Upload the texture data
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, buf);
			// Generate Mip Map
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

			is.close();
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * Creates an empty texture.
	 *
	 * @param width
	 *            Width of the texture
	 * @param height
	 *            Height of the texture
	 * @param pixelFormat
	 *            Specifies the format of the pixel data (GL_RGBA, etc.)
	 * @throws Exception
	 */
	public Texture(final int width, final int height, final int pixelFormat) throws Exception {
		id = GL11.glGenTextures();
		this.width = width;
		this.height = height;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, this.width, this.height, 0, pixelFormat,
				GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	}

	public Texture(final String fileName) throws Exception {
		this(Texture.class.getResourceAsStream(fileName));
	}

	public Texture(final String fileName, final int numCols, final int numRows) throws Exception {
		this(fileName);
		this.numCols = numCols;
		this.numRows = numRows;
	}

	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}

	public void cleanup() {
		GL11.glDeleteTextures(id);
	}

	public int getHeight() {
		return height;
	}

	public int getId() {
		return id;
	}

	public int getNumCols() {
		return numCols;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getWidth() {
		return width;
	}
}
