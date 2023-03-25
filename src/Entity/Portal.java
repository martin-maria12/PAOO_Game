package Entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import TileMap.TileMap;

public class Portal extends MapObject {

    private BufferedImage[] sprites;

    public Portal(TileMap tm) {
        super(tm);
        facingRight = true;
        width = height = 40;
        cwidth = 20;
        cheight = 40;
        try {
            BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Items/Portal.png")
            );
            sprites = new BufferedImage[9];
            for(int i = 0; i < sprites.length; i++) {
                sprites[i] = spritesheet.getSubimage(
                        i * width, 0, width, height
                );
            }
            animation.setFrames(sprites);
            animation.setDelay(50);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        animation.update();
    }

    public void draw(Graphics2D g) {
        setMapPosition();
        super.draw(g);
    }

}
