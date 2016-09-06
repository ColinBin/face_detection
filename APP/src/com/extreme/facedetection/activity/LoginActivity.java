package com.extreme.facedetection.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.extreme.facedetection.R;
import com.extreme.facedetection.net.ContactServer;

public class LoginActivity extends Activity implements OnClickListener {

	private EditText usernameText;
	private EditText passwordText;
	private RadioGroup rgSwitch = null;
	private RadioButton rbTeacher = null;
	private Button loginButton;
	private String username = "";
	private String userType = "student";

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(LoginActivity.this, "网络连接断开", Toast.LENGTH_SHORT)
						.show();
				break;
			case 0:
				Toast.makeText(LoginActivity.this, "用户名或密码错误",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT)
						.show();
				if (userType.equals("teacher")) {
					Intent toTeacherHome = new Intent(LoginActivity.this,
							HomeTeacher.class);
					toTeacherHome.putExtra("teacher_number", username);
					startActivity(toTeacherHome);
					finish();
				} else {
					Intent toStudentHome = new Intent(LoginActivity.this,
							HomeStudent.class);
					toStudentHome.putExtra("student_number", username);
					startActivity(toStudentHome);
					finish();
				}

				break;
			default:

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_login);
		usernameText = (EditText) findViewById(R.id.username_text);
		passwordText = (EditText) findViewById(R.id.password_text);
		loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(this);

		rbTeacher = (RadioButton) findViewById(R.id.rb_teacher);

		rgSwitch = (RadioGroup) findViewById(R.id.rg_switch);
		rgSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == rbTeacher.getId()) {
					userType = "teacher";
				} else {
					userType = "student";
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_button:
			username = usernameText.getText().toString().trim();
			final String password = passwordText.getText().toString().trim();

			if (username.equals("")) {
				Toast.makeText(LoginActivity.this, "Account can't be empty",
						Toast.LENGTH_SHORT).show();
			} else if (password.equals("")) {
				Toast.makeText(LoginActivity.this, "Password is invalid",
						Toast.LENGTH_SHORT).show();
			} else {
				new Thread(new Runnable() {

					@Override
					public void run() {
						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair("username", username));
						params.add(new BasicNameValuePair("password", password));
						params.add(new BasicNameValuePair("usertype", userType));
						JSONObject jsonObject;
						jsonObject = ContactServer.try2InteractWithServer(1,
								params);
						if (jsonObject != null) {
							System.out.println(jsonObject.toString());
							try {
								Message msg = new Message();
								msg.what = jsonObject.getInt("permission");
								handler.sendMessage(msg);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							Message msg = new Message();
							msg.what = -1;
							handler.sendMessage(msg);
						}

					}
				}).start();

			}
			break;
		default:
			break;
		}

	}
}
