package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import ScholargramTables._
import play.api.db.slick.Config.driver.profile.simple._
import models.School._
import models.Major._

/**
 * Created by infinitu on 15. 1. 19.
 */

object Users {
  
  lazy val tQuery = ScholargramTables.Users
  
  abstract case class User(id:Int, name:String, userType:String){
    val userDetail:JsObject
  }

  class Professor(override val id:Int, override val name:String, work:School) extends User(id,name,"professor"){
    lazy val userDetail = Json.obj(
      "work"->Json.toJson(work)
    )
  }
  class Student(override val id:Int, override val name:String, identity:Option[String], major:Major) extends User(id,name,"student"){
    lazy val userDetail = Json.obj(
      "identity"->identity,
      "major"->Json.toJson(major)
    ) 
  }

  implicit val userWrites = new Writes[User] {
    override def writes(user: User): JsValue = Json.obj(
      "id" -> user.id,
      "name" -> user.name,
      "user_type" -> user.userType,
      "user_detail" -> user.userDetail
    )
  }
  implicit val userReads:Reads[User]=(
      (JsPath \ "id").read[Int] and
      (JsPath \ "name").read[String] and
      (JsPath \ "user_type").read[String] and
      (JsPath \ "user_detail").read[JsObject]
    
    ){(id,name,usertype,detail)=>
        val nUser:User = new User(id,name,usertype) {
            override val userDetail: JsObject = detail
          }
        nUser}
  
  private case class UserJoin(user:Users, prof:Professors, stu:Students, work:Schools, major:Majors, school:Schools)

  def apply(email:String, password:String)(implicit session : Session)={
    val encryptedPassword = encrypt(password)
    
    val userRow = tQuery.filter(_.email === email).filter(_.passwd === encryptedPassword).firstOption
    userRow map getUsers
  }

  def apply(userid:Int)(implicit session : scala.slick.jdbc.JdbcBackend#SessionDef)={
    val userRow = tQuery.filter(_.userid === userid).firstOption
    userRow map getUsers
  }
  
  private def getUsers(row:UsersRow)(implicit session : scala.slick.jdbc.JdbcBackend#SessionDef):User = getStudent(row) getOrElse getProfessor(row).get
  
  private lazy val studentJoin =  Students leftJoin Majors on (_.majorid === _.majorid) leftJoin Schools on (_._2.schoolid === _.schoolid)
  private[models] def getStudent(row:UsersRow)(implicit session : scala.slick.jdbc.JdbcBackend#SessionDef):Option[Student]={
    (studentJoin filter (_._1._1.userid === row.userid))
      .firstOption
      .map{joinrow=>
        val stu:StudentsRow = joinrow._1._1
        val major:MajorsRow = joinrow._1._2
        val school:SchoolsRow = joinrow._2
        new Student(row.userid,row.name,stu.defaultidentity,new Major(major.majorid,major.majorname,school))
      }
  }
  
  private lazy val professorJoin = Professors leftJoin Schools on (_.work === _.schoolid)
  private[models] def getProfessor(row:UsersRow)(implicit session : scala.slick.jdbc.JdbcBackend#SessionDef):Option[Professor]={
    (professorJoin filter (_._1.userid === row.userid))
      .firstOption
      .map{joinrow=>
        val prof:ProfessorsRow = joinrow._1
        val work:SchoolsRow = joinrow._2
        new Professor(row.userid, row.name, new School(work))
      }
  }
  
  private def encrypt(passwd:String)={
    //todo
    passwd
  }
  
}

class InvalidateParameterException(message:String) extends Exception(message)