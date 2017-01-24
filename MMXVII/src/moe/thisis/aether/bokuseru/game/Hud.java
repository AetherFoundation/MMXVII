package moe.thisis.aether.bokuseru.game;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import moe.thisis.aether.bokuseru.engine.Utils;
import moe.thisis.aether.bokuseru.engine.Window;

public class Hud {

	private static final String FONT_NAME = "BOLD";

	private long vg;

	private NVGColor colour;

	private ByteBuffer fontBuffer;

	private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private DoubleBuffer posx;

	private DoubleBuffer posy;

	private int counter;

	public void cleanup() {
		NanoVGGL3.nvgDelete(vg);
	}

	public void incCounter() {
		counter++;
		if (counter > 99) {
			counter = 0;
		}
	}

	public void init(final Window window) throws Exception {
		vg = window.getOptions().antialiasing
				? NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES)
				: NanoVGGL3.nvgCreate(NanoVGGL3.NVG_STENCIL_STROKES);
		if (vg == MemoryUtil.NULL) {
			throw new Exception("Could not init nanovg");
		}

		fontBuffer = Utils.ioResourceToByteBuffer("/fonts/OpenSans-Bold.ttf", 150 * 1024);
		final int font = NanoVG.nvgCreateFontMem(vg, Hud.FONT_NAME, fontBuffer, 0);
		if (font == -1) {
			throw new Exception("Could not add font");
		}
		colour = NVGColor.create();

		posx = BufferUtils.createDoubleBuffer(1);
		posy = BufferUtils.createDoubleBuffer(1);

		counter = 0;
	}

	public void render(final Window window) {
		NanoVG.nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);

		// Upper ribbon
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, 0, window.getHeight() - 100, window.getWidth(), 50);
		NanoVG.nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 200, colour));
		NanoVG.nvgFill(vg);

		// Lower ribbon
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, 0, window.getHeight() - 50, window.getWidth(), 10);
		NanoVG.nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
		NanoVG.nvgFill(vg);

		GLFW.glfwGetCursorPos(window.getWindowHandle(), posx, posy);
		final int xcenter = 50;
		final int ycenter = window.getHeight() - 75;
		final int radius = 20;
		final int x = (int) posx.get(0);
		final int y = (int) posy.get(0);
		final boolean hover = (Math.pow(x - xcenter, 2) + Math.pow(y - ycenter, 2)) < Math.pow(radius, 2);

		// Circle
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgCircle(vg, xcenter, ycenter, radius);
		NanoVG.nvgFillColor(vg, rgba(0xc1, 0xe3, 0xf9, 200, colour));
		NanoVG.nvgFill(vg);

		// Clicks Text
		NanoVG.nvgFontSize(vg, 25.0f);
		NanoVG.nvgFontFace(vg, Hud.FONT_NAME);
		NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_TOP);
		if (hover) {
			NanoVG.nvgFillColor(vg, rgba(0x00, 0x00, 0x00, 255, colour));
		} else {
			NanoVG.nvgFillColor(vg, rgba(0x23, 0xa1, 0xf1, 255, colour));

		}
		NanoVG.nvgText(vg, 50, window.getHeight() - 87, String.format("%02d", counter));

		// Render hour text
		NanoVG.nvgFontSize(vg, 40.0f);
		NanoVG.nvgFontFace(vg, Hud.FONT_NAME);
		NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
		NanoVG.nvgFillColor(vg, rgba(0xe6, 0xea, 0xed, 255, colour));
		NanoVG.nvgText(vg, window.getWidth() - 150, window.getHeight() - 95, dateFormat.format(new Date()));

		NanoVG.nvgEndFrame(vg);

		// Restore state
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	private NVGColor rgba(final int r, final int g, final int b, final int a, final NVGColor colour) {
		colour.r(r / 255.0f);
		colour.g(g / 255.0f);
		colour.b(b / 255.0f);
		colour.a(a / 255.0f);

		return colour;
	}
}
