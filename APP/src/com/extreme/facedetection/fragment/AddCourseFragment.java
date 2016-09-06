package com.extreme.facedetection.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.extreme.facedetection.R;
import com.extreme.facedetection.net.ContactServer;

public class AddCourseFragment extends Fragment implements OnClickListener {

	private View view;

	private String teacherNumber = "";

	private LayoutInflater inflater;

	String courseNumber = "";
	String courseTime = "";
	String courseLocation = "";
	String courseName = "";

	EditText courseNumberText;
	EditText courseTimeText;
	EditText courseLocationText;
	EditText courseNameText;

	Button addCourseConfirm;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(inflater.getContext(), "网络连接断开......",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(inflater.getContext(), "课程编号重复...",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(inflater.getContext(), "成功添加课程!",
						Toast.LENGTH_SHORT).show();
				break;
			default:

			}
		}
	};

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.add_course, container, false);
		this.inflater = inflater;

		Bundle bundle = this.getArguments();
		teacherNumber = bundle.getString("teacher_number");

		courseLocationText = (EditText) view.findViewById(R.id.course_location);
		courseTimeText = (EditText) view.findViewById(R.id.course_time);
		courseNameText = (EditText) view.findViewById(R.id.course_name);
		courseNumberText = (EditText) view.findViewById(R.id.course_number);

		addCourseConfirm = (Button) view.findViewById(R.id.add_course_confirm);
		addCourseConfirm.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_course_confirm:
			courseNumber = courseNumberText.getText().toString().trim();
			courseTime = courseTimeText.getText().toString().trim();
			courseName = courseNameText.getText().toString().trim();
			courseLocation = courseLocationText.getText().toString().trim();
			if (courseNumber.equals("") || courseName.equals("")
					|| courseLocation.equals("") || courseTime.equals("")) {
				Toast.makeText(inflater.getContext(), "课程数据不能为空",
						Toast.LENGTH_SHORT).show();
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
						params.add(new BasicNameValuePair("teacherNumber",
								teacherNumber));
						params.add(new BasicNameValuePair("courseNumber",
								courseNumber));
						params.add(new BasicNameValuePair("courseName",
								courseName));
						params.add(new BasicNameValuePair("courseLocation",
								courseLocation));
						params.add(new BasicNameValuePair("courseTime",
								courseTime));
						JSONObject jsonObject;
						jsonObject = ContactServer.try2InteractWithServer(2,
								params);
						if (jsonObject != null) {
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
