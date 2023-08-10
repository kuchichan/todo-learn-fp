import cats.effect.implicits.*

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.std.Console
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import cats.MonadThrow
import todo.services.Tasks
import todo.programs.*
import cats.effect.std.Supervisor
import todo.services.CommandParser

object Main extends IOApp.Simple {
  given parser: CommandParser[IO] = CommandParser.instance[IO] 

  def run: IO[Unit] = for {
      state <- Tasks.instance[IO]
      _ <- Cli.mainLoop(state)
    } yield ()
}
