package com.escapist

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import com.escapist.actor.{AddLevel, GetLevel}
import com.escapist.model.{Level, LevelJsonSupport}

class LevelDesignerRoutes(levelActor: ActorRef)(implicit timeout:Timeout) extends Directives with LevelJsonSupport {


  def routes = {
    pathPrefix("api") {
      pathPrefix("levels") {
        post {
          entity(as[Level]) { level =>
            onSuccess(levelActor ? AddLevel(level)) { successMessage =>
              complete(StatusCodes.Created -> successMessage.asInstanceOf[String])
            }
          }
        } ~
        get {
          pathPrefix(Segment) { levelId =>
            onSuccess(levelActor ? GetLevel(levelId)) { successMessage =>
              complete(StatusCodes.OK -> successMessage.asInstanceOf[String])
            }
          }
        }
      }
    }
  }
}
