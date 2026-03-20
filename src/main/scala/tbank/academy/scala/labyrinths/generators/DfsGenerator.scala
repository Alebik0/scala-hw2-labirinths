package tbank.academy.scala.labyrinths.generators

import tbank.academy.scala.labyrinths.dto.{CellType, Maze, Point}
import tbank.academy.scala.labyrinths.error.{DomainError, InvalidMazeSizeError, UnexpectedError}

import scala.annotation.tailrec
import scala.util.Random

class DfsGenerator(seed: Int) extends Generator {
  private val random = new Random(seed)

  override def generate(width: Integer, height: Integer): Either[DomainError, Maze] = {
    if (width < 3 || height < 3) {
      Left(new InvalidMazeSizeError())
    } else {
      val initMaze = Maze.filledWithWalls(width, height)
      val initPoint = Point(1, 1)

      dfsBuild(initMaze, initPoint)
    }
  }

  private def nextCell(maze: Maze, point: Point): Either[DomainError, Boolean] =
    maze.cell(point) match {
      case Left(_) =>
        Left(new UnexpectedError())
      case Right(value) =>
        if (
          value == CellType.Wall
            && maze.nearbyCells(point).count(ct => ct == CellType.Empty) == 1
            && maze.isNotBorder(point)
        )
          Right(true)
        else
          Right(false)
    }

  private def dfsBuild(maze: Maze, point: Point): Either[DomainError, Maze] = {
    iterateNextCells(maze.edit(point, CellType.Empty), point)
  }

  @tailrec
  private def iterateNextCells(maze: Maze, point: Point): Either[DomainError, Maze] = {
    val nextCells = maze
      .nearby(point)
      .filter(
        cell => nextCell(maze, cell) match {
          case Left(_) => false
          case Right(value) => value
        }
      )

    if (nextCells.isEmpty)
      Right(maze)
    else
      random.shuffle(nextCells).headOption match {
        case None => Left(new UnexpectedError())
        case Some(nextPoint) =>
          dfsBuild(maze, nextPoint) match {
            case Left(error) => Left(error)
            case Right(postDfsMaze) =>
              iterateNextCells(postDfsMaze, point)
          }
      }
  }
}
