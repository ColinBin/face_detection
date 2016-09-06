package com.extreme.facedetection.fragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.extreme.facedetection.R;

public class TabIndictorView extends RelativeLayout {

	private ImageView ivTabIcon;
	private TextView tvTabHint;
	private TextView tvTabUnRead;

	private int normalIconId;
	private int focusIconId;

	public TabIndictorView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public TabIndictorView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View.inflate(context, R.layout.tab_indictor, this);
		// System.out.println("yes");

		ivTabIcon = (ImageView) findViewById(R.id.tab_indicator_icon);
		tvTabHint = (TextView) findViewById(R.id.tab_indicator_hint);

		// setTabUnreadCount(0);
	}

	public void setTabTitle(String title) {
		tvTabHint.setText(title);
	}

	public void setTabTitle(int titleId) {
		tvTabHint.setText(titleId);
	}

	public void setTabIcon(int normalIconId, int focusIconId) {
		this.focusIconId = focusIconId;
		this.normalIconId = normalIconId;

		ivTabIcon.setImageResource(normalIconId);
	}

	public void setTabSelected(boolean selected) {
		setSelected(selected);
		if (selected) {
			ivTabIcon.setImageResource(focusIconId);
		} else {
			ivTabIcon.setImageResource(normalIconId);
		}
	}

}
