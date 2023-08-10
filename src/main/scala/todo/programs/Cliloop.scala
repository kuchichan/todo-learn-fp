package todo.programs

import cats.effect.implicits.*
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.std.Console
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import cats.MonadThrow
import todo.services.*
import todo.domain.task.*

object Cli {
  val greeting = """ 
  | Todo App (Once again...)   
  |=========================
  | press 'h' for help or 'q' to quit
  """.stripMargin

  def mainLoop[F[_]: MonadThrow: Console: CommandParser](tasks: Tasks[F]): F[Unit] =
    val loop = for {
      _ <- Console[F].println("Please enter command: ")
      input <- Console[F].readLine
      command <- CommandParser[F].parseCommand(input)
      _ <- Console[F].println(s"${command}")
    } yield ()
    
    for {
      _ <- Console[F].println(greeting)   
      _ <- loop.foreverM
    } yield ()
}
