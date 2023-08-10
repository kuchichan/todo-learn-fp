package todo.domain

object command {
  enum Command {
    case Quit
    case Help
    case ShowTasks
    case UnknownTask
    case AddTask(taskText: String)
    case RemoveTask(taskNumber: String)
  }
}
