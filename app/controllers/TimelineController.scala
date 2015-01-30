package controllers

import java.sql.Timestamp

import models.{TimelineItems, ClassRegistrations, Classes, Users}
import models.Users.{User, userWrites}
import play.api.db.slick.DBAction
import play.api.db.slick.DB
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import exception._
import controllers.Utils._

import scala.slick.jdbc.JdbcBackend

/**
 * Created by infinitu on 15. 1. 29..
 */
object TimelineController extends Controller{

  val notfound = NotFound("no such item")
  val badRequest = BadRequest("wrong post format")

  def getTimelineItems(classId:Int)=DBAction{req=>
    val drop = req.getQueryString("drop").map(_.toInt).getOrElse(0)
    val take = req.getQueryString("take").map(_.toInt).getOrElse(999)
    UserLogin.loginedMe(req)
      .map{implicit user=>
        implicit val session = req.dbSession
        TimelineItems(classId,drop,take).map{Json.toJson(_)}}
      .map{x=>Ok(JsArray(x))}
      .getOrElse(UserLogin.loginNessesery)
  }
  
  def getTimelineUI(classId:Int) = TODO
  
  def getTimelineItem(classId:Int, itemId:Int) = DBAction{req =>
    UserLogin.loginedMe(req)
      .map{ implicit user=>
        implicit val session = req.dbSession
        TimelineItems(classId,itemId)
          .map(Json.toJson(_))
          .map(x=>Ok(x))
          .getOrElse(notfound)
      }
      .getOrElse(UserLogin.loginNessesery)
  }
  
  case class timelinePostForm(itemType:String, data:JsObject)
  implicit val timlimePostFormReads:Reads[timelinePostForm] = (
      (JsPath \ "itemType").read[String] and
      (JsPath \ "data").read[JsObject]
    )(timelinePostForm.apply _)
  
  case class alertForm(text:String)
  implicit val alertFormReads = (JsPath \ "text").read[String].map(alertForm)
  
  case class assignmentForm(title:String, description:String,due_datetime:Timestamp, attchments:Seq[String])
  implicit val assignmentFormReads = 
    ((JsPath \ "title").read[String]
      and (JsPath \ "description").read[String]
      and (JsPath \ "due_datetime").read[Timestamp]
      and (JsPath \ "attachments").read[Seq[String]])(assignmentForm.apply _)
  
  case class lectureForm(title:String, attchments:Seq[String])
  implicit val lectureFormReads = ((JsPath \ "title").read[String] and (JsPath \ "attachments").read[Seq[String]])(lectureForm.apply _)
  
  
  
  def postTimeline(classId:Int) = Action{ req=>
    
    Utils.postTransaction{ implicit session=>
      UserLogin.loginedMe(req)
        .map { implicit user =>
          req.body.asJson.flatMap(_.asOpt[timelinePostForm])
            .flatMap {
              case timelinePostForm("alert", json) =>
                json.asOpt[alertForm]
              case timelinePostForm("assignment", json) =>
                json.asOpt[assignmentForm]
              case timelinePostForm("lecture", json) =>
                json.asOpt[lectureForm]
              case _=>
                None
            }
            .map{ input=>
              TimelineItems.append(classId,input)
              Ok("successfully")
            }.getOrElse(badRequest)
        }.getOrElse(UserLogin.loginNessesery)
    }
  }


  
}
