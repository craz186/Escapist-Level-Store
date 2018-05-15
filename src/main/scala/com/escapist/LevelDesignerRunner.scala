package com.escapist

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.http.scaladsl.server.RouteConcatenation._
import com.escapist.actor.{LevelActor, UserActor}
import org.apache.log4j.Logger

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object LevelDesignerRunner extends App {

  val logger = Logger.getLogger(classOf[LevelDesignerRoutes])
  logger.info("Starting server")
  implicit val system: ActorSystem = ActorSystem("my-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = 120 seconds
  val levelActor: ActorRef = system.actorOf(LevelActor.props())
  val userActor: ActorRef = system.actorOf(UserActor.props())

  val allRoutes =
    new LevelDesignerRoutes(levelActor).routes ~ new UserRoutes(userActor).routes

  val bindingFuture = Http().bindAndHandle(allRoutes, "0.0.0.0", 8080)

  sys.addShutdownHook(
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  )
}