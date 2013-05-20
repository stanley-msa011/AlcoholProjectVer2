package test.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

public class QuestionFile {

	private File file;
	private BufferedWriter writer;
	private File directory;
	public QuestionFile(File directory){
		this.directory = directory;
	}
	
	public void write(int emotion,int desire){
		String a = emotion+"\t"+desire;
		file = new File(directory,"question.txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			Log.d("QUESTION WRITER","FAIL TO OPEN");
			writer = null;
		}
		try {
			writer.write(a);
		} catch (IOException e) {	}
		finally{
			try {
				writer.close();
			} catch (IOException e) {	}
		}
	}
	
}
