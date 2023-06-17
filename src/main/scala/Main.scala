import cats.effect.implicits.*

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.std.Console
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import cats.MonadThrow
import todo.Tasks
import todo.Task

object Main extends IOApp.Simple {

  def run: IO[Unit] =
    for {
      state <- Tasks.instance[IO]
      _ <- mainProgram(state)
    } yield ()

  def mainProgram[F[_]: MonadThrow: Console](tasks: todo.Tasks[F]): F[Unit] =
    for {
      _ <- Console[F].println("Please enter your next task: ")
      input <- Console[F].readLine
      _ <- tasks.add(Task(input))
      list <- tasks.getAll
      _ <- Console[F].println(s"Your current tasks: ${list}")
      _ <- mainProgram(tasks)
    } yield ()

}
