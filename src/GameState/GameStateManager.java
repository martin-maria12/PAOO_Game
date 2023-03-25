package GameState;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class GameStateManager {

	private final GameState[] gameStates;
	private int currentState;

	public static final int NUMGAMESTATES = 7;
	public static final int MENUSTATE = 0;
	public static final int LEVEL1STATE = 1;
	public static final int MENUGAMEOVER = 2;
	public static final int LEVEL2STATE = 3;
	public static final int MENUFINISH = 4;
	public static final int HELPSTATE = 5;
	public static final int LEVEL3STATE = 6;
	
	public GameStateManager() {

		gameStates = new GameState[NUMGAMESTATES];

		Connection c;
		Statement s;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:ScoreDB.db");
			c.setAutoCommit(false);
			s = c.createStatement();

			String drop = "DROP TABLE IF EXISTS PLAYER;";
			s.execute(drop);

			String create = "CREATE TABLE \"PLAYER\" (\n" +
					"\t\"Score\"\tINTEGER NOT NULL,\n" +
					"\t\"Lives\"\tINTEGER NOT NULL,\n" +
					"\t\"Kills\"\tINTEGER NOT NULL\n" +
					");";
			s.execute(create);

			s.close();
			c.commit();
			c.close();
		}catch (Exception e) {
			System.out.println("Game Manager");
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}

		currentState = MENUSTATE;
		loadState(currentState);
		
	}

	private void loadState(int state) {
		if(state == MENUSTATE)
			gameStates[state] = new MenuState(this);
		if(state == LEVEL1STATE)
			gameStates[state] = new Level1State(this);
		else
			if(state == MENUGAMEOVER)
				gameStates[state] = new MenuGameOver(this);
			else if(state == LEVEL2STATE)
			gameStates[state] = new Level2State(this);
			else if(state == MENUFINISH)
				gameStates[state] = new MenuFinish(this);
			else if(state == HELPSTATE)
				gameStates[state] = new HelpState(this);
			else if(state == LEVEL3STATE)
				gameStates[state] = new Level3State(this);
	}

	private void unloadState(int state) {
		gameStates[state] = null;
	}

	public void setState(int state) {
		unloadState(currentState);
		currentState = state;
		loadState(currentState);
	}

	public void update() {
		try {
			gameStates[currentState].update();
		} catch(Exception e) {}
	}

	public void draw(java.awt.Graphics2D g) {
		try {
			gameStates[currentState].draw(g);
		} catch(Exception e) {}
	}

	public void keyPressed(int k) {
		gameStates[currentState].keyPressed(k);
	}

	public void keyReleased(int k) {
		gameStates[currentState].keyReleased(k);
	}

}









