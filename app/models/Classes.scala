package models

import java.sql.{Date, Timestamp}

import models.Users.{User, Professor}
import play.api.libs.json.{Json, JsValue, Writes}
import play.api.db.slick.Config.driver.profile.simple._
import ScholargramTables._

/**
 * Created by infinitu on 15. 1. 22..
 */
object Classes {
  
  lazy val tQuery = ScholargramTables.Classes
  
  case class Class( id:Int,
                    name:String,
                    professor:User,
                    started_date:Option[Date],
                    ending_date:Option[Date],
                    created_datetime:Timestamp,
                    school:School){
    def this(row: ClassesRow, professor:User, school:SchoolsRow)(implicit session : Session)
      =this(row.classid,row.classname,professor,row.startdate,row.enddate,row.createdatetime,new School(school))
    
    def this(row:ClassesRow, professor:UsersRow, school:SchoolsRow)(implicit session : Session)
      =this(row,models.Users.getProfessor(professor).get,school)
  }
  
  implicit val classWrites = new Writes[Class] {
    import models.Users.userWrites
    override def writes(cls: Class): JsValue = Json.obj(
      "id" -> cls.id,
      "professor" -> Json.toJson(cls.professor),
      "started_date" -> cls.started_date,
      "ending_date" -> cls.ending_date,
      "created_date" -> cls.created_datetime,
      "school" -> Json.toJson(cls.school)
    )
  }
  
  private lazy val classJoin = tQuery leftJoin Users.tQuery on (_.professorid === _.userid) leftJoin Schools on (_._1.schoolid === _.schoolid)
  def apply(classid:Int)(implicit session : Session)={
    val row = classJoin.filter(_._1._1.classid === classid).firstOption
    row map {row=>
      val cls = row._1._1
      val prof = row._1._2
      val school = row._2
      new Class(cls,prof,school)
      
    }
  }
}
