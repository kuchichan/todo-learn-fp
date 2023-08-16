package todo.programs

import cats.Monad

import cats.effect.std.Console
import cats.syntax.all.*
import cats.implicits.*
import todo.domain.command.*
import todo.services.Tasks
import todo.domain.task.Task
import cats.MonadThrow
import cats.data.EitherT
import todo.domain.task.TaskNum
import todo.domain.RenderUtils

class CommandRunner[F[_]: Console: MonadThrow](tasks: Tasks[F]) {

  val addHelp =
    """
  |Add usage: 
  |add <task> - Adds a new task to the list. 
  """.stripMargin

  val help =
    """
  |Available Commands:
  |rm <num> - Removes a task number <num> from the list. 
  |t <num> - Cycle through task state [DONE, TODO, CANCELLED] for task with <num>. 
  |ls OR list - List your tasks.
  |q OR quit - Exits the application.
  |h OR help - Display help (the same you are reading right now).
  """.stripMargin

  def runQuit(): F[CommandResult] = Console[F]
    .println("Goodbye!")
    .map(_ => ValidResult.Terminate.asRight[CommandError])

  def runUnknown(): F[CommandResult] =
    for {
      _ <- Console[F].println("Sorry, I do not know that command!")
      _ <- Console[F].println(help)
    } yield ValidResult.OK.asRight[CommandError]

  def runHelp(): F[CommandResult] =
    for {
      _ <- Console[F].println(help)
    } yield ValidResult.OK.asRight[CommandError]

  def runAddCommand(task: String): F[CommandResult] =
    for {
      taskOrError <- ensureNonEmpty(task)
      result      <- processAddTask(taskOrError)
    } yield result

  def runShow(): F[CommandResult] =
    for {
      allTasks <- tasks.getAll
      _        <- Console[F].println(RenderUtils.renderTasks(allTasks))
    } yield ValidResult.OK.asRight[CommandError]

  def runRemove(taskNum: String): F[CommandResult] =
    for {
      numOrError <- ensureTaskNum(taskNum)
      result     <- ensureStateCommand(numOrError, tasks.removeTask)
    } yield result

  def runChangeState(taskNum: String): F[CommandResult] =
    for {
      numOrError <- ensureTaskNum(taskNum)
      result     <- ensureStateCommand(numOrError, tasks.changeTaskState)
    } yield result

  def runCommand(command: Command): F[CommandResult] =
    command match {
      case Command.Quit           => runQuit()
      case Command.UnknownTask    => runUnknown()
      case Command.AddTask(t)     => runAddCommand(t)
      case Command.ShowTasks      => runShow()
      case Command.Help           => runHelp()
      case Command.RemoveTask(n)  => runRemove(n)
      case Command.ChangeState(n) => runChangeState(n)
    }

  private def ensureNonEmpty(task: String): F[Either[CommandError, Task]] = MonadThrow[F]
    .fromOption(Task.fromString(task), CommandError(s"You cannot add empty task!\n${addHelp}"))
    .attemptNarrow[CommandError]

  private def processAddTask(taskOrError: Either[CommandError, Task]): F[CommandResult] =
    taskOrError match
      case Left(value) => MonadThrow[F].pure(value.asLeft[ValidResult])
      case Right(t)    => tasks.add(t) >> MonadThrow[F].pure(ValidResult.OK.asRight[CommandError])

  private def ensureTaskNum(taskNum: String): F[Either[CommandError, TaskNum]] = MonadThrow[F].pure(
    taskNum
      .asRight[CommandError]
      .ensure(CommandError("Task number should contain only digits."))(_.forall(_.isDigit))
      .map(num => num.toInt)
      .ensure(CommandError("Number should be positive."))(_ > 0)
      .map(TaskNum.fromInput(_))
  )

  private def ensureStateCommand(
    taskNum: Either[CommandError, TaskNum],
    command: (taskNum: TaskNum) => F[Unit],
  ): F[CommandResult] =
    taskNum match {
      case Left(value) => MonadThrow[F].pure(value.asLeft[ValidResult])
      case Right(num) =>
        command(num)
          .attempt
          .map(
            _.fold(
              _ => CommandError("Task not found.").asLeft[ValidResult],
              _ => ValidResult.OK.asRight[CommandError],
            )
          )
    }

}
