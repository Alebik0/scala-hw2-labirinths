package tbank.academy.scala.labyrinths

import cats.effect.{ExitCode, IO, IOApp}
import tbank.academy.scala.labyrinths.dto.{CellType, Maze, Path, Point}
import tbank.academy.scala.labyrinths.error.{DomainError, InputFileReadError}
import tbank.academy.scala.labyrinths.parsers.{GeneratorParser, Parser, SolverParser}

import java.io.File
import java.nio.file.Files
import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.{Failure, Success, Try}

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
    SolverParser.parseAlgorithm(args) match {
      case Left(error)   => raiseError(error)
      case Right(solver) =>
        SolverParser.parseMazeFile(args) match {
          case Left(error)     => raiseError(error)
          case Right(mazeFile) =>
            readMaze(mazeFile) match {
              case Left(error) => raiseError(error)
              case Right(maze) =>
                SolverParser.parseStart(args) match {
                  case Left(error)       => raiseError(error)
                  case Right(startPoint) =>
                    SolverParser.parseEnd(args) match {
                      case Left(error)     => raiseError(error)
                      case Right(endPoint) =>
                        SolverParser.parseOutput(args) match {
                          case Left(error)   => raiseError(error)
                          case Right(output) =>
                            solver.solve(maze, startPoint, endPoint) match {
                              case None =>
                                output.write("No path found".getBytes)
                                output.close()
                                IO(ExitCode.Success)
                              case Some(path) =>
                                val outputMaze = updateMaze(maze, path, startPoint, endPoint)
                                output.write(outputMaze.toHumanReadableString.getBytes)
                                output.close()
                                IO(ExitCode.Success)
                            }
                        }
                    }
                }
            }
        }
    }

  private def updateMaze(maze: Maze, path: Path, start: Point, end: Point): Maze =
    Maze(
      maze
        .cells
        .zipWithIndex
        .map(rowWithIndex =>
          rowWithIndex
            ._1
            .zipWithIndex
            .map(columnWithIndex => {
              val current = Point(columnWithIndex._2, rowWithIndex._2)

              if (current == start)
                CellType.Start
              else if (current == end)
                CellType.End
              else if (path.points.contains(current))
                CellType.Path
              else
                columnWithIndex._1
            })
        )
    )

  private def cellTypeFromChar(char: Char): CellType =
    char match {
      case '#' => CellType.Wall
      case '.' => CellType.Path
      case ' ' => CellType.Empty
      case 'X' => CellType.End
      case 'O' => CellType.Start
      case _   => CellType.Wall
    }

  private def readMaze(mazeFile: File): Either[DomainError, Maze] = {
    Try(Files.readAllLines(mazeFile.toPath)) match {
      case Failure(exception) =>
        Left(InputFileReadError(exception.toString))
      case Success(fileLines) =>
        Right(Maze(
          fileLines
            .asScala
            .map(line =>
              line
                .toList
                .map(cellTypeFromChar)
                .toVector
            )
            .toVector
        ))
    }
  }

  private def raiseError(error: DomainError): IO[ExitCode] = {
    println(s"Error occurred: $error")
    IO(ExitCode.Error)
  }

  private def runGenerate(args: List[String]): IO[ExitCode] = {
    val seed      = GeneratorParser.parseSeed(args)
    val generator = GeneratorParser.parseGenerator(seed, args)
    GeneratorParser.parseWidth(args) match {
      case Left(error) =>
        raiseError(error)
      case Right(width) =>
        GeneratorParser.parseHeight(args) match {
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
        |Generator Arguments:
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
        |Solver arguments
        |  --algorithm=<ALGORITHM>
        |      Algorithm to find path. "astar" or "dijkstra"
        |
        |  --file=<FILE>
        |      Maze input file.
        |
        |  --start=<START>
        |      Start point. Format: "x,y".
        |
        |  --end=<END>
        |      End point. Format: "x,y".
        |
        |Optional Arguments:
        |  --output=<OUTPUT>
        |      Output file. Solver will write to the stdout if not provided.
        |
        |Examples:
        |  generate --width 50 --height 20
        |
        |  generate --width 50 --height 20 --algorithm dfs
        |
        |  generate --width 50 --height 20 --seed 999
        |
        |  generate --width 50 --height 20 --algorithm prim --seed 42
        |
        |  solve --algorithm=astar --file=/tests/cases/4_check_maze_solving/maze.txt --start=2,2 --end=10,10
        |  """.stripMargin
    )
}
