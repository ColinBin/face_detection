package com.extreme.facedetection.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.extreme.facedetection.R;
import com.extreme.facedetection.apapter.CourseAdapter;
import com.extreme.facedetection.model.Course;
import com.extreme.facedetection.net.ContactServer;

public class AttendanceCheckFragment extends Fragment {
	private View view;

	private String studentNumber = "";

	private List<Course> courseList = new ArrayList<Course>();

	private LayoutInflater inflater;

	private ListView courseListView;

	private Handler loadCoursesHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(view.getContext(), "网络断开连接...",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(view.getContext(), "未加入课程", Toast.LENGTH_SHORT)
						.show();
				break;
			case 1:
				Toast.makeText(view.getContext(), "导入考勤记录成功！",
						Toast.LENGTH_SHORT).show();
				Collections.sort(courseList);
				CourseAdapter adapter = new CourseAdapter(view.getContext(),
						R.layout.course_item, courseList);
				ListView listView = (ListView) view
						.findViewById(R.id.course_listview);
				listView.setAdapter(adapter);
				break;
			default:

			}
		}
	};

	@Override
	public View onCreateView(final LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.course_list, container, false);
		this.inflater = inflater;

		Bundle bundle = this.getArguments();
		studentNumber = bundle.getString("student_number");
		LoadCourses();
		return view;
	}

	private void LoadCourses() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("usertype",
						"studentCheckPersonal"));
				params.add(new BasicNameValuePair("studentNumber",
						studentNumber));
				courseList = ContactServer.try2GetCourses(params);
				if (courseList != null) {
					Message msg1 = new Message();
					msg1.what = 1;
					loadCoursesHandler.sendMessage(msg1);
				} else {
					Message msg = new Message();
					msg.what = 0;
					loadCoursesHandler.sendMessage(msg);
				}

			}
		}).start();
	}

}
