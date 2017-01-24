package moe.thisis.aether.bokuseru.engine.graph;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.io.InputStream;
import java.nio.ByteBuffer;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Texture {

	private final int id;

	private final int width;

	private final int height;

	private int numRows = 1;

	private int numCols = 1;

	public Texture(InputStream is) throws Exception {
		try {
			// Load Texture file
			PNGDecoder decoder = new PNGDecoder(is);

			this.width = decoder.getWidth();
			this.height = decoder.getHeight();

			// Load texture contents into a byte buffer
			ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
			decoder.decode(buf, decoder.getWidth() * 4, Format.RGBA);
			buf.flip();

			// Create a new OpenGL texture
			this.id = glGenTextures();
			// Bind the texture
			glBindTexture(GL_TEXTURE_2D, this.id);

			// Tell OpenGL how to unpack the RGBA bytes. Each component is 1
			// byte size
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			// Upload the texture data
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
			// Generate Mip Map
			glGenerateMipmap(GL_TEXTURE_2D);

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
	public Texture(int width, int height, int pixelFormat) throws Exception {
		this.id = glGenTextures();
		this.width = width;
		this.height = height;
		glBindTexture(GL_TEXTURE_2D, this.id);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, this.width, this.height, 0, pixelFormat, GL_FLOAT,
				(ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}

	public Texture(String fileName) throws Exception {
		this(Texture.class.getResourceAsStream(fileName));
	}

	public Texture(String fileName, int numCols, int numRows) throws Exception {
		this(fileName);
		this.numCols = numCols;
		this.numRows = numRows;
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public void cleanup() {
		glDeleteTextures(id);
	}

	public int getHeight() {
		return this.height;
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
		return this.width;
	}
}
