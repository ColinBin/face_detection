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
import com.extreme.facedetection.fragment.AddCourseFragment;
import com.extreme.facedetection.fragment.CourseTeacherFragment;
import com.extreme.facedetection.fragment.TabIndictorView;

public class HomeTeacher extends FragmentActivity implements
		OnTabChangeListener {

	public static final String SELECT_COURSE = "Select Course";
	public static final String ADD_COURSE = "Add Course";

	private FragmentTabHost tabhost;
	private TabIndictorView addCourseIndictor;
	private TabIndictorView selectCourseIndictor;

	private String teacherNumber = "";

	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_teacher);

		Intent intent = getIntent();
		teacherNumber = intent.getStringExtra("teacher_number");

		Bundle bundle = new Bundle();
		bundle.putString("teacher_number", teacherNumber);
		// 1.初始化tabHost
		tabhost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabhost.setup(this, getSupportFragmentManager(),
				R.id.activity_home_container);

		// 2.新建TabSpec
		TabSpec spec = tabhost.newTabSpec(ADD_COURSE);
		spec.setIndicator(ADD_COURSE);
		addCourseIndictor = new TabIndictorView(this);
		spec.setIndicator(addCourseIndictor);
		addCourseIndictor.setTabTitle("添加课程");
		addCourseIndictor.setTabIcon(R.drawable.new_released,
				R.drawable.new_focused);
		// 3.添加TabSpec
		tabhost.addTab(spec, AddCourseFragment.class, bundle);

		// 2.新建TabSpec
		spec = tabhost.newTabSpec(SELECT_COURSE);
		spec.setIndicator(SELECT_COURSE);
		selectCourseIndictor = new TabIndictorView(this);
		spec.setIndicator(selectCourseIndictor);
		selectCourseIndictor.setTabTitle("课程考勤");
		selectCourseIndictor.setTabIcon(R.drawable.course_released,
				R.drawable.course_focused);
		// 3.添加TabSpec
		tabhost.addTab(spec, CourseTeacherFragment.class, bundle);

		// 去掉分割线
		tabhost.getTabWidget().setDividerDrawable(android.R.color.white);
		tabhost.setCurrentTabByTag(ADD_COURSE);
		addCourseIndictor.setTabSelected(true);

		// 点击切换事件,监听tabhost的选中事件;
		tabhost.setOnTabChangedListener(this);

	}

	@Override
	public void onTabChanged(String tag) {
		// TODO Auto-generated method stub

		selectCourseIndictor.setTabSelected(false);
		addCourseIndictor.setTabSelected(false);

		if (tag.equals(SELECT_COURSE)) {
			selectCourseIndictor.setTabSelected(true);
		} else {
			addCourseIndictor.setTabSelected(true);
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
