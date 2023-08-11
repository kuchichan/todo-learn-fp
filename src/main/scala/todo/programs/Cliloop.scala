package todo.programs

import cats.MonadThrow
import cats.effect.implicits.*
import cats.effect.std.Console
import cats.effect.{IO, IOApp}
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import todo.domain.command.*
import todo.domain.task.*
import todo.services.*

object Cli {
  val prompt = ">>> "

  val greeting =
    """ 
  | Todo App (Yet another)   
  |=========================
  | press 'h' for help or 'q' to quit
  """.stripMargin

  def greet[F[_]: Console] = Console[F].println(greeting)

  def mainLoop[F[_]: MonadThrow: Console](
    parser: CommandParser[F],
    runner: CommandRunner[F],
  ): F[Unit] =
    for {
      _       <- Console[F].print(prompt)
      input   <- Console[F].readLine
      command <- parser.parseCommand(input)
      result  <- runner.runCommand(command)
      _ <-
        result match {
          case Right(ValidResult.Terminate) => MonadThrow[F].unit
          case _                            => MonadThrow[F].unit >> mainLoop(parser, runner)
        }
    } yield ()

}
