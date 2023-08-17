package todo.services

import cats.Show
import cats.syntax.all.*
import todo.domain.task.State
import todo.domain.task.Task
import todo.domain.task.TaskNum

object RenderUtils {
  val ordHeader              = "No."
  val tasksHeader            = "Tasks"
  val stateHeader            = "State"
  val spaceDelimitersPerCell = 2

  given Show[TaskNum] = Show.show(tn => s"${tn}.")
  given Show[State]   = Show.show(st => st.toString().toUpperCase())

  extension (s: String) def ppad(padding: Int): String = s.padTo(padding, ' ')

  def renderTasks(tasks: Vector[Task]) =
    val max_content_pad = get_max_pad(tasks.map(_.content) :+ tasksHeader)
    val max_state_pad   = get_max_pad(tasks.map(_.taskState.show) :+ tasksHeader)
    val max_ord_num_pad = get_max_pad(Vector(ordHeader) :+ tasks.length.toString)
    val total           = max_ord_num_pad + max_ord_num_pad + max_state_pad

    val heading =
      s"| ${ordHeader.ppad(max_ord_num_pad)} | ${tasksHeader.ppad(max_content_pad)} | ${stateHeader
        .ppad(max_state_pad)} |"
    val bar =
      "+" + "-".repeat(max_ord_num_pad + spaceDelimitersPerCell) + "+" + "-".repeat(
        max_content_pad + spaceDelimitersPerCell
      ) + "+" + "-".repeat(max_state_pad + spaceDelimitersPerCell) + "+"

    tasks
      .zipWithIndex
      .map((t, i) =>
        s"| ${TaskNum(i).show.ppad(max_ord_num_pad)} | ${t
          .content
          .ppad(max_content_pad)} | ${t.taskState.show.ppad(max_state_pad)} |"
      )
      .mkString(s"$bar\n$heading\n$bar\n", "\n", s"\n$bar")

  private def get_max_pad(vec: Vector[String]): Int = vec.map(_.length()).max

}
