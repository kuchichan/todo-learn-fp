import cats.effect.implicits.*
import cats.effect.IO
import cats.effect.IOApp
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import todo.programs.*
import todo.services.*

object Main extends IOApp.Simple {

  def run: IO[Unit] =
    for {
      state <- Tasks.instance[IO]
      _     <- Cli.greet[IO]
      _     <- Cli.mainLoop(CommandParser.instance[IO], CommandRunner(state))
    } yield ()

}
