package ioio.examples.hello;

import game.statistic.StatisticAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import database.TimeBlockDB;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class BracStatisticActivity extends Activity {

	private GridView label_y;
	private GridView label_x;
	private GridView content;
	
	private TimeBlockDB tDB;
	
	private SimpleAdapter y_adapter,x_adapter;
	private StatisticAdapter content_adapter;
	private ArrayList<HashMap<String,Object>> y_list,x_list,content_list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brac_statistic);
		
		tDB = new TimeBlockDB(this);
		
		initY();
		initX();
		initContent();
	}
	
	private void initY(){
		label_y = (GridView) this.findViewById(R.id.statistic_label_y);
		y_list = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> item;
		item= new HashMap<String,Object>();
		item.put("label", "晚上");
		y_list.add(item);
		item = new HashMap<String,Object>();
		item.put("label", "下午");
		y_list.add(item);
		item = new HashMap<String,Object>();
		item.put("label", "中午");
		y_list.add(item);
		item = new HashMap<String,Object>();
		item.put("label", "早上");
		y_list.add(item);
		y_adapter = new SimpleAdapter(this,y_list,R.layout.statistic_label_y,new String[]{"label"},new int[]{R.id.statistic_label_y_t});
		label_y.setAdapter(y_adapter);
		y_adapter.notifyDataSetChanged();
	}
	private void initX(){
		label_x = (GridView) this.findViewById(R.id.statistic_label_x);
		x_list = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> item;
		Calendar cal = Calendar.getInstance();
		long cal_milli = cal.getTimeInMillis();
		
		for (int i=0;i<6;++i){
			item= new HashMap<String,Object>();
			long old_milli = cal_milli + (i-6)*86400000L;
			Calendar old_cal = Calendar.getInstance();
			old_cal.setTimeInMillis(old_milli);
			int month = old_cal.get(Calendar.MONTH) + 1;
			int date = old_cal.get(Calendar.DATE);
			String d = month+"\n月\n"+date+"\n日";
			item.put("label", d);
			x_list.add(item);
		}
		int month = cal.get(Calendar.MONTH)+1;
		int date = cal.get(Calendar.DATE);
		item= new HashMap<String,Object>();
		String d = month+"\n月\n"+date+"\n日";
		item.put("label", d);
		x_list.add(item);
		x_adapter = new SimpleAdapter(this,x_list,R.layout.statistic_label_x,new String[]{"label"},new int[]{R.id.statistic_label_x_t});
		label_x.setAdapter(x_adapter);
		x_adapter.notifyDataSetChanged();
	}
	private void initContent(){
		content = (GridView) this.findViewById(R.id.statistic_content);
		content_list = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> item;
		Calendar cal = Calendar.getInstance();
		long cal_milli = cal.getTimeInMillis();
		for (int c = 3; c>=0;--c){
			for (int i=0;i<7;++i){
				item= new HashMap<String,Object>();
				long old_milli = cal_milli + (i-6)*86400000L;
				Calendar old = Calendar.getInstance();
				old.setTimeInMillis(old_milli);
				int year = old.get(Calendar.YEAR);
				int month = old.get(Calendar.MONTH)+1;
				int date = old.get(Calendar.DATE);
				int block = c;
				boolean exist = tDB.checkSameTimeBlock(year,month,date,block);
				item.put("exist", exist);
				content_list.add(item);
			}
		}
		content_adapter = new StatisticAdapter(content_list,this);
		content.setAdapter(content_adapter);
		content_adapter.notifyDataSetChanged();
	}

}
