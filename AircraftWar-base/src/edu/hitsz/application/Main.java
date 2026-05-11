package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;
import edu.hitsz.Start;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;
    public static Start startFrame = new Start();
    public static String sound;
    public static String bgImagePath;
    public static int levelOfDifficulty;
    public static GameTemplate game;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Hello Aircraft War");
        startFrame.myMain(args);
        do {
            Thread.sleep(100);
        } while (startFrame.getFrameNotValid() != 1);
        sound = startFrame.getSeChoice();
        bgImagePath = startFrame.getBgImage();
        levelOfDifficulty = startFrame.getLevelOfDifficulty();

        // 获得屏幕的分辨率，初始化 Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Aircraft War");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if(levelOfDifficulty == 1){
            game = new EasyModeGame();
        }
        else if(levelOfDifficulty == 2){
            game = new SimpleModeGame();
        }
        else{
            game = new HardModeGame();
        }
        frame.add(game);
        frame.setVisible(true);
        game.action();
    }
}
