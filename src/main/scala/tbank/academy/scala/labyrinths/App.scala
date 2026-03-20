package tbank.academy.scala.labyrinths

import cats.effect.{ExitCode, IO, IOApp}
import tbank.academy.scala.labyrinths.error.{DomainError, HeightNotFoundError, WidthNotFoundError}
import tbank.academy.scala.labyrinths.generators.{DfsGenerator, Generator, PrimGenerator}

object App extends IOApp {
  private val DEFAULT_SEED = 123

  private def DEFAULT_GENERATOR(seed: Int) = new DfsGenerator(seed)

  override def run(args: List[String]): IO[ExitCode] = {
    if (parseHelp(args)) {
      printHelp()
      IO(ExitCode.Success)
    } else {
      args.headOption match {
        case None =>
          printHelp()
          IO(ExitCode.Error)
        case Some("solve") =>
          runSolve()
        case Some("generate") =>
          runGenerate(args)
        case Some(_) =>
          printHelp()
          IO(ExitCode.Error)
      }
    }
  }

  private def runSolve(): IO[ExitCode] = {
    IO(ExitCode.Success)
  }

  private def runGenerate(args: List[String]): IO[ExitCode] = {
    val seed = parseSeed(args)
    val generator = parseGenerator(seed, args)
    parseWidth(args) match {
      case Left(error) =>
        println(s"Error occurred: $error")
        IO(ExitCode.Error)
      case Right(width) =>
        parseHeight(args) match {
          case Left(error) =>
            println(s"Error occurred: $error")
            IO(ExitCode.Error)
          case Right(height) =>
            generator.generate(width, height) match {
              case Left(error) =>
                println(s"Error occurred: $error")
                IO(ExitCode.Error)
              case Right(maze) =>
                println(maze.toHumanReadableString)
                IO(ExitCode.Success)
            }
        }
    }
  }

  private def parseHelp(args: List[String]): Boolean =
    args == List("--help") || args == List("-h")

  private def parseWidth(args: List[String]): Either[DomainError, Int] = {
    val maybeWidth = args
      .drop(1)
      .findLast(arg => arg.startsWith("--width="))
      .flatMap(arg => arg.drop(8).toIntOption)

    maybeWidth match {
      case Some(width) => Right(width)
      case None => Left(new WidthNotFoundError())
    }
  }

  private def parseHeight(args: List[String]): Either[DomainError, Int] = {
    val maybeHeight = args
      .drop(1)
      .findLast(arg => arg.startsWith("--height="))
      .flatMap(arg => arg.drop(9).toIntOption)

    maybeHeight match {
      case Some(height) => Right(height)
      case None => Left(new HeightNotFoundError())
    }
  }

  private def parseSeed(args: List[String]): Int =
    args
      .drop(1)
      .findLast(arg => arg.startsWith("--seed="))
      .flatMap(arg => arg.drop(7).toIntOption)
      .getOrElse(DEFAULT_SEED)

  private def parseGenerator(seed: Int, args: List[String]): Generator =
    args
      .drop(1)
      .findLast(arg => arg.startsWith("--algorithm="))
      .flatMap(
        arg => arg.drop(12) match {
          case "dfs" => Some(new DfsGenerator(seed))
          case "prim" => Some(new PrimGenerator(seed))
          case _ => None
        }
      )
      .getOrElse(DEFAULT_GENERATOR(seed))

  private def printHelp(): Unit = {
    println(
      """Usage:
        |  <COMMAND> --width=<WIDTH> --height=<HEIGHT> [--algorithm=<ALGORITHM>] [--seed=<SEED>]
        |
        |Description:
        |  Executes maze generator or solver (based on <COMMAND> value)
        |
        |  <COMMAND> must be one of:
        |    generate    Generate maze
        |    solve       Find shortest path in the provided maze
        |
        |Required Arguments:
        |  <COMMAND>
        |      The operation to perform. Must be either "generate" or "solve".
        |
        |  --width=<WIDTH>
        |      The width of the output. Must be a positive integer.
        |
        |  --height=<HEIGHT>
        |      The height of the output. Must be a positive integer.
        |
        |Optional Arguments:
        |  --algorithm=<ALGORITHM>
        |      The generator to use.
        |      Default: dfs
        |      Options may include: dfs, prim
        |
        |  --seed=<SEED>
        |      Seed value for randomization.
        |      Default: 123
        |
        |  -h, --help
        |      Show this help message and exit.
        |
        |Examples:
        |  generate --width 50 --height 20
        |
        |  generate --width 50 --height 20 --algorithm dfs
        |
        |  generate --width 50 --height 20 --seed 999
        |
        |  generate --width 50 --height 20 --algorithm prim --seed 42""".stripMargin
    )
  }
}
