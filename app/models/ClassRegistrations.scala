package models


import java.sql.{Date, Timestamp}

import exception.InvalidDataIntegraityException
import models.Users.{User, Professor}
import play.api.libs.json.{Json, JsValue, Writes}
import play.api.db.slick.Config.driver.profile.simple._
import ScholargramTables._


/**
 * Created by infinitu on 15. 1. 22..
 */
object ClassRegistrations {
  
  private lazy val tQuery = ScholargramTables.Classregistrations
  
  case class ClassRegistration( student:User,
                                joined:Timestamp,
                                identity:String,
                                major:String,
                                accepted:Int ){
    def this(user: User, row: ClassregistrationsRow)
      =this(user,row.joineddatetime,row.identity,row.major,row.accepted)
    
  }
  
  implicit lazy val classRegistrationWrites = new Writes[ClassRegistration]{
    import Users.userWrites
    override def writes(reg: ClassRegistration): JsValue = Json.obj(
      "student" -> Json.toJson(reg.student),
      "joined" -> reg.joined,
      "identity" -> reg.identity,
      "major" -> reg.major,
      "accepted" -> reg.accepted
    )
  }
  def apply(user:User, classid:Int)(implicit session : Session)={
    val row = tQuery.filter(_.classid === classid).filter(_.userid === user.id).firstOption
    row map (new ClassRegistration(user,_))
  }

  private lazy val registeredStudents = (tQuery innerJoin ScholargramTables.Classes on (_.classid === _.classid)
                                                innerJoin Users.userDetailQuery on (_._1.userid === _._1.userid))
  def apply(profId:Int, classId:Int)(implicit session : Session) =
    registeredStudents
      .filter(_._1._2.professorid === profId)
      .filter(_._1._1.classid === classId)
      .list
      .map(x=>new ClassRegistration(Users.getUsers(x._2),x._1._1))
  
  def checkPerm(classId:Int,permLevel:Int = 1)(implicit session : Session, student:Users.User)=
    tQuery.filter(t=>t.classid === classId && t.userid === student.id && t.accepted >= permLevel).firstOption.isDefined
}

