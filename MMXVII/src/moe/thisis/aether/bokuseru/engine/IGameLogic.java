package moe.thisis.aether.bokuseru.engine;

public interface IGameLogic {

	void cleanup();

	void init(Window window) throws Exception;

	void input(Window window, MouseInput mouseInput);

	void render(Window window);

	void update(float interval, MouseInput mouseInput, Window window);
}