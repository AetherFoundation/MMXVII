package moe.thisis.mmxvii.game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import moe.thisis.mmxvii.engine.Utils;
import moe.thisis.mmxvii.engine.Window;
import moe.thisis.mmxvii.engine.graph.Mesh;
import moe.thisis.mmxvii.engine.graph.ShaderProgram;

public class Renderer {

	private ShaderProgram shaderProgram;
	
    public Renderer() {        
    }
    
    public void init() throws Exception {
    	shaderProgram = new ShaderProgram();
    	shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
    	shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
    	shaderProgram.link();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
