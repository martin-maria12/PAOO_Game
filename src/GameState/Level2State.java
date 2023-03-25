package GameState;

import TileMap.*;
import Entity.*;
import Entity.Enemies.*;
import Audio.*;
import Main.GamePanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

public class Level2State extends GameState {

    private TileMap tileMap;
    private Background bg;

    private Player player;

    private ArrayList<Fish> fish;
    private ArrayList<Yarn> yarns;

    private Portal portal;

    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;

    private HUD hud;

    private AudioPlayer bgMusic;

    // events
    private boolean eventFinish;
    private boolean eventDead;

    public Level2State(GameStateManager gsm) {
        this.gsm = gsm;
        init();
    }

    public void init() {

        tileMap = new TileMap(30);
        tileMap.loadTiles("/Tilesets/p611.png");
        tileMap.loadMap("/Maps/mapmap2.map");
        tileMap.setPosition(0, 0);
        tileMap.setTween(1);

        bg = new Background("/Backgrounds/Cartoon_Forest_BG_04.png", 0.1);

        player = player.getInstance(tileMap);
        //where the player starts level2
        player.setPosition(50, 150);
        player.setScore(0);
        player.setKills(0);

        populateEnemies();

        explosions = new ArrayList<Explosion>();

        hud = new HUD(player);

        populateFish();
        populateYarns();

        portal = new Portal(tileMap);
        //where is the portal for the next level
        portal.setPosition(100,860);

        bgMusic = new AudioPlayer("/Music/MUSIC_LVL2.wav");
        bgMusic.play();

    }

    private void populateYarns() {

        yarns = new ArrayList<Yarn>();

        Yarn ya;

        //where are the yarns
        Point[] points = new Point[]{
                new Point(52, 480),
                new Point(355, 830),
                new Point(1290, 637),
                new Point(1000, 345),
                new Point(1255, 120)
        };

        for (int i = 0; i < points.length; i++) {
            ya = new Yarn(tileMap);
            ya.setPosition(points[i].x, points[i].y);
            yarns.add(ya);
        }

    }

    private void populateFish() {

        fish = new ArrayList<Fish>();

        Fish f;

        //where are the fish
        Point[] points = new Point[]{
                new Point(356, 333),
                new Point(462, 638),
                new Point(828, 711),
                new Point(1525, 605),
                new Point(1425, 367),
                new Point(810, 400),
                new Point(1330, 850)
        };

        for (int i = 0; i < points.length; i++) {
            f = new Fish(tileMap);
            f.setPosition(points[i].x, points[i].y);
            fish.add(f);
        }

    }

    private void populateEnemies() {

        enemies = new ArrayList<Enemy>();

        Rat s;

        //where are the enemies
        Point[] points = new Point[]{
                new Point(1530, 875),
                new Point(1330, 875),
                new Point(1130, 875),
                new Point(930, 875),
                new Point(730, 875),
                new Point(530, 875),
                new Point(170, 875),
        };
        for (int i = 0; i < points.length; i++) {
            s = new Rat(tileMap);
            s.setPosition(points[i].x, points[i].y);
            enemies.add(s);
        }
    }


    public void update() {

        // update player
        player.update();
        tileMap.setPosition(
                GamePanel.WIDTH / 2 - player.getx(),
                GamePanel.HEIGHT / 2 - player.gety()
        );

        // set background
        bg.setPosition(tileMap.getx(), tileMap.gety());

        // attack enemies
        player.checkAttack(enemies);

        // update all enemies
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            e.update();
            if (e.isDead()) {
                player.addKill();
                enemies.remove(i);
                i--;
                explosions.add(
                        new Explosion((int) e.getx(), (int) e.gety()));
            }
        }

        // update all explosions
        for (int i = 0; i < explosions.size(); i++) {
            explosions.get(i).update();
            if (explosions.get(i).shouldRemove()) {
                explosions.remove(i);
                i--;
            }
        }

        if(player.getHealth() == 0) {
            player.setDead();
            eventDead = true;
            bgMusic.stop();
        }

        if (player.intersects(portal)) eventFinish = true;
        if (eventDead) eventDead();
        if (eventFinish) eventFinish();

        for (int i = 0; i < fish.size(); i++) {
            if (player.intersects(fish.get(i)) && !fish.get(i).shouldRemove()) {
                fish.get(i).Remove();
                player.collectFish(1);
            }
        }

        for (int i = 0; i < yarns.size(); i++) {
            if (player.intersects(yarns.get(i)) && !yarns.get(i).shouldRemove()) {
                yarns.get(i).Remove();
                player.collectYarn(1);
            }
        }
    }

    private void eventFinish() {
        player.setTeleporting(true);
        player.stop();

        Connection c = null;
        Statement s = null;

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
            System.out.println("Level2");
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        bgMusic.stop();
        gsm.setState(GameStateManager.LEVEL3STATE);
    }

    public void draw(Graphics2D g) {

        // draw bg
        bg.draw(g);

        // draw tilemap
        tileMap.draw(g);

        // draw player
        player.draw(g);

        // draw enemies;
        for (int i = 0; i < enemies.size(); i++)
            enemies.get(i).draw(g);

        // draw explosions
        for (int i = 0; i < explosions.size(); i++) {
            explosions.get(i).setMapPosition(
                    (int) tileMap.getx(), (int) tileMap.gety());
            explosions.get(i).draw(g);
        }

        // draw hud
        hud.draw(g);

        // draw fish
        for (int i = 0; i < fish.size(); i++) {
            if (!fish.get(i).shouldRemove()) {
                fish.get(i).draw(g);
            }
        }

        // draw fish
        for (int i = 0; i < yarns.size(); i++) {
            if (!yarns.get(i).shouldRemove()) {
                yarns.get(i).draw(g);
            }
        }

        //draw portal
        portal.draw(g);
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
