package models

import models.ScholargramTables._
import models.ScholargramTables.profile.simple._
import play.api.libs.json._

import scala.slick.lifted.{Column, Query}

/**
 * Created by infinitu on 15. 1. 22..
 */
object Attachments {
  val tQuery = ScholargramTables.Attachments 
  
  case class Attachment(filename:String, size:Long, url:String){
    def this(row: AttachmentsRow)=this(row.filename, 0, row.directory/*todo fix*/)
  }
  
  implicit val attachmentWrites:Writes[Attachment] = new Writes[Attachment]{
    override def writes(attachment: Attachment): JsValue = Json.obj(
      "filename"->attachment.filename,
      "size"->attachment.size,
      "url"->attachment.url
    )
  }
  
  implicit val attachmentSeqWrites = new Writes[Seq[Attachment]]{
    override def writes(attachments: Seq[Attachment]): JsValue = JsArray(attachments.map(Json.toJson(_)))
  }
  
  
  private[models] def apply(attachable: Attachable)(implicit session : Session):Seq[Attachment]={
    (attachable.attachQuery innerJoin tQuery on ((x,y)=> x._1 === y.attachmentid && x._2 === y.owner))
      .map(_._2)
      .list
      .map { row =>
        new Attachment(row)
      }
  }
}

trait Attachable{
  val attachQuery:Query[(Column[String],Column[Int]),(String,Int),Seq]
}