package history.ui;

public class DateValue {
	public int year,month,date;
	
	public DateValue(int year, int month, int date){
		this.year = year;
		this.month = month;
		this.date = date;;
	}
	
	public String toString(){
		return (month+1)+"/"+date;
	}
	
	public String toFileString(){
		return year+"_"+(month+1)+"_"+date;
	}
}
