package todo.services

import java.util.UUID
import cats.implicits.*
import cats.Monad
import cats.instances.uuid
import cats.syntax.*
import cats.MonadThrow
import cats.effect.kernel.Ref
import todo.domain.task.*
import cats.effect.std.UUIDGen


trait Tasks[F[_]] {
  def add(task: Task): F[TaskId]
  def getAll: F[List[Task]]
}

object Tasks {
  type InMemoryTaskRegistry = Map[TaskId, Task]

  def apply[F[_]](using t: Tasks[F]) = t

  def instance[F[_]: MonadThrow: Ref.Make]: F[Tasks[F]] = inMemory[F]

  def inMemory[
    F[_]: MonadThrow: Ref.Make
  ]: F[Tasks[F]] = Ref[F].of(Map.empty[TaskId, Task]).map { s =>
    new Tasks[F] {
      def add(task: Task): F[TaskId] =
        for {
          state <- s.get
          _ <- s.set(state + (task.taskId -> task))
        } yield task.taskId

      def getAll: F[List[Task]] = s.get.map(_.values.toList)
    }
  }

}
