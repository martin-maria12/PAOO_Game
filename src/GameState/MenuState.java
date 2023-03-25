package GameState;

import Audio.AudioPlayer;
import TileMap.Background;
import TratareExceptii.File;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MenuState extends GameState {
	
	private Background bg;

	private AudioPlayer bgMusic;
	
	private int currentChoice = 0;
	private final String[] options = {
		"Start",
		"Help",
		"Quit"
	};
	
	private Font font;
	
	public MenuState(GameStateManager gsm) {
		
		this.gsm = gsm;
		init();
		
		try {
			
			bg = new Background("/Backgrounds/Coperta.png", 1);
			bg.setVector(0, 0);
			if(bg == null){
				throw new File("File not found");
			}
			
			font = new Font("Broadway", Font.PLAIN, 18);
			
		}
		catch(File e){
			System.out.println(e.getMessage());
		}


	}
	
	public void init() {
		bgMusic = new AudioPlayer("/Music/SFX_Menu.wav");
		bgMusic.play();
	}
	
	public void update() {
		bg.update();
	}
	
	public void draw(Graphics2D g) {
		
		// draw bg
		bg.draw(g);

		// draw menu options
		g.setFont(font);
		for(int i = 0; i < options.length; i++) {
			if(i == currentChoice) {
				g.setColor(Color.GRAY);
			}
			else {
				g.setColor(Color.WHITE);
			}
			g.drawString(options[i], 125, 125 + i * 20);
		}
		
	}
	
	private void select() {
		if(currentChoice == 0) {
			bgMusic.stop();
			gsm.setState(GameStateManager.LEVEL1STATE);
		}
		if(currentChoice == 1) {
			bgMusic.stop();
			gsm.setState(GameStateManager.HELPSTATE);
		}
		if(currentChoice == 2) {
			System.exit(0);
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










