# -*- coding: utf-8 -*-
import tornado.ioloop
import tornado.web
from sqlalchemy import Column, String,Integer,create_engine,ForeignKey
from sqlalchemy.orm import sessionmaker,relationship,backref
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class Teacher(Base):
	__tablename__='teacher'
	
	teacher_number=Column(String(20), primary_key=True)
	password = Column(String(20))
	teacher_name=Column(String(20))
	def as_dict(self):
		return {c.name:getattr(self,c.name) for c in self.__table__.columns}
		
class Course(Base):
	__tablename__='course'
	
	course_number=Column(String(20), primary_key=True)
	course_time=Column(String(20))
	course_name=Column(String(20))
	course_location=Column(String(20))
	teacher_number=Column(String(20),ForeignKey('teacher.teacher_number'))
	teacher=relationship(Teacher,backref=backref('courses',uselist=True))
	attendance_list=Column(String(1000))
	attendance_number=Column(Integer)
	students=relationship('Student',secondary='course_student')
	def as_dict(self):
		return {c.name:getattr(self,c.name) for c in self.__table__.columns}
	
class Student(Base):
	__tablename__='student'
	
	student_number=Column(String(20), primary_key=True)
	password=Column(String(20))
	student_name=Column(String(20))
	
	courses=relationship('Course',secondary='course_student')
	def as_dict(self):
		return {c.name:getattr(self,c.name) for c in self.__table__.columns}
	
class CourseStudent(Base):
	__tablename__='course_student'
	student_numbe=Column(String(20), ForeignKey('student.student_number'), primary_key=True)
	course_number=Column(String(20), ForeignKey('course.course_number'), primary_key=True)

		
class LoginHandler(tornado.web.RequestHandler):		
	def post(self):
		session=get_session()
		userType=self.get_argument("usertype")
		if(userType=="student"):
			password=self.get_argument("password")
			student_number=self.get_argument("username")
			student=session.query(Student).filter(Student.student_number==student_number).first()
			if student is not None and student.password==password:
				self.finish({"permission":1})
			else:
				self.finish({"permission":0})
		else:
			password=self.get_argument("password")
			teacher_number=self.get_argument("username")
			teacher=session.query(Teacher).filter(Teacher.teacher_number==teacher_number).first()
			if teacher is not None and teacher.password==password:
				self.finish({"permission":1})
			else:
				self.finish({"permission":0})
		session.close()

				
class AddCourse(tornado.web.RequestHandler):
	def post(self):
		session=get_session()
		teacher_number=self.get_argument("teacherNumber")
		course_number=self.get_argument("courseNumber")
		course_time=self.get_argument("courseTime")
		course_location=self.get_argument("courseLocation")
		course_name=self.get_argument("courseName")
		allCourses=session.query(Course).all()
		jsonResult={}
		for course in allCourses:
			if(course.course_number==course_number):
				jsonResult["permission"]=0
				break
		else:
			jsonResult["permission"]=1
			teacher=session.query(Teacher).filter(Teacher.teacher_number==teacher_number).first()
			newCourse=Course(course_number=course_number,course_time=course_time,course_name=course_name,course_location=course_location,attendance_number=0,attendance_list="")
			newCourse.teacher=teacher
			session.add(newCourse)
			session.commit()
			session.close()
		self.finish(jsonResult)
		
class GetCourses(tornado.web.RequestHandler):
	def post(self):
		session=get_session()
		user_type=self.get_argument("usertype")
		if user_type=='teacher':
			teacher_number=self.get_argument("teacherNumber")
			teacher=session.query(Teacher).filter(Teacher.teacher_number==teacher_number).first()
			courses=teacher.courses
			if len(courses)!=0:
				jsonResult={}
				jsonResult["permission"]=1
				courseList=[]
				for course in courses:
					currentCourse=course.as_dict()
					currentCourse['teacher_name']=teacher.teacher_name
					currentCourse["extra_info"]=""
					courseList.append(currentCourse)
				jsonResult["courses"]=courseList
			else:
				jsonResult={}
				jsonResult["permission"]=0
			session.close()
			self.finish(jsonResult)
		elif user_type=='studentCheckAll':
			student_number=self.get_argument("studentNumber")
			student=session.query(Student).filter(Student.student_number==student_number).first()
			courses=session.query(Course).all()
			if len(courses)!=0:
				jsonResult={}
				jsonResult["permission"]=1
				courseList=[]
				for course in courses:
					currentCourse=course.as_dict()
					currentCourse["teacher_name"]=course.teacher.teacher_name
					if student in course.students:
						currentCourse["extra_info"]="in"
					else:
						currentCourse["extra_info"]="not in"
					courseList.append(currentCourse)
				jsonResult["courses"]=courseList
			else:
				jsonResult={}
				jsonResult["permission"]=0
			session.close()
			self.finish(jsonResult)
		else:
			student_number=self.get_argument("studentNumber")
			student=session.query(Student).filter(Student.student_number==student_number).first()
			courses=student.courses
			if len(courses)!=0:
				jsonResult={}
				jsonResult["permission"]=1
				courseList=[]
				for course in courses:
					currentCourse=course.as_dict()
					currentCourse["teacher_name"]=course.teacher.teacher_name
					if student_number in course.attendance_list:
						currentCourse["extra_info"]="checked"
					else:
						currentCourse["extra_info"]="not checked"
					courseList.append(currentCourse)
				jsonResult["courses"]=courseList
			else:
				jsonResult={}
				jsonResult["permission"]=0
			self.finish(jsonResult)
				
			
			
