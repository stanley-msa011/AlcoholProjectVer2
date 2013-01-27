package game;

public class GameState {
	/*Total 7 states*/ // state -> therapy stage
	static final public int MIN_STAGE = 0;
	static final public int MAX_STAGE = 3;
	
	static final public int MIN_COINS = 0;
	static final public int[] MAX_COINS = {34,69,139,139};
	public int stage;
	public int coin;
	
	public GameState(int stage, int coin){
		if (stage < MIN_STAGE)
			stage = MIN_STAGE;
		else if (stage > MAX_STAGE)
			stage = MAX_STAGE;
		this.stage = stage;
		if (coin < MIN_COINS)
			coin = MIN_COINS;
		else if (coin > MAX_COINS[stage])
			coin = MAX_COINS[stage];
		this.coin = coin;
	}
	public GameState(GameState gs){
		this.stage = gs.stage;
		this.coin = gs.coin;
	}
	public void get_coin(){
		++coin;
		if (coin > MAX_COINS[stage])
			coin = MAX_COINS[stage];
	}
	
	public void lose_coin(){
		//No lose coin now
	}
}
