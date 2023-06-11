package todo

import java.util.UUID

final case class Task(uuid: UUID, content: String)

trait Tasks[F[_]] {
  def add(task: Task): F[UUID]
  def getAll: F[List[Task]]
}

object Tasks {
  def apply[F[_]](using t: Tasks[F]) = t
}
