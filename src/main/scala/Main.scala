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

  def run: IO[Unit] =
    for {
      state <- Tasks.instance[IO]
      _     <- Cli.greet[IO]
      _     <- Cli.mainLoop(CommandParser.instance[IO], CommandRunner(state))
    } yield ()

}
