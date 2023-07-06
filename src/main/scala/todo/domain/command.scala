package todo.domain

object command {
  enum Command {
    case Quit
    case ShowTasks
    case AddTask(taskText: String)
    case RemoveTask(taskNumber: Int)
  }
}
