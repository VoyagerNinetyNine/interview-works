package edu.hitsz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SaveScore {
    private JTextField nameInput;
    private JPanel mainPanel;
    private JLabel label;
    private JButton yesButton;
    private JButton noButton;
    public static String name;
    public static JFrame frame;
    public static boolean save;
    public static boolean frameNotValid = false;
    public SaveScore() {
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                name = nameInput.getText();
                frame.dispose();
                save = true;
                frameNotValid = true;
            }
        });
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                frame.dispose();
                save = false;
                frameNotValid = true;
            }
        });
    }

    public static void main(String[] args) {
        int windowWidth = 400;
        int windowHeight = 200;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("SaveScore");
        frame.setContentPane(new SaveScore().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setBounds(((int) screenSize.getWidth() - windowWidth) / 2, 300,
                windowWidth, windowHeight);
    }

    public void myMain(String[] args){
        main(args);
    }

    public String getName(){
        return name;
    }

    public boolean getSave(){
        return save;
    }

    public boolean getFrameNotValid(){
        return frameNotValid;
    }
}
