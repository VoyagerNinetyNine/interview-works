package com.example.aircraftwar.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.aircraftwar.DAO.ScoreDAOImpl;
import com.example.aircraftwar.DAO.ScoreDocument;
import com.example.aircraftwar.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


/**
 * 程序入口
 * @author hitsz
 */
public class MainActivity extends Activity {

    public static int WINDOW_WIDTH;
    public static int WINDOW_HEIGHT;
    public static boolean seIsChecked;
    public static int bgImagePath;
    public static int heroImagePath = R.drawable.hero;
    public static int heroBulletPath = R.drawable.bullet_hero;
    public static int heroImageNumber = 1;
    public static GameTemplate game;
    private final Random r = new Random();
    private final Context context = this;
    private int rankToDelete = -1;
    private List<TableRow> tableRows;
    private Socket socket;
    public String playerName;
    public Handler handler;
    public ScoreDocument myFinalScore,rivalFinalScore;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Point p = new Point();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(p);
        WINDOW_WIDTH = p.x;
        WINDOW_HEIGHT = p.y;
        super.onCreate(savedInstanceState);
        ImageManager.setResource(getResources());
        handler = new Handler(message -> {
            if(message.what == 1){
                game = new NetworkingGame(context, socket);
                setContentView(game);
            }
            else if(message.what == 2){
                setNetworkingScorerank(myFinalScore,rivalFinalScore);
            }
            return true;
        });
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setSelectfightuiView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setSelectuiView(){
        setContentView(R.layout.selectui);

        Button easymodeButton = findViewById(R.id.easymode);
        easymodeButton.setOnClickListener(view -> {
            bgImagePath = R.drawable.bg;
            game = new EasyModeGame(context);
            setContentView(game);
        });

        Button simplemodeButton = findViewById(R.id.simplemode);
        simplemodeButton.setOnClickListener(view -> {
            bgImagePath = R.drawable.bg2;
            game = new SimpleModeGame(context);
            setContentView(game);
        });

        Button hardmodeButton = findViewById(R.id.hardmode);
        hardmodeButton.setOnClickListener(view -> {
            bgImagePath = R.drawable.bg4;
            game = new HardModeGame(context);
            setContentView(game);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setSavescoreView(int score, String path, String datetime, int difficulity){
        setContentView(R.layout.savescore);

        Button savescoreButton = findViewById(R.id.savescoreButton);
        savescoreButton.setOnClickListener(view -> {
            EditText text = findViewById(R.id.nameInput);
            String name = text.getText().toString();
            ScoreDAOImpl dao = new ScoreDAOImpl(path,context);
            dao.sortByScore();
            dao.doAdd(new ScoreDocument(score,name,datetime));
            dao.save();
            setScorerankView(difficulity,path);
        });

        Button notsavescoreButton = findViewById(R.id.notsavescorebutton);
        notsavescoreButton.setOnClickListener(view -> setScorerankView(difficulity,path));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setScorerankView(int difficulity, String path){
        setContentView(R.layout.scorerank);

        Button deleteButton = findViewById(R.id.deletebutton);
        deleteButton.setOnClickListener(view -> {
            if(rankToDelete != -1) {
                ScoreDAOImpl dao = new ScoreDAOImpl(path, context);
                dao.sortByScore();
                dao.deleteByRank(rankToDelete);
                dao.save();
                rankToDelete = -1;
                setScorerankView(difficulity, path);
            }
        });

        TextView difficulityDisplay = findViewById(R.id.difficulty);
        difficulityDisplay.setText(difficulity);

        Button restartButton = findViewById(R.id.restartbutton);
        restartButton.setOnClickListener(view -> setSelectfightuiView());

        Button exitButton = findViewById(R.id.exitbutton);
        exitButton.setOnClickListener(view -> System.exit(0));

        ScoreDAOImpl dao = new ScoreDAOImpl(path,context);
        dao.sortByScore();
        List<ScoreDocument> scores = dao.getAllDocuments();
        initData(scores);
    }

    @SuppressLint("SetTextI18n")
    private void initData(List<ScoreDocument> scores) {
        tableRows = new ArrayList<>();
        TableLayout tableLayout = findViewById(R.id.tablelayout);
        tableLayout.setStretchAllColumns(true);
        TextView ranktextview = findViewById(R.id.ranktextview);
        TextView nametextview = findViewById(R.id.nametextview);
        TextView scoretextview = findViewById(R.id.scoretextview);
        TextView timetextview = findViewById(R.id.timetextview);
        for (int row = 1; row<=scores.size(); row++){
            TableRow tableRow = new TableRow(tableLayout.getContext());
            tableRow.setBackgroundColor(Color.WHITE);
            TextView[] textViews = new TextView[4];

            textViews[0] = new TextView(ranktextview.getContext());
            textViews[1] = new TextView(nametextview.getContext());
            textViews[2] = new TextView(scoretextview.getContext());
            textViews[3] = new TextView(timetextview.getContext());

            textViews[0].setText(row + "");
            textViews[1].setText(scores.get(row - 1).getName());
            textViews[2].setText(scores.get(row - 1).getScore() + "");
            textViews[3].setText(scores.get(row - 1).getTime());

            for(int i = 0 ; i < 4 ; i++){
                textViews[i].setClickable(true);
                int finalRow = row;
                textViews[i].setOnClickListener(new View.OnClickListener() {
                    final int rank = finalRow - 1;
                    @Override
                    public void onClick(View view) {
                        if(rankToDelete != -1){
                            tableRows.get(rankToDelete).setBackgroundColor(Color.WHITE);
                        }
                        tableRows.get(rank).setBackgroundColor(Color.YELLOW);
                        rankToDelete = rank;
                    }
                });
                textViews[i].setGravity(Gravity.CENTER);
                textViews[i].setTextColor(Color.BLUE);
                tableRow.addView(textViews[i]);
            }
            tableRows.add(tableRow);
            tableLayout.addView(tableRow,new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setSelectfightuiView(){
        setContentView(R.layout.selectfightui);
        seIsChecked = false;

        Button exit = findViewById(R.id.exitButton);
        exit.setOnClickListener(view -> System.exit(0));

        Button standalone = findViewById(R.id.standalonebutton);
        standalone.setOnClickListener(view -> setSelectuiView());

        Button networkingfight = findViewById(R.id.networkingfightbutton);
        networkingfight.setOnClickListener(view -> setLoginuiView());

        ImageView skinselect = findViewById(R.id.skinselect);
        skinselect.setOnClickListener(view -> setSelectskinView());

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch soundeffectSwitch = findViewById(R.id.soundeffect);
        soundeffectSwitch.setOnCheckedChangeListener((compoundButton, b) -> seIsChecked = b);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setLoginuiView(){
        setContentView(R.layout.loginui);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button loginButton = findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(view -> {
            EditText accountInput = findViewById(R.id.accountinput);
            EditText passwordInput = findViewById(R.id.passwordinput);
            playerName = accountInput.getText().toString();
            Login login = new Login(accountInput.getText().toString(),passwordInput.getText().toString(),"login", this);
            if(login.Enter()){
                setWaitFightersView();
            }
            else{
                setLoginuiView();
            }
        });

        Button registerButton = findViewById(R.id.registerbutton);
        registerButton.setOnClickListener(view -> {
            EditText accountInput = findViewById(R.id.accountinput);
            EditText passwordInput = findViewById(R.id.passwordinput);
            Login login = new Login(accountInput.getText().toString(),passwordInput.getText().toString(),"signup",this);
            if(login.Enter()){
                setWaitFightersView();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setWaitFightersView() {
        setContentView(R.layout.waitfighters);

        Button cancleButton = findViewById(R.id.cancelbutton);
        cancleButton.setOnClickListener(view -> {
            setSelectfightuiView();
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Runnable runnable = this::waitingFighters;
        new Thread(runnable).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void waitingFighters(){
        try {
            if(r.nextInt(2) == 0){
                bgImagePath = R.drawable.bg3;
            }
            else{
                bgImagePath = R.drawable.bg5;
            }
            socket = new Socket();
            socket.connect(new InetSocketAddress("10.0.2.2",9999),5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String test = in.readLine();
            while(!Objects.equals(test, "start")) {
                if (test.equals("testvalid")) {
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);
                    writer.println("valid");
                }
                test = in.readLine();
            }
            handler.sendEmptyMessage(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setNetworkingScorerank(ScoreDocument myScore, ScoreDocument rivalScore){
        setContentView(R.layout.networkingrank);

        Button restartButton = findViewById(R.id.netrestartbutton);
        restartButton.setOnClickListener(view -> {
            try {
                if(!socket.isClosed()) {
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);
                    writer.println("restart");
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            setSelectfightuiView();
        });

        Button exitButton = findViewById(R.id.netexitbutton);
        exitButton.setOnClickListener(view -> {
            try {
                if(!socket.isClosed()) {
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);
                    writer.println("restart");
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        });

        List<ScoreDocument> scores = new ArrayList<>();
        int[] colors = new int[2];
        if(myScore.getScore()>=rivalScore.getScore()){
            scores.add(myScore);
            scores.add(rivalScore);
            colors[0] = Color.BLUE;
            colors[1] = Color.RED;
        }
        else{
            scores.add(rivalScore);
            scores.add(myScore);
            colors[0] = Color.RED;
            colors[1] = Color.BLUE;
        }

        TableLayout tableLayout = findViewById(R.id.nettablelayout);
        tableLayout.setStretchAllColumns(true);
        TextView ranktextview = findViewById(R.id.netranktextview);
        TextView nametextview = findViewById(R.id.netnametextview);
        TextView scoretextview = findViewById(R.id.netscoretextview);
        TextView timetextview = findViewById(R.id.nettimetextview);
        for (int row = 1; row<=scores.size(); row++){
            TableRow tableRow = new TableRow(tableLayout.getContext());
            tableRow.setBackgroundColor(Color.WHITE);
            TextView[] textViews = new TextView[4];

            textViews[0] = new TextView(ranktextview.getContext());
            textViews[1] = new TextView(nametextview.getContext());
            textViews[2] = new TextView(scoretextview.getContext());
            textViews[3] = new TextView(timetextview.getContext());

            textViews[0].setText(row + "");
            textViews[1].setText(scores.get(row - 1).getName());
            textViews[2].setText(scores.get(row - 1).getScore() + "");
            textViews[3].setText(scores.get(row - 1).getTime());

            for(int i = 0 ; i < 4 ; i++){
                textViews[i].setGravity(Gravity.CENTER);
                textViews[i].setTextColor(colors[row-1]);
                tableRow.addView(textViews[i]);
            }
            tableLayout.addView(tableRow,new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void networkingScoreRank(ScoreDocument myScore, ScoreDocument rivalfinalscore, boolean updateRank){
        if(!updateRank){
            setNetworkingScorerank(myScore, rivalfinalscore);
        }
        this.myFinalScore = myScore;
        Runnable runnable = ()-> {
            while (!socket.isClosed()) {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String[] stringlist;
                    stringlist = in.readLine().split(",");
                    if (stringlist.length == 4) {
                        rivalFinalScore = new ScoreDocument(Integer.parseInt(stringlist[2]), stringlist[0], stringlist[1]);
                        handler.sendEmptyMessage(2);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }catch(NullPointerException e){
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setSelectskinView(){
        setContentView(R.layout.selectskin);

        ImageView doubleplane = findViewById(R.id.doublehero);
        doubleplane.setOnClickListener(view -> {
            heroImagePath = R.drawable.myplane2;
            heroBulletPath = R.drawable.bullet;
            heroImageNumber = 2;
            setSelectfightuiView();
        });

        ImageView singleplane = findViewById(R.id.singlehero);
        singleplane.setOnClickListener(view -> {
            heroImagePath = R.drawable.hero;
            heroBulletPath = R.drawable.bullet_hero;
            heroImageNumber = 1;
            setSelectfightuiView();
        });

        ImageView tripleplane = findViewById(R.id.tripleplane);
        tripleplane.setOnClickListener(view -> {
            heroImagePath = R.drawable.tripleplane;
            heroBulletPath = R.drawable.bossbullet;
            heroImageNumber = 3;
            setSelectfightuiView();
        });
    }
}