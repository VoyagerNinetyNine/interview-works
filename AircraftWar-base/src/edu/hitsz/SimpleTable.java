package edu.hitsz;

import edu.hitsz.DAO.ScoreDAOImpl;
import edu.hitsz.DAO.ScoreDocument;
import edu.hitsz.application.GameTemplate;
import edu.hitsz.application.Main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SimpleTable {
    private JTable scoreTable;
    private JScrollPane pane;
    private JPanel mainPanel;
    private JButton deleteButton;
    private JLabel rankLable;
    private JLabel difficultyLable;
    private static ScoreDAOImpl dao;

    public SimpleTable(){
        if(Main.levelOfDifficulty == 1){
            difficultyLable.setText("难度：easy");
        }
        else if(Main.levelOfDifficulty == 2){
            difficultyLable.setText("难度：simple");
        }
        else{
            difficultyLable.setText("难度：hard");
        }
        dao = new ScoreDAOImpl(GameTemplate.path);
        dao.sortByScore();
        List<ScoreDocument> scores = dao.getAllScores();
        String[] columnName = {"排名","玩家","得分","时间"};
        String[][] tableData = new String[scores.size()][];
        int i = 1;
        for(ScoreDocument score : scores){
            tableData[i-1] = new String[4];
            tableData[i-1][0] = String.valueOf(i);
            tableData[i-1][1] = score.getName();
            tableData[i-1][2] = String.valueOf(score.getScore());
            tableData[i-1][3] = score.getTime();
            i = i+1;
        }

        DefaultTableModel model = new DefaultTableModel(tableData,columnName){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scoreTable.setModel(model);
        pane.setViewportView(scoreTable);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int row = scoreTable.getSelectedRow();
                if(row != -1){
                    model.removeRow(row);
                    dao.deleteByRank(row);
                    dao.save();
                }
            }
        });
    }

    public static void main(String[] args) {
        int windowWidth = 550;
        int windowHeight = 600;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("ScoreRank");
        frame.setContentPane(new SimpleTable().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setBounds(((int) screenSize.getWidth() - windowWidth) / 2, 100,
                windowWidth, windowHeight);
    }
}
