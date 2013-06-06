package history.ui;

public class DateValue {
	public int year,month,date;
	
	public DateValue(int year, int month, int date){
		this.year = year;
		this.month = month;
		this.date = date;;
	}
	
	public String toString(){
		return year+"/"+month+"/"+date;
	}
	
	public String toFileString(){
		return year+"_"+month+"_"+date;
	}
}
