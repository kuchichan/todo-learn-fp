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
import todo.domain.command.*

object Cli {
  val prompt = ">>> "
  val greeting = """ 
  | Todo App (Yet another)   
  |=========================
  | press 'h' for help or 'q' to quit
  """.stripMargin
  
  def greet[F[_]: Console] = Console[F].println(greeting)

  def mainLoop[F[_]: MonadThrow: Console](parser: CommandParser[F], runner: CommandRunner[F]): F[Unit] =
    for {
      _ <- Console[F].print(prompt)
      input <- Console[F].readLine
      command <- parser.parseCommand(input)
      result <- runner.runCommand(command)
      _ <- result match { 
        case Right(ValidResult.Terminate) => MonadThrow[F].unit  
        case _ => MonadThrow[F].unit >>  mainLoop(parser, runner)
      }
    } yield () 
}
