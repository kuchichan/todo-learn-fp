package todo.domain

object command {
  type CommandResult = Either[CommandError, ValidResult]

  enum Command {
    case Quit
    case Help
    case ShowTasks
    case UnknownTask
    case AddTask(taskText: String)
    case RemoveTask(taskNumber: String)
  }

  enum ValidResult {
    case OK
    case Terminate
  }

  case class CommandError(errorMsgs: List[String])
}
