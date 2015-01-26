package models


import java.sql.{Date, Timestamp}

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
    import Users.userwrites
    override def writes(reg: ClassRegistration): JsValue = Json.obj(
      "student" -> Json.toJson(reg.student),
      "joined" -> reg.joined,
      "identity" -> reg.identity,
      "major" -> reg.major,
      "accepted" -> reg.accepted
    )
  }
  def apply(user:User, classid:Int)={
    val row = tQuery.filter(_.classid === classid).filter(_.userid === user.id).firstOption
    row map (new ClassRegistration(user,_))
  }
  
}

