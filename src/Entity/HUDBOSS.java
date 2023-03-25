package Entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class HUDBOSS {

    private final Dexter boss;

    private BufferedImage image;
    private Font font;

    public HUDBOSS(Dexter b){
        boss = b;
        try{
            image = ImageIO.read(
                    Objects.requireNonNull(getClass().getResourceAsStream(
                            "/HUD/HUD2.png"
                    ))
            );
            font = new Font("Arial", Font.PLAIN, 10);
        }
        catch(Exception e){

            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g){
        g.drawImage(image, 212, 10, null);
        g.setFont(font);
        g.setColor(Color.WHITE);
        String hudb = boss.getHealth() + "/" + boss.getMaxHealth();
        g.drawString(hudb, 280, 20 );
    }
}
