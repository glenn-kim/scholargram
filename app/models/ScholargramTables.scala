package models

import scala.slick.driver.JdbcProfile

/**
 * Created by infinitu on 15. 1. 22..
 */
object ScholargramTables extends Tables{
  override val profile: JdbcProfile = play.api.db.slick.Config.driver
}
