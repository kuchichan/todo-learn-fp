package todo.services

import cats.Monad
import todo.domain.command.*

trait CommandParser[F[_]] {
  def parseCommand(rawCommand: String): F[Command]
}

object CommandParser {
  def apply[F[_]](using t: CommandParser[F]) = summon

  def instance[F[_]: Monad]: CommandParser[F] =
    val addPattern         = """^add(\W*)(.*)""".r
    val removePattern      = "^rm (.*)".r
    val changeStatePattern = "^t (.*)".r
    val helpPattern        = """(^h\W*)$|(^help\W*)$""".r
    val listPattern        = """(^ls\W*)$|(^list\W*)$""".r
    val quitPattern        = """(^q\W*)$|(^quit\W*)$""".r

    new CommandParser[F] {
      def parseCommand(rawCommand: String) = Monad[F].pure(rawCommand match {
        case addPattern("", "")         => Command.AddTask("")
        case addPattern("", _)          => Command.UnknownTask
        case addPattern(_, task)        => Command.AddTask(task)
        case removePattern(number)      => Command.RemoveTask(number)
        case changeStatePattern(number) => Command.ChangeState(number)
        case quitPattern(_, _)          => Command.Quit
        case helpPattern(_, _)          => Command.Help
        case listPattern(_, _)          => Command.ShowTasks
        case _                          => Command.UnknownTask
      })
    }

}
