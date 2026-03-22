package tbank.academy.scala.labyrinths

import cats.effect.{ExitCode, IO, IOApp}
import tbank.academy.scala.labyrinths.dto.{CellType, Maze}
import tbank.academy.scala.labyrinths.error.DomainError

import java.io.File
import java.nio.file.Files
import scala.jdk.CollectionConverters.ListHasAsScala

object App extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    if (Parser.parseHelp(args)) {
      printHelp()
      IO(ExitCode.Success)
    } else
      args.headOption match {
        case None =>
          printHelp()
          IO(ExitCode.Error)
        case Some("solve") =>
          runSolve(args)
        case Some("generate") =>
          runGenerate(args)
        case Some(_) =>
          printHelp()
          IO(ExitCode.Error)
      }
  }

  private def runSolve(args: List[String]): IO[ExitCode] =
    Parser.parseAlgorithm(args) match {
      case Left(error) => raiseError(error)
      case Right(solver) =>
        Parser.parseMazeFile(args) match {
          case Left(error) => raiseError(error)
          case Right(mazeFile) =>
            val maze = readMaze(mazeFile)

            Parser.parseStart(args) match {
              case Left(error) => raiseError(error)
              case Right(startPoint) =>
                Parser.parseEnd(args) match {
                  case Left(error) => raiseError(error)
                  case Right(endPoint) =>
                    Parser.parseOutput(args) match {
                      case Left(error) => raiseError(error)
                      case Right(output) =>
                        solver.solve(maze, startPoint, endPoint) match {
                          case None =>
                            output.write("No path found".getBytes)
                            IO(ExitCode.Success)
                          case Some(path) =>
                            output.write(path.points.map(p => s"${p.x},${p.y}").mkString("\n").getBytes)
                            IO(ExitCode.Success)
                        }
                    }
                }
            }
        }
    }

  private def cellTypeFromChar(char: Char): CellType =
    char match {
      case '#' => CellType.Wall
      case '.' => CellType.Path
      case ' ' => CellType.Empty
      case 'X' => CellType.End
      case 'O' => CellType.Start
      case _ => CellType.Wall
    }

  private def readMaze(mazeFile: File): Maze =
    Maze(
      Files
        .readAllLines(mazeFile.toPath)
        .asScala
        .map(
          line =>
            line
              .toList
              .map(cellTypeFromChar)
              .toVector
        )
        .toVector
    )

  private def raiseError(error: DomainError) = {
    println(s"Error occurred: $error")
    IO(ExitCode.Error)
  }

  private def runGenerate(args: List[String]): IO[ExitCode] = {
    val seed = Parser.parseSeed(args)
    val generator = Parser.parseGenerator(seed, args)
    Parser.parseWidth(args) match {
      case Left(error) =>
        raiseError(error)
      case Right(width) =>
        Parser.parseHeight(args) match {
          case Left(error) =>
            raiseError(error)
          case Right(height) =>
            generator.generate(width, height) match {
              case Left(error) =>
                raiseError(error)
              case Right(maze) =>
                println(maze.toHumanReadableString)
                IO(ExitCode.Success)
            }
        }
    }
  }

  private def printHelp(): Unit =
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
