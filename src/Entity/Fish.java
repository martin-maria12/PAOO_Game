package Entity;

import TileMap.TileMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Fish extends MapObject{

    private BufferedImage[] sprites;

    private boolean remove;

    public Fish(TileMap tm) {
        super(tm);

        width = 30;
        height = 30;
        cwidth = 15;
        cheight = 15;

        try{
            BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Items/Fish.png"));

            sprites = new BufferedImage[2];
            for(int i = 0; i < sprites.length; i++) {

                sprites[i]=spritesheet.getSubimage(i * width,0,width,height);

            }
            animation = new Animation();
            animation.setFrames(sprites);
            animation.setDelay(70);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g){

        setMapPosition();
        super.draw(g);
    }

    public boolean shouldRemove(){
        return remove;
    }

    public void Remove()
    {
        remove = true;
    }
}

