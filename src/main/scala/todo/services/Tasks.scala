package todo.services

import cats.MonadThrow
import cats.effect.kernel.Ref
import cats.implicits.*
import cats.instances.uuid
import cats.syntax.*
import todo.domain.task.*
import todo.services.Utils.*

trait Tasks[F[_]] {
  def add(task: Task): F[TaskNum]
  def getAll: F[Vector[Task]]
  def changeTaskState(taskNum: TaskNum): F[Unit]
  def removeTask(taskNum: TaskNum): F[Unit]
}

object Tasks {
  type InMemoryTaskRegistry = Vector[Task]

  def apply[F[_]](using t: Tasks[F]) = t

  def instance[F[_]: MonadThrow: Ref.Make]: F[Tasks[F]] = inMemory[F]

  def inMemory[F[_]: MonadThrow: Ref.Make]: F[Tasks[F]] = Ref[F].of(Vector.empty[Task]).map { s =>
    new Tasks[F] {
      def add(task: Task): F[TaskNum] =
        for {
          _     <- s.update(v => v :+ task)
          state <- s.get
        } yield TaskNum.apply(state.indexOf(task))

      def removeTask(taskNum: TaskNum): F[Unit] =
        for {
          _ <- s.update(v => Utils.removeFromVectorAtIndex(v, taskNum.toInt))
        } yield ()

      def changeTaskState(taskNum: TaskNum): F[Unit] = 
        for {
          tasks <- s.get
          task <- MonadThrow[F]
            .fromOption(tasks.get(taskNum.toInt), new UnsupportedOperationException)
          _ <- s.update(v =>
            v.updated(taskNum.toInt, task.copy(taskState = State.cycle(task.taskState)))
          )
        } yield ()

      def getAll: F[Vector[Task]] = s.get
    }
  }

}
