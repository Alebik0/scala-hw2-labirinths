package tbank.academy.scala.labyrinths.generators

import tbank.academy.scala.labyrinths.dto.{CellType, Maze, Point}
import tbank.academy.scala.labyrinths.error.{DomainError, InvalidMazeSizeError}

import scala.annotation.tailrec
import scala.util.Random

class DfsGenerator(seed: Int) extends Generator {
  private val random = new Random(seed)

  override def generate(width: Integer, height: Integer): Either[DomainError, Maze] = {
    if (width < 3 || height < 3) {
      Left(new InvalidMazeSizeError())
    } else {
      val initMaze  = Maze.filledWithWalls(width, height)
      val initPoint = Point(1, 1)

      Right(dfsBuild(initMaze, initPoint))
    }
  }

  private def dfsBuild(maze: Maze, point: Point): Maze = {
    val updatedMaze = maze.edit(point, CellType.Empty)

    iterateNearbyWalls(updatedMaze, point)
  }

  @tailrec
  private def iterateNearbyWalls(maze: Maze, point: Point): Maze = {
    val nearbyWalls = maze
      .nearby(point)
      .filter(p => maze.cell(p) == Right(CellType.Wall))
      .filter(maze.isNotBorder)
      .filter(p => maze.nearbyCells(p).count(cellType => cellType == CellType.Empty) == 1)

    random.shuffle(nearbyWalls).headOption match {
      case None            => maze
      case Some(nextPoint) =>
        val postDfsMaze = dfsBuild(maze, nextPoint)
        iterateNearbyWalls(postDfsMaze, point)
    }
  }
}
