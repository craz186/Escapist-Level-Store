package com.escapist.actor

import java.sql.{Connection, DriverManager}

abstract class MySqlConnection {
  val driver = "com.mysql.cj.jdbc.Driver"
  val url = "jdbc:mysql://localhost:3306/users?characterEncoding=utf8&useSSL=false"
  val mySqlUsername = "root"
  val mySqlPassword = "7bzgvi0v"

  Class.forName(driver).newInstance()
  // TODO close connection
  val connection: Connection = DriverManager.getConnection(url, mySqlUsername, mySqlPassword)

}
