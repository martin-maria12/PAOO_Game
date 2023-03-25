package GameState;

import Audio.AudioPlayer;
import TileMap.Background;
import TratareExceptii.File;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MenuGameOver extends GameState {

    private Background bg;

    private AudioPlayer bgMusic;

    private int currentChoice = 0;
    private final String[] options = {
            "Restart",
            "Main Menu",
            "Quit"
    };

    private Font font;

    public MenuGameOver(GameStateManager gsm) {

        this.gsm = gsm;
        init();

        try {

            bg = new Background("/Backgrounds/GameOver.png", 1);
            bg.setVector(0, 0);
            if(bg==null) {
                throw new File("File not found");
            }

            font = new Font("Broadway", Font.PLAIN, 18);

        }
        catch(File e) {
            System.out.println(e.getMessage());
        }
    }

    public void init() {
        bgMusic = new AudioPlayer("/Music/SFX_GameOver.wav");
        bgMusic.play();
        Connection c;
        Statement s;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ScoreDB.db");
            c.setAutoCommit(false);
            s = c.createStatement();

            String drop = "DROP TABLE IF EXISTS PLAYER;";
            s.execute(drop);

            String create = "CREATE TABLE \"PLAYER\" (\n" +
                    "\t\"Score\"\tINTEGER NOT NULL,\n" +
                    "\t\"Lives\"\tINTEGER NOT NULL,\n" +
                    "\t\"Kills\"\tINTEGER NOT NULL\n" +
                    ");";
            s.execute(create);

            s.close();
            c.commit();
            c.close();
        }catch (Exception e) {
            System.out.println("Game Manager");
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void update() {
        bg.update();
    }

    public void draw(Graphics2D g) {

        // draw bg
        bg.draw(g);

        // draw menu options
        g.setFont(font);
        for(int i = 0; i < options.length; i++) {
            if(i == currentChoice) {
                g.setColor(Color.WHITE);
            }
            else {
                g.setColor(Color.RED);
            }
            g.drawString(options[i], 105, 125 + i * 20);
        }

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
