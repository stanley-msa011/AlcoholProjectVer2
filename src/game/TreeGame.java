package game;

public class TreeGame {
	/*This class is used for control Tree Game
	 * May be extended if there are more required functions*/
	private GameState gState = null;
	private GameState oldState = null;
	
	public TreeGame(GameState gState){
		this.gState = gState;
	}
	
	public GameState getGameState(){
		return gState;
	}
	
	public GameState getCoin(){
		oldState = new GameState(gState);
		gState.get_coin();
		return gState;
	}

	public GameState loseCoin(){
		oldState = new GameState(gState);
		gState.lose_coin();
		return gState;
	}
	
	public GameState getPrevState(){
		return oldState;
	}
	
	public String toString(){
		return String.valueOf(gState.stage)+" "+String.valueOf(gState.coin);
	}
	
	public void resetStage(GameState newState){
		oldState = gState;
		gState = newState;
	}
	
}
