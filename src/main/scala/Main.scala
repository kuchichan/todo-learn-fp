import cats.MonadThrow
import cats.effect.implicits.*
import cats.effect.std.{Console, Supervisor}
import cats.effect.{IO, IOApp}
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import todo.programs.*
import todo.services.{CommandParser, Tasks}

object Main extends IOApp.Simple {

  def run: IO[Unit] =
    for {
      state <- Tasks.instance[IO]
      _     <- Cli.greet[IO]
      _     <- Cli.mainLoop(CommandParser.instance[IO], CommandRunner(state))
    } yield ()

}
