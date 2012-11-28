package game;

public class TreeGame {
	/*This class is used for control Tree Game
	 * May be extended if there are more required functions*/
	private GameState gState = null;

	public TreeGame(GameState gState){
		this.gState = gState;
	}
	
	public GameState getGameState(){
		return gState;
	}
	
	public GameState getCoin(){
		gState.get_coin();
		return gState;
	}

	public GameState loseCoin(){
		gState.lose_coin();
		return gState;
	}
	
}
