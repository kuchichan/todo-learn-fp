package todo.domain
import cats.syntax.all.*

import java.util.UUID

object task {

  enum State {
    case Todo
    case Done
    case Cancelled
  }

  opaque type TaskNum = Int

  object TaskNum {
    def apply(value: Int): TaskNum = value + 1
  }

  extension (x: TaskNum) {
    def toInt: Int = x - 1
  }

  case class Task(content: String, taskState: State)

  object Task { 
    def fromString(content: String): Option[Task] = 
       (!content.isEmpty()).guard[Option].as(Task(content, State.Todo))
  }
}
