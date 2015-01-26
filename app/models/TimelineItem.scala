package models

import java.sql.{Date, Timestamp}

import models.Users.{User, Professor}
import play.api.libs.json.{JsObject, Json, JsValue, Writes}
import play.api.db.slick.Config.driver.profile.simple._
import ScholargramTables._
import models.Attachments._

import scala.slick.lifted


/**
 * Created by infinitu on 15. 1. 22..
 */
object TimelineItem {
  
  val tQuery = Timelineitems

  abstract case class TimelineItem( id:Int,
                                    item_type:String,
                                    publish:Timestamp){
    val data:JsObject 
  }
  
  case class Alert(override val id:Int, override val publish:Timestamp, text:String) extends TimelineItem(id,"alert",publish){
    override lazy val data: JsObject = Json.obj(
      "text"->text
    )
  }

  case class Assignment(override val id:Int, override val publish:Timestamp,
                        title:String, text:String, due_datetime:Timestamp, attachments:Seq[Attachment])
                        extends TimelineItem(id,"assignment",publish){
    override lazy val data: JsObject = Json.obj(
      "title"->title,
      "text"->text,
      "due_datetime"->due_datetime,
      "attachments"->Json.toJson(attachments)
    )
  }

  case class Lecture(override val id:Int, override val publish:Timestamp,
                     title:String, attachments:Seq[Attachment]) extends TimelineItem(id,"lecture",publish){
    override lazy val data: JsObject = Json.obj(
      "title"->title,
      "attachments"->Json.toJson(attachments)
    )
  }

  implicit  val timelineWrites = new Writes[TimelineItem]{
    override def writes(item: TimelineItem): JsValue = Json.obj(
      "id"->item.id,
      "type"->item.item_type,
      "publish"->item.publish,
      "data"->item.data
    )
  }
  
  private def AssignmentAttachable(assignId:Int):Attachable=new Attachable{
    override val attachQuery =
      Assignmentattachments.filter(_.itemid === assignId).map(x=>(x.attachmentid,x.owner))
  }

  private def LectureAttachable(lectureId:Int):Attachable=new Attachable{
    override val attachQuery =
      Lectureattachments.filter(_.itemid === lectureId).map(x=>(x.attachmentid,x.owner))
  }
  
  val allTimeline = tQuery.leftJoin(Alerts).on(_.itemid === _.itemid)
                          .leftJoin(Assignments).on(_._1.itemid === _.itemid)
                          .leftJoin(Lectures).on(_._1._1.itemid === _.itemid)
                          .map(x=>(x._1._1._1,x._1._1._2.?,x._1._2.?,x._2.?))
  def apply(classId:Int,drop:Int=0,take:Int=999999)(implicit session : scala.slick.jdbc.JdbcBackend#SessionDef)={
    allTimeline.filter(_._1.classid === classId).drop(drop).take(take)
      .list.map{
      case (t,Some(item),None,None)=>
        Alert(t.itemid, t.publishdatetime, item.text)
      case (t,None,Some(item),None)=>
        Assignment(t.itemid,t.publishdatetime,item.title,item.description,item.duedatetime, Attachments(AssignmentAttachable(item.itemid)))
      case (t,None,None,Some(item))=>
        Lecture(t.itemid, t.publishdatetime, item.title, Attachments(LectureAttachable(item.itemid)))
    }
  }
  
}
