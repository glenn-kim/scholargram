package models

import ScholargramTables._
import play.api.libs.json.{Json, JsValue, Writes}

/**
 * Created by infinitu on 15. 1. 19..
 */

case class School(id:Int, name:String, location:Option[String]){
  def this(school:SchoolsRow)=this(school.schoolid,school.schoolname,school.location)
  
}
case class Major(id:Int, name:String, school:School){
  def this(id:Int, name:String, school:SchoolsRow)=this(id,name,new School(school))
}

object School{
  implicit val SchoolWrites = new Writes[School]{
    override def writes(o: School): JsValue = Json.obj(
      "id"->o.id,
      "name"->o.name,
      "location"->o.location
    )
  }
  
}
object Major{
  implicit val MajorWrites = new Writes[Major] {
    override def writes(o: Major): JsValue = Json.obj(
      "id"->o.id,
      "name"->o.name,
      "School"->Json.toJson(o.school)
    )
  }
}
