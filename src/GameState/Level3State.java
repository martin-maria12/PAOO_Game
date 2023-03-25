package GameState;

import Audio.AudioPlayer;
import Entity.*;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;

public class Level3State extends GameState {

    private TileMap tileMap;
    private Background bg;

    private Player player;
    private Dexter boss;

    private HUD hud;
    private HUDBOSS hudboss;

    private AudioPlayer bgMusic;

    // events
    private boolean eventFinish;
    private boolean eventDead;

    public Level3State(GameStateManager gsm) {
        this.gsm = gsm;
        init();
    }

    public void init() {

        tileMap = new TileMap(30);
        tileMap.loadTiles("/Tilesets/p62.png");
        tileMap.loadMap("/Maps/mapmap3.map");
        tileMap.setPosition(0, 0);
        tileMap.setTween(1);

        bg = new Background("/Backgrounds/Cartoon_Forest_BG_01.png", 0.1);

        player = new Player(tileMap);
        player.setPosition(50, 250);
        //where the player starts level3
        player.setScore(0);
        player.setKills(0);

        boss = new Dexter(tileMap,player);
        //where is the boss at the beginning
        boss.setPosition(500, 250);

        hud = new HUD(player);
        hudboss = new HUDBOSS(boss);

        bgMusic = new AudioPlayer("/Music/MUSIC_BOSS1.wav");
        bgMusic.play();
    }

    public void update() {

        // update player
        player.update();
        boss.update();
        tileMap.setPosition(
                GamePanel.WIDTH / 2. - player.getx(),
                GamePanel.HEIGHT / 2. - player.gety()
        );

        // set background
        bg.setPosition(tileMap.getx(), tileMap.gety());

        // attack enemies
        player.checkAttack(boss);
        boss.checkAttack(player);

        if(player.getHealth() == 0) {
            player.setDead();
            eventDead = true;
            bgMusic.stop();
        }

        if (boss.getDeadDone()) eventFinish = true;
        if (eventDead) eventDead();
        if (eventFinish) eventFinish();
    }

    private void eventFinish() {
        player.setTeleporting(true);
        player.stop();

        Connection c;
        Statement s;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:ScoreDB.db");
            c.setAutoCommit(false);
            s = c.createStatement();

            String addLvl = "INSERT INTO PLAYER (Score,Lives,Kills) " +
                    "VALUES (" +
                    player.getScore() + "," +
                    player.getHealth() + "," +
                    player.getKills() +")";
            s.execute(addLvl);

            s.close();
            c.commit();
            c.close();
        }catch (Exception e) {
            System.out.println("Level3");
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        try {
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        bgMusic.stop();
        gsm.setState(GameStateManager.MENUFINISH);
    }

    public void draw(Graphics2D g) {

        // draw bg
        bg.draw(g);

        // draw tilemap
        tileMap.draw(g);

        // draw player
        player.draw(g);
        boss.draw(g);

        // draw hud
        hud.draw(g);
        hudboss.draw(g);
    }

    public void keyPressed(int k) {
        if (k == KeyEvent.VK_LEFT) player.setLeft(true);
        if (k == KeyEvent.VK_RIGHT) player.setRight(true);
        if (k == KeyEvent.VK_UP) player.setUp(true);
        if (k == KeyEvent.VK_DOWN) player.setDown(true);
        if (k == KeyEvent.VK_SPACE) player.setJumping(true);
        if (k == KeyEvent.VK_R) player.setRunning(true);
        if (k == KeyEvent.VK_H) player.setHitting();
    }

    public void keyReleased(int k) {
        if (k == KeyEvent.VK_LEFT) player.setLeft(false);
        if (k == KeyEvent.VK_RIGHT) player.setRight(false);
        if (k == KeyEvent.VK_UP) player.setUp(false);
        if (k == KeyEvent.VK_DOWN) player.setDown(false);
        if (k == KeyEvent.VK_SPACE) player.setJumping(false);
        if (k == KeyEvent.VK_R) player.setRunning(false);
    }

    // reset level
    private void reset() {
        player.setPosition(100, 300);
    }

    // player has died
    private void eventDead() {
        if(player.getHealth()==0){
            player.resetInstance();
            if(player.getDeadDone())
                gsm.setState(GameStateManager.MENUGAMEOVER);
        }
    }

}
