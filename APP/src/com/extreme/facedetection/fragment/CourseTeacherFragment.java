package com.extreme.facedetection.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.extreme.facedetection.R;
import com.extreme.facedetection.activity.AttendanceCheck;
import com.extreme.facedetection.apapter.CourseAdapter;
import com.extreme.facedetection.model.Course;
import com.extreme.facedetection.net.ContactServer;

public class CourseTeacherFragment extends Fragment {
	private String teacherNumber = "";

	private List<Course> courseList = new ArrayList<Course>();

	private View view;

	private LayoutInflater inflater;

	private ListView courseListView;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(inflater.getContext(), "没有课程",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(inflater.getContext(), "没有课程，请先添加课程",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(inflater.getContext(), "导入课表成功！",
						Toast.LENGTH_SHORT).show();
				Collections.sort(courseList);

				CourseAdapter adapter = new CourseAdapter(
						inflater.getContext(), R.layout.course_item, courseList);
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
		teacherNumber = bundle.getString("teacher_number");
		courseListView = (ListView) view.findViewById(R.id.course_listview);
		courseListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final Course selectedCourse = courseList.get(arg2);
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity());
				dialog.setTitle("开始考勤");
				dialog.setMessage("开始记录考勤?");
				dialog.setCancelable(false);
				dialog.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent toAttendanceCheck = new Intent(getActivity(),
								AttendanceCheck.class);
						toAttendanceCheck.putExtra("courseNumber",
								selectedCourse.getCourseNumber());
						startActivity(toAttendanceCheck);
					}
				});
				dialog.setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				dialog.show();

			}

		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("usertype", "teacher"));
				params.add(new BasicNameValuePair("teacherNumber",
						teacherNumber));
				courseList = ContactServer.try2GetCourses(params);
				if (courseList != null) {
					Message msg1 = new Message();
					msg1.what = 1;
					handler.sendMessage(msg1);
				} else {
					Message msg = new Message();
					msg.what = 0;
					handler.sendMessage(msg);
				}

			}
		}).start();
		return view;
	}

}
