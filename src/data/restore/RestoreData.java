package data.restore;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ubicomp.drunk_detection.activities.PreSettingActivity;

import data.calculate.WeekNum;
import data.database.AudioDB;
import data.database.CleanDB;
import data.database.HistoryDB;
import data.database.QuestionDB;
import data.info.AccumulatedHistoryState;
import data.info.DateBracDetectionState;
import data.info.DateValue;
import data.info.EmotionData;
import data.info.EmotionManageData;
import data.info.QuestionnaireData;
import data.info.StorytellingUsage;
import data.info.UsedDetection;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class RestoreData extends AsyncTask<Void, Void, Void> {

	private String uid;
	private File dir;
	private File zipFile;
	private Context context;
	
	private boolean hasFile = false;
	private SharedPreferences sp;
	private HistoryDB hdb;
	private QuestionDB qdb;
	private AudioDB adb;
	private CleanDB cdb;
	
	public RestoreData(String uid,Context context){
		this.uid = uid;
		this.context = context;
		
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED))
			dir = new File(Environment.getExternalStorageDirectory(),"drunk_detection");
		else
			dir = new File(context.getFilesDir(),"drunk_detection");
		zipFile = new File(dir,uid+".zip");
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		hasFile = zipFile.exists();
		hdb = new HistoryDB(context);
		qdb = new QuestionDB(context);
		adb = new AudioDB(context);
		cdb = new CleanDB(context);
	}
	
	private ProgressDialog dialog = null;
	
	@Override
	protected void onPreExecute(){
		dialog = new ProgressDialog(context);
		dialog.setMessage("回復中");
		dialog.setCancelable(false);
		dialog.show();
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		if (hasFile){
			unzip();
			 restoreAlcoholic();
			 cleanAllDatabase();
			 restoreDetections();
			 restoreUsedDetections();
			 restoreQuestionnaires();
			 restoreAudios();
			 restoreStorytellingUsage();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result){
		if (dialog!=null)
			dialog.dismiss();
		Intent intent = new Intent(context, PreSettingActivity.class);
		context.startActivity(intent);
	}

	
	private void unzip(){
		 try  { 
			 ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile)); 
		     ZipEntry ze = null; 
		     while ((ze = zin.getNextEntry()) != null) {
		    	 if (ze.isDirectory()){
		    		File d = new File(dir+"/"+ze.getName());
		    		d.mkdirs();
		    	 }else{
		    		 File outFile = new File(dir,ze.getName());
		    		 FileOutputStream fout = new FileOutputStream(outFile); 
		      			for (int c = zin.read(); c != -1; c = zin.read()) 
		      				fout.write(c); 
		      		zin.closeEntry(); 
	      			fout.close(); 
		    	 }
		      	} 
		      	zin.close(); 
		    } catch(Exception e) {
		    	Log.d("RESTORE","EXECEPTION: "+e.getMessage());
		    } 
	}
	
	
	private int cleanAllDatabase(){
		cdb.clean();
		return 0;
	}
	
	@SuppressLint("CommitPrefEdits")
	private int  restoreAlcoholic(){
		File f = new File(dir+"/"+uid+"/alcoholic.restore");
		if (f.exists()){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(f))));
				String str = reader.readLine();
				if (str==null)
					Log.d("RESTORE","No Alcoholic Info");
				else{
					str = reader.readLine();
					String[] data = str.split(",");
					
					String[] dateInfo = data[1].split("-");
					int mYear = Integer.valueOf(dateInfo[0]);
					int mMonth = Integer.valueOf(dateInfo[1])-1;
					int mDay = Integer.valueOf(dateInfo[2]);
					
					SharedPreferences.Editor editor= sp.edit();
					editor.putString("uid", data[0]);
					editor.putInt("sYear",mYear );
					editor.putInt("sMonth",mMonth );
					editor.putInt("sDate", mDay);
					editor.commit();
				}
				reader.close();
			} catch (FileNotFoundException e) {
				Log.d("RESTORE","NO ALCOHOLIC FILE");
			} catch (IOException e) {
				Log.d("RESTORE","ALCOHOLIC FILE READ FAIL");
			}
		}
		return 0;
	}
	
	private int  restoreDetections(){
		File f = new File(dir+"/"+uid+"/detection.restore");
		if (f.exists()){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(f))));
				String str = reader.readLine();
				if (str==null)
					Log.d("RESTORE","No Detection Info");
				else{
					while((str = reader.readLine()) !=null){
						String[] data = str.split(",");
						long timestamp = Long.valueOf(data[0])*1000L;
						int week = WeekNum.getWeek(context, timestamp);
						int emotion =Integer.valueOf(data[1]);
						int desire = Integer.valueOf(data[2]);
						float brac = Float.valueOf(data[3]);
						int[] acc_test = new int[3];
						for (int i=0;i<3;++i)
							acc_test[i] = Integer.valueOf(data[4+i]);
						int[] acc_pass= new int[3];
						for (int i=0;i<3;++i)
							acc_pass[i] = Integer.valueOf(data[7+i]);
						int[] t_acc_test = new int[3];
						for (int i=0;i<3;++i)
							t_acc_test[i] = Integer.valueOf(data[10+i]);
						int[] t_acc_pass = new int[3];
						for (int i=0;i<3;++i)
							t_acc_pass[i] = Integer.valueOf(data[13+i]);
						
						AccumulatedHistoryState a_history = new AccumulatedHistoryState(week,acc_test,acc_pass, t_acc_test, t_acc_pass); 
						DateBracDetectionState detection= new DateBracDetectionState(week,timestamp,brac,emotion,desire);
						hdb.insertNewState(detection,a_history);
					}
					hdb.updateAllDetectionUploaded();
				}
				reader.close();
			} catch (FileNotFoundException e) {
				Log.d("RESTORE","NO Detection FILE");
			} catch (IOException e) {
				Log.d("RESTORE","Detection FILE READ FAIL");
			}
		}
		return 0;
	}
	
	private int  restoreUsedDetections(){
		File f = new File(dir+"/"+uid+"/usedDetection.restore");
		if (f.exists()){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(f))));
				String str = reader.readLine();
				if (str==null)
					Log.d("RESTORE","No Used Detection Info");
				else{
					while((str = reader.readLine()) !=null){
						String[] data = str.split(",");
						int[] test = new int[3];
						for (int i=0;i<3;++i)
							test[i] = Integer.valueOf(data[0+i]);
						int[] pass= new int[3];
						for (int i=0;i<3;++i)
							pass[i] = Integer.valueOf(data[3+i]);
						UsedDetection usedDetection = new UsedDetection(test,pass);
						hdb.restoreUsedDetection(usedDetection);
					}
				}
				reader.close();
			} catch (FileNotFoundException e) {
				Log.d("RESTORE","NO Used Detection FILE");
			} catch (IOException e) {
				Log.d("RESTORE","Used Detection FILE READ FAIL");
			}
		}
		return 0;
	}
	
	private int  restoreQuestionnaires(){
		File f;
		EmotionData ed = null;
		EmotionManageData emd = null;
		QuestionnaireData qd = null;
		f = new File(dir+"/"+uid+"/emotionDIY.restore");
		if (f.exists()){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(f))));
				String str = reader.readLine();
				if (str==null)
					Log.d("RESTORE","No Emotion DIY Info");
				else{
					while((str = reader.readLine()) !=null){
						String[] data = str.split(",");
						long ts = Integer.valueOf(data[0])*1000L;
						int[] acc = new int[3];
						for (int i=0;i<3;++i)
							acc[i] = Integer.valueOf(data[1+i]);
						int[] used= new int[3];
						for (int i=0;i<3;++i)
							used[i] = Integer.valueOf(data[4+i]);
						ed = new EmotionData(ts,-1,null,acc,used);
					}
				}
				reader.close();
			} catch (FileNotFoundException e) {
				Log.d("RESTORE","NO Emotion DIY FILE");
			} catch (IOException e) {
				Log.d("RESTORE","Emotion DIY FILE READ FAIL");
			}
		}
		
		f = new File(dir+"/"+uid+"/emotionManage.restore");
		if (f.exists()){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(f))));
				String str = reader.readLine();
				if (str==null)
					Log.d("RESTORE","No Emotion Manage Info");
				else{
					while((str = reader.readLine()) !=null){
						String[] data = str.split(",");
						long ts = Integer.valueOf(data[0])*1000L;
						int[] acc = new int[3];
						for (int i=0;i<3;++i)
							acc[i] = Integer.valueOf(data[1+i]);
						int[] used= new int[3];
						for (int i=0;i<3;++i)
							used[i] = Integer.valueOf(data[4+i]);
						emd = new EmotionManageData(ts,-1,-1,"",acc,used);
					}
				}
				reader.close();
			} catch (FileNotFoundException e) {
				Log.d("RESTORE","NO Emotion Manage FILE");
			} catch (IOException e) {
				Log.d("RESTORE","Emotion Manage FILE READ FAIL");
			}
		}
		
		f = new File(dir+"/"+uid+"/questionnaire.restore");
		if (f.exists()){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(f))));
				String str = reader.readLine();
				if (str==null)
					Log.d("RESTORE","No Questionnaire Info");
				else{
					while((str = reader.readLine()) !=null){
						String[] data = str.split(",");
						long ts = Integer.valueOf(data[0])*1000L;
						int[] acc = new int[12];
						for (int i=0;i<12;++i)
							acc[i] = Integer.valueOf(data[1+i]);
						int[] used= new int[12];
						for (int i=0;i<12;++i)
							used[i] = Integer.valueOf(data[13+i]);
						qd = new QuestionnaireData(ts,-2,"",acc,used);
					}
				}
				reader.close();
			} catch (FileNotFoundException e) {
				Log.d("RESTORE","NO Questionnaire FILE");
			} catch (IOException e) {
				Log.d("RESTORE","Questionnaire FILE READ FAIL");
			}
		}
		qdb.restoreData(ed, emd, qd);
		return 0;
	}
	
	private int  restoreAudios(){
		File f = new File(dir+"/"+uid+"/audio.restore");
		if (f.exists()){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(f))));
				String str = reader.readLine();
				if (str==null)
					Log.d("RESTORE","No Audio Info");
				else{
					while((str = reader.readLine()) !=null){
						String[] data = str.split(",");
						long ts = Integer.valueOf(data[0])*1000L;
						String[] dateInfo = data[1].split("-");
						int year = Integer.valueOf(dateInfo[0]);
						int month = Integer.valueOf(dateInfo[1])-1;
						int date = Integer.valueOf(dateInfo[2]);
						int[] acc = new int[3];
						int[] used = new int[3];
						for (int i=0;i<3;++i){
							acc[i] = Integer.valueOf(data[i+2]);
							used[i] = Integer.valueOf(data[i+5]);
						}
						DateValue dv = new DateValue(year,month,date);
						adb.restoreAudio(dv, ts,acc,used);
						
						File src = new File(dir+"/"+uid+"/audio_records/"+dv.toFileString()+".3gp");
						File audio_dir = new File(dir+"/audio_records");
						if (!audio_dir.exists())
							audio_dir.mkdirs();
						File dst = new File(audio_dir+"/"+dv.toFileString()+".3gp");
						moveFiles(src,dst);
					}
				}
				reader.close();
			} catch (FileNotFoundException e) {
				Log.d("RESTORE","NO Audio FILE");
			} catch (IOException e) {
				Log.d("RESTORE","Used Audio FILE READ FAIL");
			}
		}
		return 0;
	}
	
	private int  restoreStorytellingUsage(){
		File f;
		StorytellingUsage su = null;
		f = new File(dir+"/"+uid+"/storytelling.restore");
		if (f.exists()){
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(f))));
				String str = reader.readLine();
				if (str==null)
					Log.d("RESTORE","No Storytelling Usage Info");
				else{
					while((str = reader.readLine()) !=null){
						String[] data = str.split(",");
						long ts = Integer.valueOf(data[0])*1000L;
						int acc = Integer.valueOf(data[1]);
						int used= Integer.valueOf(data[2]);
						su = new StorytellingUsage(ts,0,acc,used);
					}
				}
				reader.close();
			} catch (FileNotFoundException e) {
				Log.d("RESTORE","NO Storytelling Usage FILE");
			} catch (IOException e) {
				Log.d("RESTORE","Storytelling Usage FILE READ FAIL");
			}
		}
		qdb.restoreData(su);
		return 0;
	}
	
	private void moveFiles(File src, File dst){
		InputStream in;
		try {
			in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		    // Transfer bytes from in to out
		byte[] buf = new byte[4096];
		int len;
		while ((len = in.read(buf)) > 0)
			out.write(buf, 0, len);
		in.close();
		out.close();
		}catch (Exception e) {}
	}
	
}

