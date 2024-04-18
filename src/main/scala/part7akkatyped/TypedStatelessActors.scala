package part7akkatyped

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object TypedStatelessActors extends App {

  trait SimpleThing
  case object EatChocolate extends SimpleThing
  case object WashDishes extends SimpleThing
  case object LearAkka extends SimpleThing

  val emotionalMutableActor: Behavior[SimpleThing] = Behaviors.setup { context =>
    // spin up the actor state
    var happiness = 0

    // behavior of the actor
    Behaviors.receiveMessage {
      case EatChocolate =>
        context.log.info(s"($happiness) Eating chocolate, getting a shot of dopamine!")
        happiness += 1
        Behaviors.same
      case WashDishes =>
        context.log.info(s"($happiness) Washing dishes...!")
        happiness += 2
        Behaviors.same
      case LearAkka =>
        context.log.info(s"($happiness) Learning akka")
        happiness += 100
        Behaviors.same
      case _ =>
        context.log.info(s"($happiness) I don't know")
        Behaviors.same
    }
  }

  def emotionalFunctionalActor(happiness: Int = 0): Behavior[SimpleThing] = Behaviors.receive { (context, message) =>
    message match {
      case EatChocolate =>
        context.log.info(s"($happiness) Eating chocolate, getting a shot of dopamine!")
        emotionalFunctionalActor(happiness+1)
      case WashDishes =>
        context.log.info(s"($happiness) Washing dishes...!")
        emotionalFunctionalActor(happiness-2)
      case LearAkka =>
        context.log.info(s"($happiness) Learning akka")
        emotionalFunctionalActor(happiness+100)
      case _ =>
        context.log.info(s"($happiness) I don't know")
        Behaviors.same
    }
  }

  var emotionalMutableSystem = ActorSystem(emotionalFunctionalActor(), "emotionalSystem")

  emotionalMutableSystem ! EatChocolate
  emotionalMutableSystem ! EatChocolate
  emotionalMutableSystem ! EatChocolate
  emotionalMutableSystem ! WashDishes
  emotionalMutableSystem ! LearAkka

  Thread.sleep(1000)

  emotionalMutableSystem.terminate()

}
