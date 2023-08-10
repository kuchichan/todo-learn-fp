package todo.services

import todo.domain.task.*

import cats.implicits.*
import cats.instances.uuid
import cats.syntax.*
import cats.MonadThrow
import cats.effect.kernel.Ref


trait Tasks[F[_]] {
  def add(task: Task): F[TaskNum]
  def getAll: F[Vector[Task]]
}

object Tasks {
  type InMemoryTaskRegistry = Vector[Task]

  def apply[F[_]](using t: Tasks[F]) = t

  def instance[F[_]: MonadThrow: Ref.Make]: F[Tasks[F]] = inMemory[F]

  def inMemory[F[_]: MonadThrow: Ref.Make]: F[Tasks[F]] = 
    Ref[F].of(Vector.empty[Task]).map { s =>
      new Tasks[F] {
        def add(task: Task): F[TaskNum] =
          for {
            _ <- s.update(v => v :+ task)
            state <- s.get
          } yield TaskNum.apply(state.indexOf(task))

        def getAll: F[Vector[Task]] = s.get
      }
    }
}
