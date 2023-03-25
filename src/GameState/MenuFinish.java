package GameState;

import Audio.AudioPlayer;
import TileMap.Background;
import TratareExceptii.File;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MenuFinish extends GameState {

    private Background bg;

    private AudioPlayer bgMusic;

    private final int maxHealth = 15;
    private int score = 0;
    private int scoreB = 0;
    private int health = 0;
    private int scoreH = 0;
    private int kills = 0;
    private int scoreK = 0;
    private int finalScore = 0;

    private int currentChoice = 0;
    private final String[] options = {
            "Restart Game",
            "Menu",
            "Quit"
    };

    private Font font;

    public MenuFinish(GameStateManager gsm) {

        this.gsm = gsm;
        init();

        try {

            bg = new Background("/Backgrounds/Finaal.png", 1);
            bg.setVector(0, 0);
            if(bg == null){
                throw new File("File not found");
            }

            font = new Font("Broadway", Font.PLAIN, 18);

        }
        catch(File e) {
            System.out.println(e.getMessage());
        }

    }

    public void init() {
        bgMusic = new AudioPlayer("/Music/MUSIC_END.wav");
        bgMusic.play();
        Connection c;
        Statement s;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ScoreDB.db");
            c.setAutoCommit(false);
            s = c.createStatement();

            ResultSet rs = s.executeQuery("SELECT * FROM PLAYER");
            while(rs.next()) {
                score += rs.getInt("Score");
                kills += rs.getInt("Kills");
                health += rs.getInt("Lives");
            }

            rs.close();
            s.close();
            c.commit();
            c.close();
        }catch (Exception e){
            System.out.println("FINAL");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        scoreH = 10 * health;
        scoreK = 15 * kills;
        scoreB = 1000;
        finalScore = score + scoreH + scoreK + scoreB;
    }

    public void update() {
        bg.update();
    }

    public void draw(Graphics2D g) {

        // draw bg
        bg.draw(g);

        int multi;
        // draw menu options
        g.setFont(font);
        for(int i = 0; i < options.length; i++) {
            if(i == currentChoice) {
                g.setColor(Color.GRAY);
            }
            else {
                g.setColor(Color.BLACK);
            }
            switch(i){
                case 1:
                    multi = 7;
                    break;
                case 2:
                    multi = 8;
                    break;
                default:
                    multi = 0;
                    break;
            }
            g.drawString(options[i], 95 + multi * 5, 75 + i * 20);
        }
        String h = "Lives: " + health + "/" + maxHealth;
        String k = "Kills: " + kills;
        String sc = "Points: " + score;
        String fsc = "Final Score: " + finalScore;
        g.setColor(Color.BLACK);
        g.setFont(new Font("Broadway", Font.PLAIN, 15));
        g.drawString(sc,15,150);
        g.drawString(h,115,150);
        g.drawString("+ " + scoreH, 140, 170);
        g.drawString(k, 230, 150);
        g.drawString("+ " + scoreK, 240, 170);
        g.drawString("Boss Killed: +" + scoreB, 80, 185);
        g.setFont(new Font("Broadway", Font.PLAIN, 20));
        g.setColor(Color.RED);
        g.drawString(fsc, 80, 210);

    }

    private void select() {
        if(currentChoice == 0) {
            bgMusic.stop();
            gsm.setState(GameStateManager.LEVEL1STATE);
        }
        if(currentChoice == 1) {
            bgMusic.stop();
            gsm.setState(GameStateManager.MENUSTATE);
        }
        if(currentChoice == 2) {
            System.exit(0);
        }
    }

    public void keyPressed(int k) {
        if(k == KeyEvent.VK_ENTER){
            select();
        }
        if(k == KeyEvent.VK_UP) {
            currentChoice--;
            if(currentChoice == -1) {
                currentChoice = options.length - 1;
            }
        }
        if(k == KeyEvent.VK_DOWN) {
            currentChoice++;
            if(currentChoice == options.length) {
                currentChoice = 0;
            }
        }
    }
    public void keyReleased(int k) {}

}










