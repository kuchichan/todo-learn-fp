package todo.domain 

import java.util.UUID

object task {
  enum State {
    case Todo
    case Done
    case Cancelled
  }
  case class TaskId(value: UUID)
  case class Task(taskId: TaskId, content: String, taskState: State)
}
