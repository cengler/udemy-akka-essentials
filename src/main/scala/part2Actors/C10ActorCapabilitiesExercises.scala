package part2Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2Actors.C10ActorCapabilitiesExercises.Person.LiveTheLife

object C10ActorCapabilitiesExercises extends App {

  /**
   * Exercises
   *
   * 1. a Counter actor
   *   - Increment
   *   - Decrement
   *   - Print
   *
   * 2. a Bank account as an actor
   *   receives
   *   - Deposit an amount
   *   - Withdraw an amount
   *   - Statement
   *   replies with
   *   - Success
   *   - Failure
   *
   *   interact with some other kind of actor
   */

  val system = ActorSystem("system")

  // DOMAIN comp object
  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {
    import Counter._
    var counter: Int = 0
    override def receive: Receive = {
      case Increment => counter += 1
      case Decrement => counter -= 1
      case Print => println(counter)
    }
  }

  val counter = system.actorOf(Props[Counter], "myCounter")
  import Counter._
  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)
  counter ! Print

  object BankAccount {
    case class Deposit(amount: Int)
    case class Withdraw(amount: Int)
    case object Statement
    case class TransactionSuccess(message: String)
    case class TransactionFailure(reason: String)
  }

  class BankAccount extends Actor {
    import BankAccount._
    var funds: Int = 0
    override def receive: Receive = {
      case Deposit(amount) =>
        if(amount < 0) sender() ! TransactionFailure("Amount cant be negative")
        else {
          funds += amount
          sender() ! TransactionSuccess(s"Transaction success $amount")
        }

      case Withdraw(amount) =>
        if(amount < 0) sender() ! TransactionFailure("Amount cant be negative")
        else if(funds < amount) sender() ! TransactionFailure("insufficient founds")
        else {
          funds -= amount
          sender() ! TransactionSuccess(s"Transaction success, $amount")
        }
      case Statement => println(s"Your balance is $funds")
    }
  }

  object Person {
    def props(name: String): Props = Props(new Person(name))
    case class LiveTheLife(account: ActorRef)
  }

  class Person(name: String) extends Actor {
    import Person._
    import BankAccount._
    override def receive: Receive = {
      case LiveTheLife(account) =>
        account ! Deposit(1000)
        account ! Withdraw(20000)
        account ! Withdraw(100)
        account ! Withdraw(10)
        account ! Withdraw(200)
        account ! Statement
      case message => println(message)
    }
  }

  val joe = system.actorOf(Person.props("joe"), "joe")
  val account = system.actorOf(Props[BankAccount], "joeAccount")
  joe ! LiveTheLife(account)
}
