package todo.programs

import cats.Monad
import cats.effect.implicits.*
import cats.effect.std.Console
import cats.effect.IO
import cats.effect.IOApp
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

  private def processResult[F[_]: Console: Monad](result: CommandResult) =
    result match
      case Right(_)    => Monad[F].unit
      case Left(error) => Console[F].println(error.errorMsg)

  def mainLoop[F[_]: Monad: Console](
    parser: CommandParser[F],
    runner: CommandRunner[F],
  ): F[Unit] =
    val loop =
      for {
        _       <- Console[F].print(prompt)
        input   <- Console[F].readLine
        command <- parser.parseCommand(input)
        result  <- runner.runCommand(command)
        _       <- processResult(result)
      } yield result

    Monad[F].iterateUntil(loop)(result => result == Right(ValidResult.Terminate)).void

}
