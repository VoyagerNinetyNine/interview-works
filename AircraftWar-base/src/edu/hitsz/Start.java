package edu.hitsz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Start {
    private JButton simpleMode;
    private JButton hardMode;
    private JButton easyMode;
    private JLabel soundEffect;
    private JComboBox seSwitch;
    private JPanel mainPanel;
    public  static String seChoice;
    public  static String bgImage;
    private Random r = new Random();
    public static JFrame frame;
    public static int frameNotValid;
    public static int levelOfDifficulty;
    public Start() {
        easyMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                seChoice = (String) seSwitch.getSelectedItem();
                frame.dispose();
                setFrameNotValid();
                bgImage = "src/images/bg.jpg";
                levelOfDifficulty = 1;
            }
        });
        simpleMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                seChoice = (String) seSwitch.getSelectedItem();
                frame.dispose();
                setFrameNotValid();
                if(r.nextInt(2)==1){
                    bgImage = "src/images/bg2.jpg";
                }
                else{
                    bgImage = "src/images/bg3.jpg";
                }
                levelOfDifficulty = 2;
            }
        });
        hardMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                seChoice = (String) seSwitch.getSelectedItem();
                frame.dispose();
                setFrameNotValid();
                if(r.nextInt(2)==1){
                    bgImage = "src/images/bg4.jpg";
                }
                else{
                    bgImage = "src/images/bg5.jpg";
                }
                levelOfDifficulty = 3;
            }
        });
    }

    public String getBgImage(){
        return bgImage;
    }

    public static void main(String[] args) {
        int windowWidth = 400;
        int windowHeight = 550;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("Start");
        frame.setContentPane(new Start().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setBounds(((int) screenSize.getWidth() - windowWidth) / 2, 100,
                windowWidth, windowHeight);
    }

    public void myMain(String[] args){
        frameNotValid = 0;
        main(args);
    }

    public int getFrameNotValid(){
        return frameNotValid;
    }

    public void setFrameNotValid(){
        frameNotValid = 1;
    }

    public String getSeChoice(){
        return seChoice;
    }

    public int getLevelOfDifficulty(){
        return levelOfDifficulty;
    }
}
