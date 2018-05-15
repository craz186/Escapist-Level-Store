package com.escapist.actor

import java.sql.SQLIntegrityConstraintViolationException

import akka.actor.{Actor, ActorLogging, Props}
import com.escapist.model.Level
import java.util.UUID

import org.apache.log4j.Logger

import scala.util.Try

object LevelActor {
  def props(): Props = Props(new LevelActor)
}

class LevelActor extends MySqlConnection with Actor with ActorLogging {

  val logger = Logger.getLogger(classOf[UserActor])

  val insertSql = "INSERT INTO levels VALUES (?, ?, ?);"
  val getLevelByIdSql = "SELECT level_json_format FROM levels WHERE level_id = ?"

  override def receive: Receive = {
    case AddLevel(level) =>
      logger.info("Adding level")
      sender ! addLevel(level)
    case GetLevel(levelId) =>
      logger.info("Fetching level")
      sender ! getLevel(levelId)
    case _ ⇒  logger.info(s"Received unknown message: ")

  }

  def addLevel(level: Level): String = {
    // TODO validate that User exists
    logger.info("Generating level id")

    val levelId = UUID.randomUUID.toString
    logger.info("Preparing to insert level")
    val preparedStatement = connection.prepareStatement(insertSql)
    preparedStatement.setString(1, levelId)
    preparedStatement.setString(2, level.creatorId)
    preparedStatement.setString(3, level.levelDetail)

    try {
      preparedStatement.executeUpdate
    } catch {
      case e: SQLIntegrityConstraintViolationException =>
        throw new SQLIntegrityConstraintViolationException(e.getMessage)
    }
    levelId
  }

  def getLevel(levelId: String): String = {
    val preparedStatement = connection.prepareStatement(getLevelByIdSql)
    preparedStatement.setString(1, levelId)

    try {
      val resultSet = preparedStatement.executeQuery
      var count = 0
      var result = ""
      while (resultSet.next()) {
        result = resultSet.getString("level_json_format")
        count += 1
      }
      if (count > 1) {
        // TODO ERROR
      } else if (result == "") {
        // TODO ERROR
      }
      result
    } catch {
      case e: SQLIntegrityConstraintViolationException =>
        throw new SQLIntegrityConstraintViolationException(e.getMessage)
    }
  }
}

case class AddLevel(level: Level)
case class GetLevel(levelId: String)