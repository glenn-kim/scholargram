package controllers

import java.sql.Timestamp

import controllers.TimelineController._
import exception.DoesNotHavePermissionException
import play.api.db.slick._
import play.api.libs.json._
import play.api.mvc.Result
import play.api.Play.current

import scala.slick.jdbc.JdbcBackend

/**
 * Created by infinitu on 15. 1. 30..
 */
object Utils {
  
  implicit val timestampRead = new Reads[Timestamp]{
    override def reads(json: JsValue): JsResult[Timestamp] = json match {
      case JsNumber(d) => JsSuccess(new Timestamp(d.toLong))
      case JsString(s) => 
        try{JsSuccess(Timestamp.valueOf(s))}
        catch{case x: Throwable =>JsError(x.getMessage)}
      case x=>
        JsError("can't parse " + x.getClass.toString + " type")
    }
  }

  implicit val timestampWrites = new Writes[Timestamp] {
    override def writes(o: Timestamp): JsValue = JsNumber(o.getTime)
  }
  
  def postTransaction(f:(JdbcBackend.Session)=>Result):Result=
    DB.withTransaction{session=>
      try{
        f.apply(session)
      }
      catch{
        case t: DoesNotHavePermissionException=>
          session.rollback()
          //TODO Logger
          Forbidden(t.getMessage)
        case t: Throwable =>
          session.rollback()
          //TODO Logger
          InternalServerError(t.getMessage)
      }
    }

  
}
