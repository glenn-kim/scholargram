package models

import java.sql.{Date, Timestamp}

import models.Submissions.Submission
import models.TimelineItem._
import models.Users.{User, Professor}
import play.api.libs.json.{JsObject, Json, JsValue, Writes}
import play.api.db.slick.Config.driver.profile.simple._
import ScholargramTables._

import scala.slick.lifted.{Column, Query}

/**
 * Created by infinitu on 15. 1. 22..
 */
object Attachments {
  val tQuery = ScholargramTables.Attachments 
  case class Attachment(filename:String, size:Long, url:String){
    def this(row: AttachmentsRow)=this(row.filename, 0, row.directory/*todo fix*/)
  }
  
  implicit val attachmentWrites = new Writes[Attachment]{
    override def writes(attachment: Attachment): JsValue = Json.obj(
      "filename"->attachment.filename,
      "size"->attachment.size,
      "url"->attachment.url
    )
  }
  
  
  private[models] def apply(attachable: Attachable)(implicit session : scala.slick.jdbc.JdbcBackend#SessionDef):Seq[Attachment]={
    (attachable.attachQuery innerJoin tQuery on ((x,y)=> x._1 === y.attachmentid && x._2 === y.owner))
      .map(_._2)
      .list
      .map { row =>
        new Attachment(row)
      }
  }
}

trait Attachable{
  val attachQuery:Query[Column[String],Column[Int],(String,Int)]
}