package GameState;

import TileMap.Background;
import TratareExceptii.File;

import java.awt.*;
import java.awt.event.KeyEvent;

public class HelpState extends GameState {

    private Background bg;

    private int currentChoice = 0;
    private final String[] options = {
            "Back"
    };

    private Font font;

    public HelpState(GameStateManager gsm) {

        this.gsm = gsm;
        init();

        try {

            bg = new Background("/Backgrounds/help.png", 1);
            bg.setVector(0, 0);

            if(bg == null){
                throw new File("File not found");
            }

            font = new Font("Broadway", Font.PLAIN, 18);

        }
        catch(File e) {
           System.out.println((e.getMessage()));
        }

    }

    public void init() {}

    public void update() {
        bg.update();
    }

    public void draw(Graphics2D g) {

        // draw bg
        bg.draw(g);

        // draw menu options
        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString(options[0], 135, 195);
    }

    private void select() {
        if(currentChoice == 0) {
            gsm.setState(GameStateManager.MENUSTATE);
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










