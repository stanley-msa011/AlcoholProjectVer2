package data.info;

public class StorytellingUsage implements SelfHelpCounter{
	public long ts;
	public int daily_usage = 0;
	public int acc = 0;
	public int used = 0;
	public String name;
	public int minutes;
	
	public StorytellingUsage(long ts, int daily_usage, int acc, int used, String name, int minutes){
		this.ts = ts;
		this.daily_usage = daily_usage;
		this.acc = acc;
		this.used = used;
		this.name = name;
		this.minutes = minutes;
	}

	@Override
	public int getSelfHelpCounter() {
		return acc - used;
	}
}
