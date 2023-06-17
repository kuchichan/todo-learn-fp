package todo

import munit.CatsEffectSuite
import cats.effect.IO
import cats.syntax.functor.*
import cats.syntax.flatMap.*

class TasksTest extends CatsEffectSuite {
  val tasks = Tasks.instance[IO]

  test("Tasks get all without task return empty list") {
    tasks.flatMap(_.getAll).map(it => assertEquals(it, List.empty))
  }

  test("Tasks add task returns list with one task") {
    val testTask = Task("test")
    for {
      t <- tasks
      _ <- t.add(testTask)
      list <- t.getAll
    } yield assertEquals(list, List(testTask))
  }
}
