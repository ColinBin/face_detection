package com.extreme.facedetection.apapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.extreme.facedetection.R;
import com.extreme.facedetection.model.Course;

public class CourseAdapter extends ArrayAdapter<Course> {
	private int resourceId;

	public CourseAdapter(Context context, int textViewResourceId,
			List<Course> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Course currentCourse = getItem(position);// ʵ����
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

		TextView courseNumber = (TextView) view
				.findViewById(R.id.course_number);
		TextView teacherName = (TextView) view.findViewById(R.id.teacher_name);
		TextView courseName = (TextView) view.findViewById(R.id.course_name);
		TextView courseTime = (TextView) view.findViewById(R.id.course_time);
		TextView courseLocation = (TextView) view
				.findViewById(R.id.course_location);
		TextView extraInfo = (TextView) view.findViewById(R.id.extra_info);

		courseNumber.setText(currentCourse.getCourseNumber());
		courseName.setText(currentCourse.getCourseName());
		teacherName.setText(currentCourse.getTeacherName());
		courseTime.setText(currentCourse.getCourseTime());
		courseLocation.setText(currentCourse.getCourseLocation());
		if (currentCourse.getExtraInfo().equals("in")) {
			extraInfo.setText("�Ѽ���ÿγ�");
			extraInfo.setTextColor(Color.GREEN);
		} else if (currentCourse.getExtraInfo().equals("not in")) {
			extraInfo.setText("δ����ÿγ�");
			extraInfo.setTextColor(Color.BLUE);
		} else if (currentCourse.getExtraInfo().equals("checked")) {
			extraInfo.setText("��ǩ��");
			extraInfo.setTextColor(Color.BLUE);
		} else if (currentCourse.getExtraInfo().equals("not checked")) {
			extraInfo.setText("δǩ��");
			extraInfo.setTextColor(Color.RED);
		} else {
			extraInfo.setText(currentCourse.getExtraInfo());
		}
		return view;

	}
}
