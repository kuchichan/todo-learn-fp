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
  def mainLoop[F[_]: MonadThrow: Console](tasks: Tasks[F]): F[Unit] =
    val loop = for {
      _ <- Console[F].println("Please enter your next task: ")
      input <- Console[F].readLine
      _ <- tasks.add(Task(input, State.Todo))
      list <- tasks.getAll
      _ <- Console[F].println(s"Your current tasks: ${list}")
    } yield ()

    loop.foreverM
}
