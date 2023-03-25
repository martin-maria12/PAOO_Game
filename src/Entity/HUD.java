package Entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class HUD {

    private final Player player;

    private BufferedImage image;
    private Font font;

    public HUD(Player p){
        player = p;
        try{
            image = ImageIO.read(
                    Objects.requireNonNull(getClass().getResourceAsStream(
                            "/HUD/HUD1.png"
                    ))
            );
            font = new Font("Arial", Font.PLAIN, 10);
        }
        catch(Exception e){

            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g){

        g.drawImage(image, 0, 10, null);
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("S:  ", 2, 33);
        g.drawString(player.getHealth() + "/" + player.getMaxHealth(), 20, 21 );
        g.drawString(String.valueOf(player.getScore()),20,33);

    }
}
