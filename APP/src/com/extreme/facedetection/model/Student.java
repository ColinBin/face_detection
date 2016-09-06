package com.extreme.facedetection.model;

public class Student implements Comparable<Student> {
	private String studentNumber;
	private String studentName;
	private String extraInfo;

	public String getStudentNumber() {
		return studentNumber;
	}

	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	@Override
	public int compareTo(Student another) {
		return Integer.valueOf(this.getStudentNumber()).compareTo(
				Integer.valueOf(another.getStudentNumber()));
	}

}
