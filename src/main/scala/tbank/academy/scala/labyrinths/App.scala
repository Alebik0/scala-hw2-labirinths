package tbank.academy.scala.labyrinths

import cats.effect.{ExitCode, IO, IOApp}
import tbank.academy.scala.labyrinths.dto.{CellType, Maze}
import tbank.academy.scala.labyrinths.error.{DomainError, HeightNotFoundError, WidthNotFoundError}
import tbank.academy.scala.labyrinths.generators.{DfsGenerator, Generator}

object App extends IOApp {
  private val DEFAULT_SEED = 123

  private def DEFAULT_GENERATOR(seed: Int) = new DfsGenerator(seed)

  override def run(args: List[String]): IO[ExitCode] = {
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
                printMaze(maze)
            }
        }
    }
  }

  private def printMaze(maze: Maze): IO[ExitCode] = {
    println(
      maze
        .cells
        .map(
          row => row
            .map {
              case CellType.Empty => " "
              case CellType.End => "X"
              case CellType.Path => "."
              case CellType.Start => "O"
              case CellType.Wall => "#"
              case _ => ""
            }
            .mkString
        )
        .mkString("\n")
    )

    IO(ExitCode.Success)
  }

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
          case _ => None
        }
      )
      .getOrElse(DEFAULT_GENERATOR(seed))

  private def printHelp() = {
    println("TODO")
  }
}
