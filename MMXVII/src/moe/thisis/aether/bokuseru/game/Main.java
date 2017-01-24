package moe.thisis.aether.bokuseru.game;

import moe.thisis.aether.bokuseru.engine.GameEngine;
import moe.thisis.aether.bokuseru.engine.IGameLogic;
import moe.thisis.aether.bokuseru.engine.Window;

public class Main {

    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new Bokuseru();
            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = true;
            opts.showFps = true;
            opts.compatibleProfile = true;
            opts.antialiasing = true;
            GameEngine gameEng = new GameEngine("GAME", vSync, opts, gameLogic);
            gameEng.start();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}
