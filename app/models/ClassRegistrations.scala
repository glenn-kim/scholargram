package models


import java.sql.Timestamp

import controllers.ClassRegistrationController
import exception.{AlreadyRegistrated, DoesNotHavePermissionException, NoSuchRowException}
import models.Users.User
import play.api.libs.json.{JsValue, Json, Writes}
import models.ScholargramTables._
import models.ScholargramTables.profile.simple._

import scala.slick.jdbc.JdbcBackend


/**
 * Created by infinitu on 15. 1. 22..
 */
object ClassRegistrations {
  type Session = JdbcBackend#SessionDef
  
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
    import models.Users.userWrites
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
  
  def accept(classId:Int, form:ClassRegistrationController.AcceptForm)(implicit session:Session, prof:Users.User){
    if(!Classes.checkPerm(classId))
      throw new DoesNotHavePermissionException("Does not have permission to accept")
    val q = tQuery.filter(t=> t.classid === classId && t.userid === form.studentId)
    val sqlResult = form match{
      case ClassRegistrationController.AcceptForm(sid,-1)=>
        q.delete
      case _=>
        q.map(_.accepted).update(form.acceptLevel)
    }
    if(sqlResult<1)
      throw new NoSuchRowException("can't find such row")
  }
  
  def register(classId:Int, form:ClassRegistrationController.JoinForm)(implicit session:Session, stu:Users.User){
    if(checkPerm(classId,0))
      throw new AlreadyRegistrated("alread registrated")
    
    tQuery += 
      ClassregistrationsRow(
        stu.id, classId,
        form.identity.getOrElse((stu.userDetail \ "identity").as[String]),
        form.major.getOrElse((stu.userDetail \ "major" \ "name").as[String]),
        new Timestamp(System.currentTimeMillis()), 0)
   }
  
  def checkPerm(classId:Int,permLevel:Int = 1)(implicit session : Session, student:Users.User)=
    tQuery.filter(t=>t.classid === classId && t.userid === student.id && t.accepted >= permLevel).firstOption.isDefined
}

