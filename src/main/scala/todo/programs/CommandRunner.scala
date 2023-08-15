package todo.programs

import cats.Monad

import cats.effect.std.Console
import cats.syntax.all.*
import todo.domain.command.*
import todo.services.Tasks
import todo.domain.task.Task
import cats.MonadThrow
import cats.data.EitherT

class CommandRunner[F[_]: Console: MonadThrow](tasks: Tasks[F]) {
  val addHelp = """
  |Add usage: 
  |add <task> - Adds a new task to the list. 
  """.stripMargin

  val help =
    """
  |Available Commands:
  |rm <num> - Removes a task number <num> from the list. 
  |ls OR list - List your tasks.
  |q OR quit - Exits the application.
  |h OR help - Display help (the same you are reading right now).
  """.stripMargin

  def doQuit(): F[CommandResult] = Console[F]
    .println("Goodbye!")
    .map(_ => ValidResult.Terminate.asRight[CommandError])

  def doUnknown(): F[CommandResult] =
    for {
      _ <- Console[F].println("Sorry, I do not know that command!")
      _ <- Console[F].println(help)
    } yield ValidResult.OK.asRight[CommandError]

  def doHelp(): F[CommandResult] =
    for {
      _ <- Console[F].println(help)
    } yield ValidResult.OK.asRight[CommandError]

  def doAddHelp(): F[CommandResult] = Console[F]
    .println(addHelp)
    .map(_ => ValidResult.OK.asRight[CommandError])

  def doAddCommand(task: String): F[CommandResult] =
    for {
      taskOrError <- ensureNonEmpty(task)
      result      <- processTask(taskOrError)
    } yield result

  def doShow(): F[CommandResult] =
    for {
      allTasks <- tasks.getAll
      _        <- Console[F].println(allTasks)
    } yield ValidResult.OK.asRight[CommandError]

  def runCommand(command: Command): F[CommandResult] =
    command match {
      case Command.Quit        => doQuit()
      case Command.UnknownTask => doUnknown()
      case Command.AddTask(t)  => doAddCommand(t)
      case Command.ShowTasks   => doShow()
      case Command.Help        => doHelp()
      case Command.AddHelp     => doAddHelp()
      case _                   => MonadThrow[F].pure(ValidResult.OK.asRight[CommandError])
    }

  private def ensureNonEmpty(task: String): F[Either[CommandError, Task]] = MonadThrow[F]
    .fromOption(Task.fromString(task), CommandError("You cannot add empty task!"))
    .attemptNarrow[CommandError]

  private def processTask(taskOrError: Either[CommandError, Task]): F[CommandResult] =
    taskOrError match
      case Left(value) => MonadThrow[F].pure(value.asLeft[ValidResult])
      case Right(t)    => tasks.add(t) >> MonadThrow[F].pure(ValidResult.OK.asRight[CommandError])

}
