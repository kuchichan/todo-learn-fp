package todo.services

import cats.Show
import cats.syntax.all.*
import todo.domain.task.State
import todo.domain.task.Task
import todo.domain.task.TaskNum
import scala.io.AnsiColor.*

object RenderUtils {
  val ansiEscapeChars        = "\\u001B\\[\\d{1,2}m"
  val ordHeader              = "No."
  val tasksHeader            = "Tasks"
  val stateHeader            = "State"
  val spaceDelimitersPerCell = 2

  given Show[TaskNum] = Show.show(tn => s"${tn}.")

  given Show[State] = Show.show { st =>
    val asString = s"${BOLD}${st.toString().toUpperCase()}"
    st match
      case State.Todo      => s"${BLUE}${asString}${RESET}"
      case State.Done      => s"${GREEN}${asString}${RESET}"
      case State.Cancelled => s"${RED}${asString}${RESET}"

  }

  extension (s: String)

    def ppad(padding: Int): String =
      val difference = s.length() - s.replaceAll(ansiEscapeChars, "").length()
      s.padTo(padding + difference, ' ')

  def renderTasks(tasks: Vector[Task]) =
    val maxContentPad = getMaxPad(tasks.map(_.content) :+ tasksHeader)
    val maxStatePad   = getMaxPad(tasks.map(_.taskState.toString()) :+ stateHeader)
    val maxOrdNumPad  = getMaxPad(Vector(ordHeader) :+ tasks.length.toString)
    val total         = maxOrdNumPad + maxContentPad + maxStatePad

    val heading =
      s"| ${ordHeader.ppad(maxOrdNumPad)} | ${tasksHeader.ppad(maxContentPad)} | ${stateHeader
        .ppad(maxStatePad)} |"
    val bar =
      "+" + "-".repeat(maxOrdNumPad + spaceDelimitersPerCell) + "+" + "-".repeat(
        maxContentPad + spaceDelimitersPerCell
      ) + "+" + "-".repeat(maxStatePad + spaceDelimitersPerCell) + "+"

    tasks
      .zipWithIndex
      .map((t, i) =>
        s"| ${TaskNum(i).show.ppad(maxOrdNumPad)} | ${t
          .content
          .ppad(maxContentPad)} | ${t.taskState.show.ppad(maxStatePad)} |"
      )
      .mkString(s"$bar\n$heading\n$bar\n", "\n", s"\n$bar")

  private def getMaxPad(vec: Vector[String]): Int = vec.map(_.length()).max
}
