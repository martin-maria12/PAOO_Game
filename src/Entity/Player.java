package Entity;

import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;

public class Player extends MapObject {

	protected static Player instance = null;

	// player stuff
	private int health;
	private final int maxHealth;
	private boolean dead;
	private boolean deadDone = false;

	private boolean flinching;
	private long flinchTimer;

	private boolean teleporting;

	private int Score;
	private int kills;

	// hit
	private boolean hitting;
	private final int hitDamage;
	private final int hitRange;
	
	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {
		10, 10, 8, 8, 8, 10, 10
	};
	
	// animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int RUNNING = 4;
	private static final int DYING = 5;
	private static final int HITTING = 6;
	
	public Player(TileMap tm) {
		
		super(tm);
		
		width = 30;
		height = 30;
		cwidth = 20;
		cheight = 20;
		
		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		
		facingRight = true;
		
		health = maxHealth = 5;
		Score = 0;

		hitDamage = 1;
		hitRange = 40;
		
		// load sprites
		try {
			
			BufferedImage spritesheet = ImageIO.read(
					Objects.requireNonNull(getClass().getResourceAsStream(
							"/Sprites/Player/playersprites.png"
					))
			);
			
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 7; i++) {
				
				BufferedImage[] bi =
					new BufferedImage[numFrames[i]];
				
				for(int j = 0; j < numFrames[i]; j++) {
					
					if(i != 7) {
						bi[j] = spritesheet.getSubimage(
								j * width,
								i * height,
								width,
								height
						);
					}
					else {
						bi[j] = spritesheet.getSubimage(
								j * width * 2,
								i * height,
								width * 2,
								height
						);
					}
					
				}
				
				sprites.add(bi);
				
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
		
	}

	public static Player getInstance(TileMap tm){
		if(instance == null){
			instance = new Player(tm);
		}
		return instance;
	}

	public void resetInstance(){ instance = null; }

	public int getHealth() { return health; }
	public int getMaxHealth() { return maxHealth; }

	public void setDying() {
		dead = true;
	}
	public void setHitting() {
		hitting = true;
	}
	public boolean getDeadDone() { return deadDone; }

	public void setScore(int s) { Score = s;}
	public int getScore(){return Score;}
	public void addKill() {kills++;}

	public void checkAttack(ArrayList<Enemy> enemies) {

		//loop through enemies
		for (int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);

			//hit
			if (hitting) {
				if (facingRight) {
					if (e.getx() > x && e.getx() < x + hitRange && e.gety() > y - height / 2. && e.gety() < y + height / 2.) {
						e.hit(hitDamage);
					}
				}
				else{
					if (e.getx() < x && e.getx() > x - hitRange && e.gety() > y - height / 2. && e.gety() < y + height / 2.) {
						e.hit(hitDamage);
					}
				}
			}
			// check for enemy collision
			if(intersects(e)) {
				hit(e.getDamage());
			}
		}
	}

	public void checkAttack(Dexter boss) {
		if (hitting) {
			if (facingRight) {
				if (boss.getx() > x && boss.getx() < x + hitRange && boss.gety() > y - height / 2. && boss.gety() < y + height / 2.) {
					boss.hit(hitDamage);
				}
			}
			else{
				if (boss.getx() < x && boss.getx() > x - hitRange && boss.gety() > y - height / 2. && boss.gety() < y + height / 2.) {
					boss.hit(hitDamage);
				}
			}
		}
		// check for enemy collision
		if(intersects(boss)) {
			hit(boss.getDamage());
		}
	}

	public void reset() {
		health = maxHealth;
		facingRight = true;
		currentAction = -1;
		stop();
	}

	public void stop() {

		left = right = up = down = flinching = jumping = hitting = false;
	}

	public void setDead() {
		health = 0;
		stop();
	}

	public void hit(int damage){
		if(flinching) return;
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) dead = true;
		if(currentAction != HITTING) {
			flinching = true;
			flinchTimer = System.nanoTime();
		}
	}

	private void getNextPosition() {
		
		// movement
		if(left) {
			double spe = 1;
			if(running) spe = 1.5;
			dx -= spe*moveSpeed;
			if(dx < -spe*maxSpeed) {
				dx = -spe*maxSpeed;
			}
		}
		else if(right) {
			double spe = 1;
			if(running) spe = 1.5;
			dx += spe*moveSpeed;
			if(dx > spe*maxSpeed) {
				dx = spe*maxSpeed;
			}
		}
		else {
			if(dx > 0) {
				dx -= stopSpeed;
				if(dx < 0) {
					dx = 0;
				}
			}
			else if(dx < 0) {
				dx += stopSpeed;
				if(dx > 0) {
					dx = 0;
				}
			}
		}
		
		// jumping
		if(jumping && !falling) {
			dy = jumpStart;
			falling = true;	
		}
		
		// falling
		if(falling) {
			
			if(dy > 0 ) dy += fallSpeed * 0.3;
			else dy += fallSpeed;
			
			if(dy > 0) jumping = false;
			if(dy < 0 && !jumping) dy += stopJumpSpeed;
			
			if(dy > maxFallSpeed) dy = maxFallSpeed;
			
		}

	}

	public void collectFish(int i){
			Score = Score + 25;
	}

	public void collectYarn(int i){
		Score = Score + 50;
	}
	public void update() {
		
		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);

		//check attack has stopped
		if(currentAction == HITTING){
			if(animation.hasPlayedOnce()) hitting = false;
		}

		if(currentAction == DYING){
			if(animation.hasPlayedOnce()) deadDone = true;
		}

		// check done flinching
		if(flinching) {
			long elapsed =
					(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 1000) {
				flinching = false;
			}
		}

		// set animation
		if(hitting) {
			if(currentAction != HITTING) {
				currentAction = HITTING;
				animation.setFrames(sprites.get(HITTING));
				animation.setDelay(80);
				width = 30;

			}
		}
		else if(dead){
			if(currentAction != DYING){
				currentAction = DYING;
				animation.setFrames(sprites.get(DYING));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if(dy > 0) {
			if(currentAction != FALLING) {
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if(dy < 0) {
			if(currentAction != JUMPING) {
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if((left || right) && !running) {
			if (currentAction != WALKING) {
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if((left || right) && running) {
			if(currentAction != RUNNING) {
				currentAction = RUNNING;
				animation.setFrames(sprites.get(RUNNING));
				animation.setDelay(100);
				width = 30;
			}
		}
		else {
			if(currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(200);
				width = 30;
			}
		}
		
		animation.update();
		
		// set direction
		if(currentAction != HITTING) {
			if(right) facingRight = true;
			if(left) facingRight = false;
		}
		
	}
	
	public void draw(Graphics2D g) {
		//where we draw the character
		setMapPosition();
		
		// draw player
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2 == 0) {
				return;
			}
		}
		
		super.draw(g);
		
	}

	public void setTeleporting(boolean t) { teleporting = t; }

    public int getKills() {
		return kills;
    }

	public void setKills(int i) {
		kills = i;
	}

}








