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
  val userDetailQuery = (tQuery
      leftJoin (Students leftJoin Majors on (_.majorid === _.majorid)
                        leftJoin Schools on (_._2.schoolid === _.schoolid) map (x=>(x._1._1,x._1._2,x._2)))
       on (_.userid === _._1.userid)
      leftJoin (Professors leftJoin Schools on (_.work === _.schoolid))
        on (_._1.userid === _._1.userid)
      map{set=>(set._1._1, set._1._2._1.?, set._1._2._2.?, set._1._2._3.?, set._2._1.?, set._2._2.?)})


  abstract case class User(id:Int, name:String, userType:String){
    val userDetail:JsObject
  }

  class Professor(override val id:Int, override val name:String, work:School) extends User(id,name,"professor"){
    def this(user:UsersRow, student:ProfessorsRow, school:SchoolsRow)=
      this(user.userid,user.name,new School(school))
    lazy val userDetail = Json.obj(
      "work"->Json.toJson(work)
    )
  }
  class Student(override val id:Int, override val name:String, identity:Option[String], major:Major) extends User(id,name,"student"){
    def this(user:UsersRow, student:StudentsRow, major:MajorsRow, school:SchoolsRow)=
      this(user.userid,user.name,student.defaultidentity,new Major(major,school))
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
    
    val userRow = userDetailQuery.filter(_._1.email === email).filter(_._1.passwd === encryptedPassword).firstOption
    userRow map getUsers
  }

  def apply(userid:Int)(implicit session : scala.slick.jdbc.JdbcBackend#SessionDef)=
    userDetailQuery.filter(_._1.userid === userid).firstOption.map(getUsers)

  type detailType = (UsersRow,Option[StudentsRow], Option[MajorsRow], Option[SchoolsRow], Option[ProfessorsRow], Option[SchoolsRow])
  private[models] def getUsers(set:detailType)=set match{ case (row:UsersRow,stu:Option[StudentsRow], stu_major:Option[MajorsRow], stu_school:Option[SchoolsRow], prof:Option[ProfessorsRow], prof_work:Option[SchoolsRow])=>
    if(stu.isDefined)
      new Student(row,stu.get,stu_major.get,stu_school.get)
    else
      new Professor(row,prof.get,prof_work.get)
  }
  
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