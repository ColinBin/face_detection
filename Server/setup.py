# -*- coding: utf-8 -*-

from sqlalchemy import Column, String, create_engine,ForeignKey
from sqlalchemy.orm import sessionmaker,relationship,backref
from sqlalchemy.ext.declarative import declarative_base

Base=declarative_base()

class Teacher(Base):
	__tablename__='teacher'
	
	teacher_number=Column(String(20), primary_key=True)
	password = Column(String(20))
	teacher_name=Column(String(20))
	
class Course(Base):
	__tablename__='course'
	
	course_number=Column(String(20), primary_key=True)
	course_time=Column(String(20))
	course_name=Column(String(20))
	course_location=Column(String(20))
	teacher_number=Column(String(20),ForeignKey('teacher.teacher_number'))
	teacher=relationship(Teacher,backref=backref('courses',uselist=True))
	
	students=relationship('Student',secondary='course_student')
	
class Student(Base):
	__tablename__='student'
	
	student_number=Column(String(20), primary_key=True)
	password=Column(String(20))
	student_name=Column(String(20))
	face_id=Column(String(40))
	
	courses=relationship('Course',secondary='course_student')
	
class CourseStudent(Base):
	__tablename__='course_student'
	student_numbe=Column(String(20), ForeignKey('student.student_number'), primary_key=True)
	course_number=Column(String(20), ForeignKey('course.course_number'), primary_key=True)

engine=create_engine('mysql+mysqlconnector://root:your_password_here@localhost:3306/face_detection')
session=sessionmaker()
session.configure(bind=engine)
Base.metadata.create_all(engine)

s=session()
teacher=Teacher(teacher_number='0001',password='0001',teacher_name='Yue Zhang')
student1=Student(student_number='00000001',password='00000001',student_name='Lei Li')
student2=Student(student_number='00000002',password='00000002',student_name='Meimei Han')
course1=Course(course_number='6001',course_name='Software engineering',course_location='Building1',course_time='2015.9.1')
course2=Course(course_number='6002',course_name='Database Concepts',course_location='Building2',course_time='2016.1.1')

s.add(teacher)
course1.teacher=teacher
course2.teacher=teacher

course1.students.append(student1)
course1.students.append(student2)
course2.students.append(student1)
s.add(student1)
s.add(student2)
s.add(course1)
s.add(course2)

s.commit()

