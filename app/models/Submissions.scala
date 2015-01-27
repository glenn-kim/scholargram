package models

import java.sql.Timestamp

import models.Attachments._
import models.ScholargramTables._
import models.Users.User
import play.api.db.slick.Config.driver.profile.simple._
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
  
  case class StudentSubmission(student:User, last_updated:Timestamp, first_upload:Timestamp, last_submission:Submission)

  implicit val submissionWrites = new Writes[Submission]{
    import models.Attachments.attachmentWrites
    override def writes(submission: Submission): JsValue = Json.obj(
      "create_datetime"->submission.create_datetime,
      "descripttion"->submission.description,
      "attachments"->Json.toJson(submission.attachments)
    ) 
  }

  implicit val studentSubmissionWrites = new Writes[StudentSubmission]{
    import  models.Users.userwrites
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
  
  
  val lastestSubmissionQuery = tQuery.groupBy(sub=>(sub.assignmentid,sub.userid))
                                      .map{ case (group,sub) => (sub.map(_.submissionid).max,sub.map(_.createdatetime).min)}
                                      .leftJoin(tQuery).on(_._1 === _.submissionid).map{case (x,y)=> (y,x._2)}
                                      .leftJoin(Users.tQuery).on(_._1.userid === _.userid).map{case (x,y) => (x._1,y,x._2)}
  
  
  
  def apply(assignmentId:Int)(implicit session : Session)={
    lastestSubmissionQuery
      .filter{_._1.assignmentid === assignmentId}
      .list
      .map { x =>
        StudentSubmission(Users.getStudent(x._2).get, x._1.createdatetime,x._3.get, new Submission(x._1))
      }
  }
  
  def apply(assignmentId:Int, userId:Int)(implicit session : Session)={
    tQuery.filter(x=>x.assignmentid === assignmentId && x.userid === userId)
          .list.map(new Submission(_))
  }
  
  
}
