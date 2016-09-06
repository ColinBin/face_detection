package com.extreme.facedetection.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.extreme.facedetection.apapter.CourseAdapter;
import com.extreme.facedetection.model.Course;
import com.extreme.facedetection.net.ContactServer;

public class CourseStudentFragment extends Fragment {

	private List<Course> courseList = new ArrayList<Course>();

	private LayoutInflater inflater;

	private View view;

	private String studentNumber = "";
	private ListView courseListView;

	private Handler loadCoursesHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(inflater.getContext(), "网络断开连接...",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(inflater.getContext(), "没有课程可选",
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

	private Handler joinCourseHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(inflater.getContext(), "网络断开连接...",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(inflater.getContext(), "不能重复加入课程",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(inflater.getContext(), "加入课程成功！",
						Toast.LENGTH_SHORT).show();
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

		courseListView = (ListView) view.findViewById(R.id.course_listview);
		courseListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final Course selectedCourse = courseList.get(arg2);

				AlertDialog.Builder dialog = new AlertDialog.Builder(view
						.getContext());
				dialog.setTitle("加入课程");
				dialog.setMessage("确认加入编号为 " + selectedCourse.getCourseNumber()
						+ " 的课程?");
				dialog.setCancelable(true);

				dialog.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								JoinCourse(selectedCourse);
							}
						});

				dialog.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				dialog.show();

			}
		});

		LoadCourses();

		return view;

	}

	private void JoinCourse(final Course selectedCourse) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("studentNumber",
						studentNumber));
				params.add(new BasicNameValuePair("courseNumber",
						selectedCourse.getCourseNumber()));
				JSONObject jsonObject;
				jsonObject = ContactServer.try2InteractWithServer(3, params);
				if (jsonObject != null) {
					try {
						Message msg = new Message();
						msg.what = jsonObject.getInt("permission");
						joinCourseHandler.sendMessage(msg);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Message msg = new Message();
					msg.what = -1;
					joinCourseHandler.sendMessage(msg);
				}

			}
		}).start();
	}

	private void LoadCourses() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("usertype", "studentCheckAll"));
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
