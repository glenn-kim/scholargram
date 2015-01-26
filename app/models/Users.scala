package models

import play.api.libs.json.{JsObject, JsValue, Json, Writes}
import ScholargramTables._
import play.api.db.slick.Config.driver.profile.simple._

/**
 * Created by infinitu on 15. 1. 19.
 */

object Users {
  
  lazy val tQuery = ScholargramTables.Users
  
  abstract case class User(id:Int, name:String){
    val userType:String
    val userDetail:JsObject
  }

  case class Professor(override val id:Int, override val name:String, work:School) extends User(id,name){
    val userType = "professor"
    lazy val userDetail = Json.obj(
      "work"->Json.toJson(work)
    )
  }
  case class Student(override val id:Int, override val name:String, identity:Option[String], major:Major) extends User(id,name){
    val userType = "student"
    lazy val userDetail = Json.obj(
      "identity"->identity,
      "major"->Json.toJson(major)
    ) 
  }

  implicit val userwrites = new Writes[User] {
    override def writes(user: User): JsValue = Json.obj(
      "id" -> user.id,
      "name" -> user.name,
      "user_type" -> user.userType,
      "user_detail" -> user.userDetail
    )
  }
  
  private case class UserJoin(user:Users, prof:Professors, stu:Students, work:Schools, major:Majors, school:Schools)

  def apply(email:String, password:String)={
    val encryptedPassword = encrypt(password)
    
    val userRow = tQuery.filter(_.email === email).filter(_.passwd === encryptedPassword).firstOption
    userRow map getUsers
  }

  private[models] def apply(userid:Int)={
    val userRow = tQuery.filter(_.userid === userid).firstOption
    userRow map getUsers
  }
  
  private def getUsers(row:UsersRow):User = getStudent(row) getOrElse getProfessor(row).get
  
  private lazy val studentJoin =  Students leftJoin Majors on (_.majorid === _.majorid) leftJoin Schools on (_._2.schoolid === _.schoolid)
  private[models] def getStudent(row:UsersRow):Option[Student]={
    (studentJoin filter (_._1._1.userid === row.userid))
      .firstOption
      .map{joinrow=>
        val stu:StudentsRow = joinrow._1._1
        val major:MajorsRow = joinrow._1._2
        val school:SchoolsRow = joinrow._2
        Student(row.userid,row.name,stu.defaultidentity,new Major(major.majorid,major.majorname,school))
      }
  }
  
  private lazy val professorJoin = Professors leftJoin Schools on (_.work === _.schoolid)
  private[models] def getProfessor(row:UsersRow):Option[Professor]={
    (professorJoin filter (_._1.userid === row.userid))
      .firstOption
      .map{joinrow=>
        val prof:ProfessorsRow = joinrow._1
        val work:SchoolsRow = joinrow._2
        Professor(row.userid, row.name, new School(work))
      }
  }
  
  private def encrypt(passwd:String)={
    //todo
    passwd
  }
  
}

class InvalidateParameterException(message:String) extends Exception(message)