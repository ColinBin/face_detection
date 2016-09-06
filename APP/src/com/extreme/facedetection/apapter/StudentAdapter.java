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
import com.extreme.facedetection.model.Student;

public class StudentAdapter extends ArrayAdapter<Student> {
	private int resourceId;

	public StudentAdapter(Context context, int textViewResourceId,
			List<Student> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Student currentStudent = getItem(position);// 实例化
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);

		TextView studentNumber = (TextView) view
				.findViewById(R.id.student_number);
		TextView studentName = (TextView) view.findViewById(R.id.student_name);
		TextView attendanceInfo = (TextView) view
				.findViewById(R.id.attendance_info);

		studentNumber.setText(currentStudent.getStudentNumber());
		studentName.setText(currentStudent.getStudentName());
		if (currentStudent.getExtraInfo().equals("checked")) {
			attendanceInfo.setText("已签到");
			attendanceInfo.setTextColor(Color.BLUE);
		} else if (currentStudent.getExtraInfo().equals("not checked")) {
			attendanceInfo.setText("未签到");
			attendanceInfo.setTextColor(Color.RED);
		} else {
			attendanceInfo.setText(currentStudent.getExtraInfo());
		}
		return view;

	}
}