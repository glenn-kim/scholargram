package models

import java.sql.Timestamp

import exception.{DoesNotHavePermissionException, InvalidDataIntegraityException}
import models.Attachments._
import models.ScholargramTables._
import models.ScholargramTables.profile.simple._
import play.api.libs.json.{JsObject, JsValue, Json, Writes}


/**
 * Created by infinitu on 15. 1. 22..
 */
object TimelineItems {
  
  val tQuery = Timelineitems

  abstract case class TimelineItem( id:Int,
                                    item_type:String,
                                    publish:Timestamp){
    val data:JsObject 
  }
  
  class Alert(override val id:Int, override val publish:Timestamp, text:String) extends TimelineItem(id,"alert",publish){
    override lazy val data: JsObject = Json.obj(
      "text"->text
    )
  }

  class Assignment(override val id:Int, override val publish:Timestamp,
                        title:String, text:String, due_datetime:Timestamp, attachments:Seq[Attachment])
                        extends TimelineItem(id,"assignment",publish){
    override lazy val data: JsObject = Json.obj(
      "title"->title,
      "text"->text,
      "due_datetime"->due_datetime,
      "attachments"->Json.toJson(attachments)
    )
  }

  class Lecture(override val id:Int, override val publish:Timestamp,
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
  
  def checkTimelineReadPerm(classId:Int)(implicit session : Session, user:Users.User)=
    user match {
        case Users.User(id, name, "student") =>
          ClassRegistrations.checkPerm(classId)
        case Users.User(id, name, "professor") =>
          Classes.checkPerm(classId)
      }
  
  def checkTimelineWritePerm(classId:Int)(implicit session : Session, user:Users.User)=
    user match {
      case Users.User(id, name, "student") =>
        false
      case Users.User(id, name, "professor") =>
        Classes.checkPerm(classId)
    }

  private def dataobjectMapping(implicit session:Session):((queryResultType)=>TimelineItem) = { 
        case (t,Some(item),None,None)=>
          new Alert(t.itemid, t.publishdatetime, item.text)
        case (t,None,Some(item),None)=>
          new Assignment(t.itemid,t.publishdatetime,item.title,item.description,item.duedatetime, Attachments(AssignmentAttachable(item.itemid)))
        case (t,None,None,Some(item))=>
          new Lecture(t.itemid, t.publishdatetime, item.title, Attachments(LectureAttachable(item.itemid)))
        case _=>
          throw new InvalidDataIntegraityException("timeline should have just one type")
    }
  type queryResultType = (TimelineitemsRow,Option[AlertsRow],Option[AssignmentsRow],Option[LecturesRow])
  val allTimeline = tQuery.leftJoin(Alerts).on(_.itemid === _.itemid)
                          .leftJoin(Assignments).on(_._1.itemid === _.itemid)
                          .leftJoin(Lectures).on(_._1._1.itemid === _.itemid)
                          .map(x=>(x._1._1._1,x._1._1._2.?,x._1._2.?,x._2.?))
  def apply(classId:Int,drop:Int=0,take:Int=999999)(implicit session : Session, user:Users.User)={
    if(!checkTimelineReadPerm(classId)) throw new DoesNotHavePermissionException("cant not have permission to read this classes")
    allTimeline.filter(_._1.classid === classId).drop(drop).take(take)
      .list.map(dataobjectMapping)
  }

  
  def apply(classId:Int, itemId:Int)(implicit session:Session, user:Users.User)={
    if(!checkTimelineReadPerm(classId)) throw new DoesNotHavePermissionException("cant not have permission to read this classes")
    allTimeline.filter(_._1.classid === classId).filter(_._1.itemid === itemId).firstOption.map(dataobjectMapping)
  }

  def append(classId:Int,item:Any)(implicit session:Session, user:Users.User)={
    if(!checkTimelineWritePerm(classId)) throw new DoesNotHavePermissionException("cant not have permission to read this classes")

    val itemid = (tQuery returning tQuery.map(_.itemid)) +=
      TimelineitemsRow(0,classId,1,new Timestamp(System.currentTimeMillis()))
    import controllers.TimelineController._
    item match {
      case alertForm(text)=>
        Alerts += AlertsRow(itemid,text)

      case assignmentForm(title,desc,due,attch)=>
        Assignments += AssignmentsRow(itemid,title,desc,due)
        Assignmentattachments ++= attch.map{AssignmentattachmentsRow(_,user.id,itemid)}

      case lectureForm(title,attch)=>
        Lectures += LecturesRow(itemid,title)
        Lectureattachments ++= attch.map(LectureattachmentsRow(_,user.id,itemid))
    }
  }
  
}
