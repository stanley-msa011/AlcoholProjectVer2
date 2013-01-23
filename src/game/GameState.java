package game;

public class GameState {
	/*Total 7 states*/ // state -> therapy stage
	static final public int MIN_STATE = 0;
	static final public int MAX_STATE = 6;
	
	static final public int MIN_COINS = 0;
	static final public int MAX_COINS = 27;
	public int state;
	public int coin;
	
	public GameState(int state, int coin){
		if (state < MIN_STATE)
			state = MIN_STATE;
		else if (state > MAX_STATE)
			state = MAX_STATE;
		this.state = state;
		if (coin < MIN_COINS)
			coin = MIN_COINS;
		else if (coin > MAX_COINS)
			coin = MAX_COINS;
		this.coin = coin;
	}
	public GameState(GameState gs){
		this.state = gs.state;
		this.coin = gs.coin;
	}
	public void get_coin(){
		++coin;
		if (coin > MAX_COINS)
			coin = MAX_COINS;
	}
	
	public void lose_coin(){
		//No lose coin now
	}
}
