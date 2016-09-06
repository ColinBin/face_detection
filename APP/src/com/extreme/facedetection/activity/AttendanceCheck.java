package com.extreme.facedetection.activity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.extreme.facedetection.Config;
import com.extreme.facedetection.R;
import com.extreme.facedetection.net.ContactServer;
import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

public class AttendanceCheck extends Activity implements OnClickListener {

	private Button getPhoto;
	private Button identifyFace;
	private Button courseAttendanceInfo;
	private TextView totalStudentNumberText;
	private TextView attendanceStudentNumberText;

	private String courseNumber = "";
	private int totalStudentNumber = 0;
	private int attendanceStudentNumber = 0;

	private ImageView imageView;
	private Bitmap img = null;
	byte[] imageArray;

	private Boolean isPhotoTaken = false;

	private String face_id;
	private String studentNumber = "";
	private String studentName = "";

	HttpRequests httpRequests = new HttpRequests(Config.API_KEY,
			Config.API_SECRET, true, false);

	private Handler attendanceCheckHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200:

				AttendanceCheckStudent();
				break;
			case -1:
				Toast.makeText(AttendanceCheck.this, "网络连接断开",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:

				JSONObject jsonResult0 = (JSONObject) msg.obj;
				try {
					attendanceStudentNumber = jsonResult0
							.getInt("attendance_student_number");
					attendanceStudentNumberText.setText(String
							.valueOf(attendanceStudentNumber));
					studentName = jsonResult0.getString("student_name");
					Toast.makeText(AttendanceCheck.this,
							"未加入此课程\n" + studentNumber + " " + studentName,
							Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			case 1:
				try {
					JSONObject jsonResult1 = (JSONObject) msg.obj;
					attendanceStudentNumber = jsonResult1
							.getInt("attendance_student_number");
					attendanceStudentNumberText.setText(String
							.valueOf(attendanceStudentNumber));
					studentName = jsonResult1.getString("student_name");
					Toast.makeText(AttendanceCheck.this,
							"签到成功\n" + studentNumber + " " + studentName,
							Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case 2:
				try {
					JSONObject jsonResult2 = (JSONObject) msg.obj;
					attendanceStudentNumber = jsonResult2
							.getInt("attendance_student_number");
					attendanceStudentNumberText.setText(String
							.valueOf(attendanceStudentNumber));
					studentName = jsonResult2.getString("student_name");
					Toast.makeText(AttendanceCheck.this,
							"不能重复签到\n" + studentNumber + " " + studentName,
							Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			default:
				break;
			}
		}
	};

	private Handler courseInfoHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(AttendanceCheck.this, "网络连接断开",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(AttendanceCheck.this, "没有课程，请先添加课程",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(AttendanceCheck.this, "已导入课程信息",
						Toast.LENGTH_SHORT).show();
				JSONObject jsonObject = (JSONObject) msg.obj;
				try {
					totalStudentNumber = jsonObject
							.getInt("total_student_number");
					attendanceStudentNumber = jsonObject
							.getInt("attendance_student_number");
					totalStudentNumberText.setText(String
							.valueOf(totalStudentNumber));
					attendanceStudentNumberText.setText(String
							.valueOf(attendanceStudentNumber));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			default:

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attendance_check);
		Intent intent = getIntent();
		courseNumber = intent.getStringExtra("courseNumber");

		courseAttendanceInfo = (Button) findViewById(R.id.course_attendance_info);
		courseAttendanceInfo.setOnClickListener(this);

		getPhoto = (Button) findViewById(R.id.get_photo_attendance);
		getPhoto.setOnClickListener(this);
		identifyFace = (Button) findViewById(R.id.detect_face_attendance);
		identifyFace.setOnClickListener(this);
		totalStudentNumberText = (TextView) findViewById(R.id.total_student_number);
		attendanceStudentNumberText = (TextView) findViewById(R.id.attendance_student_number);
		imageView = (ImageView) findViewById(R.id.image_attendance);
		GetCourseInfo();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.get_photo_attendance:
			Intent toTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(toTakePhoto, 1);
			break;
		case R.id.detect_face_attendance:
			if (!isPhotoTaken) {
				Toast.makeText(AttendanceCheck.this, "请先拍照", Toast.LENGTH_SHORT)
						.show();
			} else {
				IdentifyFace();
			}
			break;
		case R.id.course_attendance_info:
			Intent toCourseAttendanceInfo = new Intent(AttendanceCheck.this,
					AttendanceCourse.class);
			toCourseAttendanceInfo.putExtra("courseNumber", courseNumber);
			startActivity(toCourseAttendanceInfo);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == Activity.RESULT_OK) {
			isPhotoTaken = true;
			Bundle bundle = intent.getExtras();
			img = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
			imageView.setImageBitmap(img);// 将图片显示在ImageView里
		}
	}

	private void IdentifyFace() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		float scale = Math.min(1,
				Math.min(600f / img.getWidth(), 600f / img.getHeight()));
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		Bitmap imgSmall = Bitmap.createBitmap(img, 0, 0, img.getWidth(),
				img.getHeight(), matrix, false);
		// Log.v(TAG, "imgSmall size : " + imgSmall.getWidth() + " "
		// + imgSmall.getHeight());

		imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		imageArray = stream.toByteArray();
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject identifyResult = null;
				try {
					identifyResult = httpRequests
							.recognitionIdentify(new PostParameters()
									.setGroupName("my_group")
									.setImg(imageArray));
					System.out.println(identifyResult.toString());
					JSONObject bestCandidate;
					JSONArray resultArray;
					resultArray = identifyResult.getJSONArray("face");
					if (resultArray.length() == 0) {
						AttendanceCheck.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(AttendanceCheck.this,
										"识别失败，请重新识别", Toast.LENGTH_SHORT)
										.show();
							}
						});
					} else if (resultArray.length() > 1) {
						AttendanceCheck.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(AttendanceCheck.this,
										"识别到多张人脸\n请重新拍照", Toast.LENGTH_SHORT)
										.show();
							}
						});
					} else {
						bestCandidate = identifyResult.getJSONArray("face")
								.getJSONObject(0).getJSONArray("candidate")
								.getJSONObject(0);

						if (bestCandidate.getDouble("confidence") < 50) {
							AttendanceCheck.this.runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(AttendanceCheck.this,
											"无相似人脸", Toast.LENGTH_SHORT).show();
								}
							});
						} else {
							studentNumber = bestCandidate
									.getString("person_name");
							Message msg = new Message();
							msg.what = 200;
							attendanceCheckHandler.sendMessage(msg);

						}
					}

				} catch (FaceppParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();

	}

	private void GetCourseInfo() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("courseNumber", courseNumber));

				JSONObject jsonObject;
				jsonObject = ContactServer.try2InteractWithServer(4, params);
				if (jsonObject != null) {
					Message msg1 = new Message();
					msg1.what = 1;
					msg1.obj = jsonObject;
					courseInfoHandler.sendMessage(msg1);
				} else {
					Message msg = new Message();
					msg.what = -1;
					courseInfoHandler.sendMessage(msg);
				}

			}
		}).start();
	}

	private void AttendanceCheckStudent() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("courseNumber", courseNumber));
				params.add(new BasicNameValuePair("studentNumber",
						studentNumber));

				JSONObject jsonObject;
				jsonObject = ContactServer.try2InteractWithServer(5, params);
				if (jsonObject != null) {
					System.out.println(jsonObject.toString());
					try {
						Message msg = new Message();
						msg.what = jsonObject.getInt("permission");
						msg.obj = jsonObject;
						attendanceCheckHandler.sendMessage(msg);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Message msg = new Message();
					msg.what = -1;
					attendanceCheckHandler.sendMessage(msg);
				}

			}
		}).start();
	}

}
