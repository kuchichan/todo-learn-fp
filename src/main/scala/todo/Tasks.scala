package todo

import java.util.UUID
import cats.implicits.*
import cats.Monad
import cats.instances.uuid
import cats.syntax.*
import cats.MonadThrow
import cats.effect.kernel.Ref

final case class Task(content: String)

trait Tasks[F[_]] {
  def add(task: Task): F[UUID]
  def getAll: F[List[Task]]
}

object Tasks {
  type InMemoryTaskRegistry = Map[UUID, Task]

  def apply[F[_]](using t: Tasks[F]) = t

  def instance[F[_]: MonadThrow: Ref.Make]: F[Tasks[F]] = inMemory[F]

  def inMemory[
    F[_]: MonadThrow: Ref.Make
  ]: F[Tasks[F]] = Ref[F].of(Map.empty[UUID, Task]).map { s =>
    new Tasks[F] {
      def add(task: Task): F[UUID] =
        for {
          state <- s.get
          uuid <- UUID.randomUUID().pure[F]
          _ <- s.set(state + (uuid -> task))
        } yield uuid

      def getAll: F[List[Task]] = s.get.map(_.values.toList)
    }
  }

}
