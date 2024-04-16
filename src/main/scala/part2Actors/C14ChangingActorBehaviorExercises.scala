package part2Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.runtime.Nothing$

object C14ChangingActorBehaviorExercises extends App {

  /**
   * Exercises
   * 1 - recreate the Counter Actor with context.become and NO MUTABLE STATE
   */

  val system = ActorSystem("system")

  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {
    import Counter._
    override def receive: Receive = counterRecive(0)

    def counterRecive(counter: Int): Receive = {
      case Increment => {
        println("incrementing...")
        context.become(counterRecive(counter+1))
      }
      case Decrement =>
        println("decrementing...")
        if(counter <= 0) sender() ! "Reject"
        else context.become(counterRecive(counter-1))
      case Print => println(s"Count: $counter")
    }
  }

  import Counter._
  val counter = system.actorOf(Props[Counter], "myCounter")

  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print

  /**
   * Exercise 2 - a simplified voting system
   */

  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidate: Option[String])
  class Citizen extends Actor {
    override def receive: Receive = {
      case VoteStatusRequest => sender() ! VoteStatusReply(None)
      case Vote(candidate) => context.become(votedReceiver(candidate))
    }
    def votedReceiver(candidate: String): Receive = {
      case VoteStatusRequest => sender() ! VoteStatusReply(Some(candidate))
      case Vote(candidate) => ???
    }
  }

  case class AggregateVotes(citizens: Set[ActorRef])
  class VoteAggregator extends Actor {

    override def receive: Receive = {
      case AggregateVotes(citizens) => citizens.foreach(c => c ! VoteStatusRequest)
      context.become(awaitingAll(citizens, Map()))
    }

    def awaitingAll(stillWaiting: Set[ActorRef], votes: Map[String, Int]): Receive = {
      case VoteStatusReply(Some(candidate)) => {
        val newStillWaiting = stillWaiting - sender()
        val currentVotesForCandidate = votes.getOrElse(candidate, 0)
        val newVotes = votes + (candidate -> (currentVotesForCandidate+1))
        if(newStillWaiting.isEmpty)
          println(s"pool stats $newVotes")
        else
          context.become(awaitingAll(newStillWaiting, newVotes))
      }
    }
  }

  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])

  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")

  val voteAggregator = system.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice, bob, charlie, daniel))

  /*
    Print the status of the votes

    Martin -> 1
    Jonas -> 1
    Roland -> 2
   */
}
