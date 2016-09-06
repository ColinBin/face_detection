package com.extreme.facedetection.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.extreme.facedetection.Config;
import com.extreme.facedetection.model.Course;
import com.extreme.facedetection.model.Student;

public class ContactServer {

	private static List<Course> Analysis(JSONObject jsonAll)
			throws JSONException {
		// ��ʼ��list�������
		List<Course> list = new ArrayList<Course>();

		JSONArray jsonArray = jsonAll.getJSONArray("courses");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			// ��ʼ��map�������
			Course currentCourse = new Course();
			currentCourse
					.setCourseNumber(jsonObject.getString("course_number"));
			currentCourse.setCourseTime(jsonObject.getString("course_time"));
			currentCourse.setCourseLocation(jsonObject
					.getString("course_location"));
			currentCourse.setCourseName(jsonObject.getString("course_name"));
			currentCourse.setExtraInfo(jsonObject.getString("extra_info"));
			currentCourse.setTeacherName(jsonObject.getString("teacher_name"));
			list.add(currentCourse);

		}
		return list;
	}

	public static List<Course> try2GetCourses(List<BasicNameValuePair> params) {
		HttpClient httpClient = new DefaultHttpClient();
		List<Course> list = new ArrayList<Course>();
		String validateUrl = "";
		validateUrl = Config.BASE_URL + "getcourses";
		// �������ӳ�ʱ
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);

		// ���ö�ȡ��ʱ
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				5000);

		HttpPost httpRequst = new HttpPost(validateUrl);

		try {
			// ��������
			httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// �õ���Ӧ
			HttpResponse response = httpClient.execute(httpRequst);

			// ����ֵ���Ϊ200�Ļ���֤���ɹ��ĵõ�������
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				// ���õ������ݽ��н���
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));

				for (String s = buffer.readLine(); s != null; s = buffer
						.readLine()) {
					builder.append(s);

				}
				JSONObject jsonObject = new JSONObject(builder.toString());

				if (jsonObject.getInt("permission") == 0) {
					return null;
				} else {
					list = Analysis(jsonObject);
					return list;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}

		return list;

	}

	private static List<Student> AnalysisStudent(JSONObject jsonAll)
			throws JSONException {
		// ��ʼ��list�������
		List<Student> list = new ArrayList<Student>();

		JSONArray jsonArray = jsonAll.getJSONArray("students");

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			// ��ʼ��map�������
			Student currentStudent = new Student();
			currentStudent.setStudentName(jsonObject.getString("student_name"));
			currentStudent.setStudentNumber(jsonObject
					.getString("student_number"));
			currentStudent.setExtraInfo(jsonObject.getString("extra_info"));
			list.add(currentStudent);

		}
		return list;
	}

	public static List<Student> try2GetStudents(List<BasicNameValuePair> params) {
		HttpClient httpClient = new DefaultHttpClient();
		List<Student> list = new ArrayList<Student>();
		String validateUrl = "";
		validateUrl = Config.BASE_URL + "getstudents";
		// �������ӳ�ʱ
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);

		// ���ö�ȡ��ʱ
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				5000);

		HttpPost httpRequst = new HttpPost(validateUrl);

		try {
			// ��������
			httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// �õ���Ӧ
			HttpResponse response = httpClient.execute(httpRequst);

			// ����ֵ���Ϊ200�Ļ���֤���ɹ��ĵõ�������
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				// ���õ������ݽ��н���
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));

				for (String s = buffer.readLine(); s != null; s = buffer
						.readLine()) {
					builder.append(s);

				}
				JSONObject jsonObject = new JSONObject(builder.toString());
				System.out
						.println("in contact server:" + jsonObject.toString());
				if (jsonObject.getInt("permission") == 0) {
					return null;
				} else {
					list = AnalysisStudent(jsonObject);
					return list;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}

		return list;

	}

	public static JSONObject try2InteractWithServer(int type,
			List<BasicNameValuePair> params) {

		HttpClient httpClient = new DefaultHttpClient();

		String validateUrl = "";
		switch (type) {
		case 1:
			validateUrl = Config.BASE_URL + "login";
			break;
		case 2:
			validateUrl = Config.BASE_URL + "addcourse";
			break;
		case 3:
			validateUrl = Config.BASE_URL + "joincourse";
			break;
		case 4:
			validateUrl = Config.BASE_URL + "getcourseinfo";
			break;
		case 5:
			validateUrl = Config.BASE_URL + "attendancecheck";
			break;
		default:
			return null;
		}

		// �������ӳ�ʱ
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);

		// ���ö�ȡ��ʱ
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				5000);

		HttpPost httpRequst = new HttpPost(validateUrl);
		// HttpGet httpGet = new HttpGet(validateUrl);
		try {
			// ��������
			httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// �õ���Ӧ
			HttpResponse response = httpClient.execute(httpRequst);

			// ����ֵ���Ϊ200�Ļ���֤���ɹ��ĵõ�������
			if (response.getStatusLine().getStatusCode() == 200) {
				StringBuilder builder = new StringBuilder();
				// ���õ������ݽ��н���
				BufferedReader buffer = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));

				for (String s = buffer.readLine(); s != null; s = buffer
						.readLine()) {
					builder.append(s);

				}
				// �õ�Json����
				JSONObject jsonObject = new JSONObject(builder.toString());

				return jsonObject;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}

		return null;
	}
}
