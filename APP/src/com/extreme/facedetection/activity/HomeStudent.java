package com.extreme.facedetection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.extreme.facedetection.R;
import com.extreme.facedetection.fragment.AttendanceCheckFragment;
import com.extreme.facedetection.fragment.CollectFaceDataFragment;
import com.extreme.facedetection.fragment.CourseStudentFragment;
import com.extreme.facedetection.fragment.TabIndictorView;

public class HomeStudent extends FragmentActivity implements
		OnTabChangeListener {

	public static final String SELECT_COURSE = "Select Course";
	public static final String ATTENDANCE_RECORD = "Attendance Record";
	public static final String FACE_DETECT = "Face Detect";

	private FragmentTabHost tabhost;
	private TabIndictorView selectCourseIndictor;
	private TabIndictorView attendanceRecordIndictor;
	private TabIndictorView faceDetectIndictor;

	private String studentNumber = "";

	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_student);

		Intent intent = getIntent();
		studentNumber = intent.getStringExtra("student_number");

		Bundle bundle = new Bundle();
		bundle.putString("student_number", studentNumber);
		// 1.初始化tabHost
		tabhost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabhost.setup(this, getSupportFragmentManager(),
				R.id.activity_home_container);

		// 2.新建TabSpec
		TabSpec spec = tabhost.newTabSpec(SELECT_COURSE);
		spec.setIndicator(SELECT_COURSE);
		selectCourseIndictor = new TabIndictorView(this);
		spec.setIndicator(selectCourseIndictor);
		selectCourseIndictor.setTabTitle("加入课程");
		selectCourseIndictor.setTabIcon(R.drawable.list_released,
				R.drawable.list_focused);
		// 3.添加TabSpec
		tabhost.addTab(spec, CourseStudentFragment.class, bundle);

		// 2.新建TabSpec
		spec = tabhost.newTabSpec(ATTENDANCE_RECORD);
		spec.setIndicator(ATTENDANCE_RECORD);
		attendanceRecordIndictor = new TabIndictorView(this);
		spec.setIndicator(attendanceRecordIndictor);
		attendanceRecordIndictor.setTabTitle("考勤记录");
		attendanceRecordIndictor.setTabIcon(R.drawable.record_released,
				R.drawable.record_focused);
		// 3.添加TabSpec
		tabhost.addTab(spec, AttendanceCheckFragment.class, bundle);

		// 2.新建TabSpec
		spec = tabhost.newTabSpec(FACE_DETECT);
		spec.setIndicator(FACE_DETECT);
		faceDetectIndictor = new TabIndictorView(this);
		spec.setIndicator(faceDetectIndictor);
		faceDetectIndictor.setTabTitle("特征提取");
		faceDetectIndictor.setTabIcon(R.drawable.face_released,
				R.drawable.face_focused);
		// 3.添加TabSpec
		tabhost.addTab(spec, CollectFaceDataFragment.class, bundle);

		// 去掉分割线
		tabhost.getTabWidget().setDividerDrawable(android.R.color.white);
		tabhost.setCurrentTabByTag(SELECT_COURSE);
		selectCourseIndictor.setTabSelected(true);

		// 点击切换事件,监听tabhost的选中事件;
		tabhost.setOnTabChangedListener(this);

	}

	@Override
	public void onTabChanged(String tag) {
		// TODO Auto-generated method stub

		selectCourseIndictor.setTabSelected(false);
		attendanceRecordIndictor.setTabSelected(false);
		faceDetectIndictor.setTabSelected(false);

		if (tag.equals(SELECT_COURSE)) {
			selectCourseIndictor.setTabSelected(true);
		} else if (tag.equals(FACE_DETECT)) {
			faceDetectIndictor.setTabSelected(true);
		} else {
			attendanceRecordIndictor.setTabSelected(true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
