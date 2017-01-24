package moe.thisis.aether.bokuseru.engine;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class MouseInput {

	private final Vector2d previousPos;

	private final Vector2d currentPos;

	private final Vector2f displVec;

	private boolean inWindow = false;

	private boolean leftButtonPressed = false;

	private boolean rightButtonPressed = false;

	private GLFWCursorPosCallback cursorPosCallback;

	private GLFWCursorEnterCallback cursorEnterCallback;

	private GLFWMouseButtonCallback mouseButtonCallback;

	public MouseInput() {
		previousPos = new Vector2d(-1, -1);
		currentPos = new Vector2d(0, 0);
		displVec = new Vector2f();
	}

	public Vector2d getCurrentPos() {
		return currentPos;
	}

	public Vector2f getDisplVec() {
		return displVec;
	}

	public void init(final Window window) {
		GLFW.glfwSetCursorPosCallback(window.getWindowHandle(), cursorPosCallback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(final long window, final double xpos, final double ypos) {
				currentPos.x = xpos;
				currentPos.y = ypos;
			}
		});
		GLFW.glfwSetCursorEnterCallback(window.getWindowHandle(), cursorEnterCallback = new GLFWCursorEnterCallback() {
			@Override
			public void invoke(final long window, final boolean entered) {
				inWindow = entered;
			}
		});
		GLFW.glfwSetMouseButtonCallback(window.getWindowHandle(), mouseButtonCallback = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(final long window, final int button, final int action, final int mods) {
				leftButtonPressed = (button == GLFW.GLFW_MOUSE_BUTTON_1) && (action == GLFW.GLFW_PRESS);
				rightButtonPressed = (button == GLFW.GLFW_MOUSE_BUTTON_2) && (action == GLFW.GLFW_PRESS);
			}
		});
	}

	public void input(final Window window) {
		displVec.x = 0;
		displVec.y = 0;
		if ((previousPos.x > 0) && (previousPos.y > 0) && inWindow) {
			final double deltax = currentPos.x - previousPos.x;
			final double deltay = currentPos.y - previousPos.y;
			final boolean rotateX = deltax != 0;
			final boolean rotateY = deltay != 0;
			if (rotateX) {
				displVec.y = (float) deltax;
			}
			if (rotateY) {
				displVec.x = (float) deltay;
			}
		}
		previousPos.x = currentPos.x;
		previousPos.y = currentPos.y;
	}

	public boolean isLeftButtonPressed() {
		return leftButtonPressed;
	}

	public boolean isRightButtonPressed() {
		return rightButtonPressed;
	}
}
