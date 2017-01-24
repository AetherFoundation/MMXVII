package moe.thisis.aether.bokuseru.engine.graph.anim;

import java.util.List;

import org.joml.Matrix4f;

import moe.thisis.aether.bokuseru.engine.graph.Mesh;
import moe.thisis.aether.bokuseru.engine.items.GameItem;

public class AnimGameItem extends GameItem {

	private int currentFrame;

	private List<AnimatedFrame> frames;

	private final List<Matrix4f> invJointMatrices;

	public AnimGameItem(final Mesh[] meshes, final List<AnimatedFrame> frames, final List<Matrix4f> invJointMatrices) {
		super(meshes);
		this.frames = frames;
		this.invJointMatrices = invJointMatrices;
		currentFrame = 0;
	}

	public AnimatedFrame getCurrentFrame() {
		return frames.get(currentFrame);
	}

	public List<AnimatedFrame> getFrames() {
		return frames;
	}

	public List<Matrix4f> getInvJointMatrices() {
		return invJointMatrices;
	}

	public AnimatedFrame getNextFrame() {
		int nextFrame = currentFrame + 1;
		if (nextFrame > (frames.size() - 1)) {
			nextFrame = 0;
		}
		return frames.get(nextFrame);
	}

	public void nextFrame() {
		final int nextFrame = currentFrame + 1;
		if (nextFrame > (frames.size() - 1)) {
			currentFrame = 0;
		} else {
			currentFrame = nextFrame;
		}
	}

	public void setFrames(final List<AnimatedFrame> frames) {
		this.frames = frames;
	}
}
