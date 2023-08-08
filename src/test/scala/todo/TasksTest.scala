import todo.services.*
import todo.domain.*

import munit.CatsEffectSuite
import cats.effect.IO
import cats.syntax.functor.*
import cats.syntax.flatMap.*

class TasksTest extends CatsEffectSuite {
  val tasks = Tasks.instance[IO]
  val testTask = task.Task(content="Hello", taskState=task.State.Done) 

  test("Tasks get all without task return empty list") {
    tasks.flatMap(_.getAll).map(it => assertEquals(it, Vector.empty))
  }
  test("Task add returns task number that is greater than one than index") {
    for {
      t <- tasks
      taskNum <- t.add(testTask)
      taskVec <- t.getAll
    } yield assertEquals(taskVec.indexOf(testTask), taskNum.toInt)
  }

  test("Tasks add task returns list with one task") {

    for {
      t <- tasks
      _ <- t.add(testTask)
      list <- t.getAll
    } yield assertEquals(list, Vector(testTask))
  }
}
