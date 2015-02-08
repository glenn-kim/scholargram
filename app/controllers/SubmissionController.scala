package controllers

import controllers.ClassRegistrationController._
import exception.InvalidDataIntegraityException
import models._
import models.Users.{User, userWrites}
import models.Submissions._
import play.api.db.slick.DBAction
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import models.ScholargramTables.profile.simple.{Session=>dbSession}


/**
 * Created by infinitu on 15. 2. 4..
 */
object SubmissionController extends Controller {

  val forbidden = Forbidden("does not have permission to request it.")
  val badrequest = Forbidden("wrong format.")

  def getSubmissions(classId: Int, assignmentId: Int) = DBAction { req =>
    UserLogin.loginedMe(req)
      .map{implicit user=>
        implicit val session = req.dbSession
        val uidOpt:Option[Int] = req.getQueryString("userid").map(_.toInt)
        
        if(user.userType == "professor")
          uidOpt match {
            case Some(uid)=>
              Submissions.versionLog(classId, assignmentId, uid).map(Json.toJson(_))
            case None=>
              Submissions.lastest(classId, assignmentId).map(Json.toJson(_))
          }
        else
          Submissions.versionLog(classId, assignmentId).map(Json.toJson(_))
      }
      .map(arr=>Ok(JsArray(arr)))
      .getOrElse(UserLogin.loginNessesery)
  }
  
  case class SubmissionPostForm(description:String, attchments:Seq[String])
  implicit val submissionFormReads = ((JsPath \ "description").read[String] and (JsPath \ "attachments").read[Seq[String]])(SubmissionPostForm.apply _)

  def postSubmission(classId:Int,assignmentId:Int) = Action { req =>

    Utils.postTransaction { implicit session =>
      UserLogin.loginedMe(req)
        .map(implicit user=>
          req.body.asJson.flatMap{_.asOpt[SubmissionPostForm]}
            .map{x=>
              Submissions.append(classId,assignmentId,x)
              Ok("successfully")
            }
            .getOrElse(badrequest)
        )
        .getOrElse(UserLogin.loginNessesery)
    }
  }
}
