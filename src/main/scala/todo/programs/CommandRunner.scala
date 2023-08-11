package todo.programs

import cats.Monad
import cats.effect.std.Console
import cats.syntax.all.*
import todo.domain.command.*
import todo.services.Tasks

class CommandRunner[F[_]: Console: Monad](tasks: Tasks[F]) {

  def doQuit(): F[CommandResult] = Console[F]
    .println("Goodbye!")
    .map(_ => ValidResult.Terminate.asRight[CommandError])

  def doUnknown(): F[CommandResult] = Console[F]
    .println("Sorry, I do not know that command!")
    .map(_ => ValidResult.OK.asRight[CommandError])

  def runCommand(command: Command): F[CommandResult] =
    command match {
      case Command.Quit        => doQuit()
      case Command.UnknownTask => doUnknown()
      case _                   => Monad[F].pure(ValidResult.OK.asRight[CommandError])
    }

}
