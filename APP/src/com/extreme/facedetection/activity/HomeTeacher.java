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
		// 1.��ʼ��tabHost
		tabhost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		tabhost.setup(this, getSupportFragmentManager(),
				R.id.activity_home_container);

		// 2.�½�TabSpec
		TabSpec spec = tabhost.newTabSpec(ADD_COURSE);
		spec.setIndicator(ADD_COURSE);
		addCourseIndictor = new TabIndictorView(this);
		spec.setIndicator(addCourseIndictor);
		addCourseIndictor.setTabTitle("��ӿγ�");
		addCourseIndictor.setTabIcon(R.drawable.new_released,
				R.drawable.new_focused);
		// 3.���TabSpec
		tabhost.addTab(spec, AddCourseFragment.class, bundle);

		// 2.�½�TabSpec
		spec = tabhost.newTabSpec(SELECT_COURSE);
		spec.setIndicator(SELECT_COURSE);
		selectCourseIndictor = new TabIndictorView(this);
		spec.setIndicator(selectCourseIndictor);
		selectCourseIndictor.setTabTitle("�γ̿���");
		selectCourseIndictor.setTabIcon(R.drawable.course_released,
				R.drawable.course_focused);
		// 3.���TabSpec
		tabhost.addTab(spec, CourseTeacherFragment.class, bundle);

		// ȥ���ָ���
		tabhost.getTabWidget().setDividerDrawable(android.R.color.white);
		tabhost.setCurrentTabByTag(ADD_COURSE);
		addCourseIndictor.setTabSelected(true);

		// ����л��¼�,����tabhost��ѡ���¼�;
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
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
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
