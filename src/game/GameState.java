package game;

public class GameState {
	/*Total 7 states*/
	static final public int MIN_STATE = 0;
	static final public int MAX_STATE = 6;
	/*0~10 coins. If # of coins > 10 => add_state*/
	static final public int MIN_COINS = 0;
	static final public int MAX_COINS = 5;
	public int state;
	public int coin;
	
	public GameState(int state, int coin){
		this.state = state;
		this.coin = coin;
	}
	public GameState(GameState gs){
		this.state = gs.state;
		this.coin = gs.coin;
	}
	public void get_coin(){
		++coin;
		if (coin>MAX_COINS && state!= MAX_STATE){
			++state;
			coin = MIN_COINS;
		}
		else if (coin>MAX_COINS && state==MAX_STATE){
			coin = MAX_COINS;
		}
	}
	
	public void lose_coin(){
		--coin;
		if (coin<MIN_COINS && state!= MIN_STATE){
			--state;
			coin = MAX_COINS;
		}
		else if(coin<MIN_COINS && state == MIN_STATE){
			coin = MIN_COINS;
		}
	}
}
