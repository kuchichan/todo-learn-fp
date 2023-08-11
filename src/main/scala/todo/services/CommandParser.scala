package todo.services

import todo.domain.command.* 
import cats.MonadThrow

trait CommandParser[F[_]] {
  def parseCommand(rawCommand: String): F[Command] 
}

object CommandParser {
  def apply[F[_]](using t: CommandParser[F]) = summon

  def instance[F[_]: MonadThrow]: CommandParser[F] =
    val addPattern = "^add (.*)".r
    val removePattern = "^rm (.*)".r
    val helpPattern = "(^h$)|(^help$)".r
    val listPattern = "(^ls$)|(^list$)".r
    val quitPattern = "(^q$)|(^quit$)".r

    new CommandParser[F] {
      def parseCommand(rawCommand: String) = MonadThrow[F].pure(rawCommand.trim() match {
        case addPattern(task) => Command.AddTask(task) 
        case removePattern(number) => Command.RemoveTask(number) 
        case quitPattern(_, _) => Command.Quit
        case helpPattern(_, _) => Command.Help
        case listPattern(_, _) => Command.ShowTasks
        case _ => Command.UnknownTask
      })
    }
     
}

