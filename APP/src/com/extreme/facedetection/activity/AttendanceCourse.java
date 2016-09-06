package com.extreme.facedetection.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.Toast;

import com.extreme.facedetection.R;
import com.extreme.facedetection.apapter.StudentAdapter;
import com.extreme.facedetection.model.Student;
import com.extreme.facedetection.net.ContactServer;

public class AttendanceCourse extends Activity {

	private String courseNumber = "";

	private List<Student> studentList = new ArrayList<Student>();

	private ListView studentListView;

	private Handler loadStudensHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(AttendanceCourse.this, "网络断开连接...",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(AttendanceCourse.this, "没有学生信息",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(AttendanceCourse.this, "导入学生信息成功！",
						Toast.LENGTH_SHORT).show();
				Collections.sort(studentList);
				StudentAdapter adapter = new StudentAdapter(
						AttendanceCourse.this, R.layout.student_item,
						studentList);
				ListView listView = (ListView) findViewById(R.id.student_listview);
				listView.setAdapter(adapter);
				break;
			default:

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_list);
		Intent intent = getIntent();
		courseNumber = intent.getStringExtra("courseNumber");
		LoadStudents();
	}

	private void LoadStudents() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("courseNumber", courseNumber));
				studentList = ContactServer.try2GetStudents(params);

				if (studentList != null) {
					System.out.println(studentList.toString());
					Message msg1 = new Message();
					msg1.what = 1;
					loadStudensHandler.sendMessage(msg1);
				} else {
					Message msg = new Message();
					msg.what = 0;
					loadStudensHandler.sendMessage(msg);
				}

			}
		}).start();
	}

}
