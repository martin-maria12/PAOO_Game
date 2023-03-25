package Entity.Enemies;

import Entity.Animation;
import Entity.Enemy;
import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Rat extends Enemy {

    private BufferedImage[] sprites;

    public Rat(TileMap tm) {
        super(tm);

        moveSpeed = 0.3;
        maxSpeed = 0.3;
        fallSpeed = 0.2;
        maxFallSpeed = 10.00;

        width = 30;
        height = 30;
        cwidth = 20;
        cheight = 20;

        health = maxHealth = 1;
        damage = 1;

        // load sprites
        try{
            BufferedImage spritesheet = ImageIO.read(
                    getClass().getResourceAsStream(
                            "/Sprites/Enemies/rat.png"
                    )
            );

            sprites = new BufferedImage[3];
            for(int i = 0; i < sprites.length; i++){
                sprites[i] = spritesheet.getSubimage(
                        i * width,
                        0,
                        width,
                        height
                );
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        animation = new Animation();
        animation.setFrames(sprites);
        animation.setDelay(300);

        right = true;
        facingRight = true;

    }

    private void getNextPosition(){

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

        // falling
        if(falling){
            dy += fallSpeed;
        }
    }

    public void update(){

        // update position
        getNextPosition();
        checkTileMapCollision();
        setPosition(xtemp,ytemp);

        // check flinching
        if(flinching) {
            long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
            if(elapsed > 400) {
                flinching = false;
            }
        }

        //if it hits a wall, go other direction
        if(right && dx == 0){
            right = false;
            left = true;
            facingRight = false;
        }
        else if(left && dx == 0){
            left = false;
            right = true;
            facingRight = true;
        }

        // update animation
        animation.update();

    }

    public void draw(Graphics2D g){

        //if(notOnScreen()) return;

        setMapPosition();

        super.draw(g);
    }

}
