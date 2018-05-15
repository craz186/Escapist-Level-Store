package com.escapist

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import com.escapist.actor.AddUser
import com.escapist.model.{User, UserJsonSupport}

class UserRoutes(userActor: ActorRef)(implicit timeout:Timeout) extends Directives with UserJsonSupport{
  def routes = {
    pathPrefix("api") {
      pathPrefix("users") {
        pathPrefix(Segment) { username =>
          get {
            complete("get user")
          }
        } ~
          post {
            entity(as[User]) { user =>
              onSuccess(userActor ? AddUser(user)) { successMessage =>
                complete(StatusCodes.Created -> successMessage.asInstanceOf[String])
              }
            }
          }
      }
    }
  }
}
