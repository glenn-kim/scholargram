package models

import ScholargramTables._

/**
 * Created by infinitu on 15. 1. 19..
 */

case class School(id:Int, name:String, location:String){
  def this(school:SchoolsRow)=this(school.schoolid,school.schoolname,school.location)
  
}
case class Major(id:Int, name:String, school:School){
  def this(id:Int, name:String, school:SchoolsRow)=this(id,name,new School(school))
}
