package models

import java.sql.Timestamp

import controllers.SubmissionController.SubmissionPostForm
import exception.DoesNotHavePermissionException
import models.Attachments._
import models.ScholargramTables._
import models.Users.User
import models.Users.userWrites
import models.Attachments.attachmentWrites
import models.ScholargramTables.profile.simple._
import play.api.libs.json.{Json, JsValue, Writes}


/**
 * Created by infinitu on 15. 1. 23..
 */
object Submissions {
  
  val tQuery = models.ScholargramTables.Submissions
  
  case class Submission(create_datetime:Timestamp, description: String, attachments:Seq[Attachment]){
    def this(row:SubmissionsRow)(implicit session : Session) =
      this(row.createdatetime,row.description, Attachments(SubmissionAttachable(row.submissionid)))
  }
  
  case class StudentSubmission(student:User, last_updated:Option[Timestamp], first_upload:Option[Timestamp], last_submission:Option[Submission])

  implicit val submissionWrites = new Writes[Submission]{
    override def writes(submission: Submission): JsValue = Json.obj(
      "create_datetime"->submission.create_datetime,
      "descripttion"->submission.description,
      "attachments"->Json.toJson(submission.attachments)
    ) 
  }

  implicit val studentSubmissionWrites = new Writes[StudentSubmission]{
    override def writes(userSubmission: StudentSubmission): JsValue = Json.obj(
      "student"->Json.toJson(userSubmission.student),
      "last_updated"->userSubmission.last_updated,
      "first_upload"->userSubmission.first_upload,
      "last_submission"->userSubmission.last_submission
    )
  }

  private def SubmissionAttachable(submissionId:Int):Attachable=new Attachable{
    override val attachQuery =
      Submissionattachments.filter(_.submissionid === submissionId).map(x=>(x.attachmentid,x.owner))
  }
  
  def checkReadPerm(classId:Int, assignmentId:Int)(implicit session : Session, user:User) =
    TimelineItems.checkTimelineReadPerm(classId) && {
      (Timelineitems innerJoin Assignments on (_.itemid === _.itemid))
        .filter(x=>x._1.classid === classId && x._2.itemid === assignmentId)
        .firstOption.isDefined
    }

  def checkAdminPerm(classId:Int, assignmentId:Int)(implicit session : Session, user:User) =
    TimelineItems.checkTimelineWritePerm(classId) && {
      (Timelineitems innerJoin Assignments on (_.itemid === _.itemid))
        .filter(x=>x._1.classid === classId && x._2.itemid === assignmentId)
        .firstOption.isDefined
    }
  
  val lastestSubmissionQuery = 
    tQuery.groupBy(sub=>(sub.assignmentid,sub.userid))
      .map{ case (group,sub) => (sub.map(_.submissionid).max,sub.map(_.createdatetime).min)}
      .leftJoin(tQuery).on(_._1 === _.submissionid).map{case (x,y)=> (y,x._2)}
      .innerJoin(
        (Timelineitems innerJoin Assignments on (_.itemid === _.itemid)).map(x=>(x._1.classid,x._1.itemid)))
      .on(_._1.assignmentid === _._2).map{case ((x1,x2),(cid,iid))=>(x1,x2,cid,iid)}
  
  val studentLastestSubmissionQuery=
    ClassRegistrations.registeredStudents
      .leftJoin(lastestSubmissionQuery)
      .on{case ((reg,user),(lastsub,firstTimestamp,cid,iid))=>reg._1.classid === cid && reg._1.userid === lastsub.userid}
      .map{case((reg,user),(lastsub,firstTimestamp,cid,iid))=>(reg,user,iid,lastsub.?,firstTimestamp)}
  

  def lastest(classId:Int, assignmentId:Int)(implicit session : Session, prof:User)={
    if(!checkReadPerm(classId,assignmentId))
      throw new DoesNotHavePermissionException("does not have this timeline or it may be not a assignment.")
    
    studentLastestSubmissionQuery
      .filter{case (reg,user,iid,lastsub,firststamp)=>
        reg._2.classid === classId &&
        iid === assignmentId}
      .list
      .map{case (reg,user,iid,lastsub,firststamp)=>
        StudentSubmission(
          Users.getUsers(user),
          lastsub.map(_.createdatetime),
          firststamp,
          lastsub.map(new Submission(_))
        )    
      }
  }

  def versionLog(classId:Int,assignmentId:Int)(implicit session : Session, user:User):List[Submissions.Submission]=
    versionLog(classId,assignmentId,user.id)
  def versionLog(classId:Int,assignmentId:Int,studentId:Int)(implicit session:Session, user:User)={
    if(user.id!=studentId && !Classes.checkPerm(classId))
      throw new DoesNotHavePermissionException("can not access to this submission.")
    
    tQuery.filter(t=> t.assignmentid === assignmentId && t.userid === studentId)
      .list
      .map(new Submission(_))
  }

  def append(classId:Int,assignmentId:Int,form:SubmissionPostForm)(implicit session:Session, user:Users.User)={
    if(!checkReadPerm(classId,assignmentId)) throw new DoesNotHavePermissionException("cant not have permission to read this classes")
    if(user.userType == "professor") throw new DoesNotHavePermissionException("professor does should not submit")

    val sid = tQuery.returning(tQuery map (_.submissionid)) += 
      SubmissionsRow(0,assignmentId,user.id,form.description,new Timestamp(System.currentTimeMillis()))
    
    Submissionattachments ++= form.attchments.map(SubmissionattachmentsRow(_,user.id,sid))
  }
  
}
