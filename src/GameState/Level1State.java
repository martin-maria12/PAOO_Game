package GameState;

import Audio.AudioPlayer;
import Entity.Enemies.Rat;
import Entity.*;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;

public class Level1State extends GameState {

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

	public Level1State(GameStateManager gsm) {
		this.gsm = gsm;
		init();
	}

	public void init() {

		//map and tiles
		tileMap = new TileMap(30);
		tileMap.loadTiles("/Tilesets/p60.png");
		tileMap.loadMap("/Maps/mapmap.map");
		tileMap.setPosition(0, 0);
		tileMap.setTween(1);

		bg = new Background("/Backgrounds/Cartoon_Forest_BG_02.png", 0.1);

		player = new Player(tileMap);
		//where the player starts level1
		player.setPosition(50, 350);
		player.setScore(0);
		player.setKills(0);

		populateEnemies();

		explosions = new ArrayList<>();

		hud = new HUD(player);

		populateFish();
		populateYarns();

		portal = new Portal(tileMap);
		//where is the portal for the next level
		portal.setPosition(180,650);

		bgMusic = new AudioPlayer("/Music/MUSIC_LVL1.wav");
		bgMusic.play();

	}

	private void populateYarns() {

		yarns = new ArrayList<>();

		Yarn ya;

		//where are the yarns
		Point[] points = new Point[]{
				new Point(200, 200),
				new Point(1780, 100),
				new Point(1630, 230),
				new Point(1700, 700),
				new Point(400, 650),
				new Point(2320, 465)
		};

		for (int i = 0; i < points.length; i++) {
			ya = new Yarn(tileMap);
			ya.setPosition(points[i].x, points[i].y);
			yarns.add(ya);
		}

	}

	private void populateFish() {

		fish = new ArrayList<>();

		Fish f;

		//where are the fish
		Point[] points = new Point[]{
				new Point(200, 300),
				new Point(295, 150),
				new Point(670, 150),
				new Point(700, 300),
				new Point(897, 700),
				new Point(1800, 550)
		};

		for (int i = 0; i < points.length; i++) {
			f = new Fish(tileMap);
			f.setPosition(points[i].x, points[i].y);
			fish.add(f);
		}

	}

	private void populateEnemies() {

		enemies = new ArrayList<>();

		Rat s;

		//where are the enemies
		Point[] points = new Point[]{
				new Point(100, 350),
				new Point(400, 350),
				new Point(860, 350),
				new Point(1080, 350),
				new Point(1130, 350),
				new Point(1200, 700),
				new Point(1300, 700),
				new Point(1400, 700),
				new Point(1500, 700),
				new Point(1600, 700),
				new Point(1700, 700),
				new Point(1900, 700),
				new Point(1800, 700),
				new Point(800, 750),
				new Point(700, 750),
				new Point(500, 750)
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
				GamePanel.WIDTH / 2. - player.getx(),
				GamePanel.HEIGHT / 2. - player.gety()
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
			System.out.println("Level1");
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}

		bgMusic.stop();
		gsm.setState(GameStateManager.LEVEL2STATE);
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