class JoinCourse(tornado.web.RequestHandler):
	def post(self):
		session=get_session()
		student_number=self.get_argument("studentNumber")
		course_number=self.get_argument("courseNumber")
		student=session.query(Student).filter(Student.student_number==student_number).first()
		courses=student.courses
		jsonResult={}
		for course in courses:
			if course.course_number==course_number:
				jsonResult["permission"]=0
				break
		else:
			course=session.query(Course).filter(Course.course_number==course_number).first()
			course.students.append(student)
			jsonResult["permission"]=1
			session.commit()
		self.finish(jsonResult)
	
class GetCourseInfo(tornado.web.RequestHandler):
	def post(self):
		session=get_session()
		course_number=self.get_argument("courseNumber")
		course=session.query(Course).filter(Course.course_number==course_number).first()
		students=course.students
		jsonObject={}
		jsonObject["total_student_number"]=len(students)
		jsonObject["attendance_student_number"]=course.attendance_number
		self.finish(jsonObject)
		
	
class AttendanceCheck(tornado.web.RequestHandler):
	def post(self):
		session=get_session()
		course_number=self.get_argument("courseNumber")
		student_number=self.get_argument("studentNumber")
		course=session.query(Course).filter(Course.course_number==course_number).first()
		student=session.query(Student).filter(Student.student_number==student_number).first()
		students=course.students
		jsonResult={}
		for currentStudent in students:
			if currentStudent.student_number==student_number:
				attendance_list=course.attendance_list.encode("utf-8")
				if student_number not in attendance_list:
					attendance_list=attendance_list+student_number+","
					course.attendance_number=course.attendance_number+1
					course.attendance_list=attendance_list
					jsonResult["attendance_student_number"]=course.attendance_number
					jsonResult["student_name"]=student.student_name
					session.commit()
					jsonResult["permission"]=1
					
					break
				else:
					jsonResult["permission"]=2
					jsonResult["attendance_student_number"]=course.attendance_number
					jsonResult["student_name"]=student.student_name
					break
		else:
			jsonResult["permission"]=0
			jsonResult["attendance_student_number"]=course.attendance_number
			jsonResult["student_name"]=student.student_name
		self.finish(jsonResult)

class GetStudents(tornado.web.RequestHandler):
	def post(self):
		session=get_session()
		course_number=self.get_argument("courseNumber")
		course=session.query(Course).filter(Course.course_number==course_number).first()
		students=course.students
		jsonResult={}
		if len(students)!=0:
			jsonResult["permission"]=1
			studentList=[]
			attendance_list=course.attendance_list.encode("utf-8")
			for student in students:
				currentStudent=student.as_dict()
				if student.student_number in attendance_list:
					currentStudent["extra_info"]="checked"
				else:
					currentStudent["extra_info"]="not checked"
				studentList.append(currentStudent)
			jsonResult["students"]=studentList
		else:
			jsonResult["permission"]=0
		self.finish(jsonResult)
		
application = tornado.web.Application([
	(r"/login",LoginHandler),
	(r"/addcourse",AddCourse),
	(r"/getcourses",GetCourses),
	(r"/joincourse",JoinCourse),
	(r"/getcourseinfo",GetCourseInfo),
	(r"/attendancecheck",AttendanceCheck),
	(r"/getstudents",GetStudents),
])
def get_session():
    engine = create_engine('mysql+mysqlconnector://root:your_password_here@localhost:3306/anywhere')
    DBSession = sessionmaker(bind=engine)
    session = DBSession()
    return session

if __name__ == "__main__":
    application.listen(8885)
    tornado.ioloop.IOLoop.instance().start()