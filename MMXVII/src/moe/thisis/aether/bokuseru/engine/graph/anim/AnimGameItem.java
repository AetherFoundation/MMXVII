package moe.thisis.aether.bokuseru.engine.graph.anim;

import java.util.List;

import org.joml.Matrix4f;

import moe.thisis.aether.bokuseru.engine.graph.Mesh;
import moe.thisis.aether.bokuseru.engine.items.GameItem;

public class AnimGameItem extends GameItem {

	private int currentFrame;

	private List<AnimatedFrame> frames;

	private List<Matrix4f> invJointMatrices;

	public AnimGameItem(Mesh[] meshes, List<AnimatedFrame> frames, List<Matrix4f> invJointMatrices) {
		super(meshes);
		this.frames = frames;
		this.invJointMatrices = invJointMatrices;
		currentFrame = 0;
	}

	public AnimatedFrame getCurrentFrame() {
		return this.frames.get(currentFrame);
	}

	public List<AnimatedFrame> getFrames() {
		return frames;
	}

	public List<Matrix4f> getInvJointMatrices() {
		return invJointMatrices;
	}

	public AnimatedFrame getNextFrame() {
		int nextFrame = currentFrame + 1;
		if (nextFrame > frames.size() - 1) {
			nextFrame = 0;
		}
		return this.frames.get(nextFrame);
	}

	public void nextFrame() {
		int nextFrame = currentFrame + 1;
		if (nextFrame > frames.size() - 1) {
			currentFrame = 0;
		} else {
			currentFrame = nextFrame;
		}
	}

	public void setFrames(List<AnimatedFrame> frames) {
		this.frames = frames;
	}
}
