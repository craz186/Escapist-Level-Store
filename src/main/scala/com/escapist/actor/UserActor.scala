package com.escapist.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.escapist.model.User
import org.apache.log4j.Logger

object UserActor {
  def props(): Props = Props(new UserActor)
}
class UserActor extends MySqlConnection with Actor with ActorLogging {

  val logger = Logger.getLogger(classOf[UserActor])
  // connect to the database named "mysql" on the localhost
  // TODO read creds from file
  val insertSql = "INSERT INTO users VALUES (?, ?);"

  override def receive: Receive = {
    case AddUser(user) =>
      addUser(user)
      sender ! s"Successfully added user ${user.username}"
    case _ ⇒  logger.info(s"Received unknown message: ")

  }


  def addUser(user: User): Unit = {
    val preparedStatement = connection.prepareStatement(insertSql)
    preparedStatement.setString(1, user.username)
    preparedStatement.setString(2, user.password)
    preparedStatement.executeUpdate
  }
}

case class AddUser(user: User)