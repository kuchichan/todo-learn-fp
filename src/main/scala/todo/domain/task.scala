package todo.domain 

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
  extension(x: TaskNum) {
    def toInt: Int = x - 1  
  }
  case class Task(content: String, taskState: State)
}
