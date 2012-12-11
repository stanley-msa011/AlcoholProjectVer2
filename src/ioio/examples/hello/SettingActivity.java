package ioio.examples.hello;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;

public class SettingActivity extends Activity {

	private TabHost tabHost;
	private EditText pwdInput;
	private Button pwdButton;
	private TabHost.TabSpec doctor_pwd;
	private TabHost.TabSpec normal_setting;
	private TabHost.TabSpec doctor_setting;
	private TabHost.TabSpec doctor_page;
	private static final CharSequence password = "1234";
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		tabHost = (TabHost)findViewById(R.id.setting_tab_host);
		tabHost.setup();
		
		normal_setting = tabHost.newTabSpec("Normal_Setting").setContent(R.id.normal_setting).setIndicator("Normal Setting");
		doctor_setting = tabHost.newTabSpec("Doctor_Setting").setContent(R.id.doctor_setting).setIndicator("Doctor Setting");
		doctor_pwd = tabHost.newTabSpec("Doctor_Pwd").setContent(R.id.doctor_pwd).setIndicator("Doctor Setting");
		doctor_page = doctor_pwd;
		tabHost.addTab(normal_setting);
		tabHost.addTab(doctor_page);
		context = this;
		init_pwd_page();
	}

	private void init_pwd_page(){
		pwdInput = (EditText) findViewById(R.id.doctor_setting_pwd);
		pwdButton = (Button) findViewById(R.id.doctor_setting_pwd_button);
		pwdButton.setOnClickListener(new PwdClickListener());
	}
	
	private class PwdClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			CharSequence input_pwd = pwdInput.getText().toString();
			if (password.toString().equals(input_pwd.toString())){
			doctor_page = doctor_setting;
			tabHost.clearAllTabs();
			tabHost.addTab(normal_setting);
			tabHost.addTab(doctor_page);
			tabHost.setCurrentTab(1);
			}
		}
	}

}
