package tbank.academy.scala.labyrinths.generators

import tbank.academy.scala.labyrinths.dto.{CellType, Maze, Point}
import tbank.academy.scala.labyrinths.error.{DomainError, InvalidMazeSizeError}

import scala.annotation.tailrec
import scala.util.Random

class PrimGenerator(seed: Int) extends Generator {
  private val random = new Random(seed)

  override def generate(width: Integer, height: Integer): Either[DomainError, Maze] = {
    if (width < 3 || height < 3) {
      Left(new InvalidMazeSizeError())
    } else {
      val initMaze  = Maze.filledWithWalls(width, height)
      val initPoint = Point(1, 1)

      Right(primBuild(initMaze, initPoint))
    }
  }

  private def primBuild(
      maze: Maze,
      point: Point,
      wallsStack: List[Point] = List()
  ): Maze = {
    val updatedMaze    = maze.edit(point, CellType.Empty)
    val appendingWalls = updatedMaze
      .nearby(point)
      .filter(p => updatedMaze.cell(p) == Right(CellType.Wall))
      .filterNot(updatedMaze.isBorder)
      .filterNot(wallsStack.contains)
    val updatedWallsStack = wallsStack
      .filterNot(point.equals)
      .appendedAll(appendingWalls)

    iterateWallChoose(updatedMaze, point, updatedWallsStack)
  }

  @tailrec
  private def iterateWallChoose(
      maze: Maze,
      point: Point,
      wallStack: List[Point]
  ): Maze = {
    if (wallStack.isEmpty)
      maze
    else {
      val chosenWall       = wallStack(random.nextInt(wallStack.length))
      val nearbyEmptyCount = maze
        .nearbyCells(chosenWall)
        .count(cellType => cellType == CellType.Empty)

      if (nearbyEmptyCount == 1)
        primBuild(maze, chosenWall, wallStack)
      else
        iterateWallChoose(maze, point, wallStack.filterNot(p => p == chosenWall))
    }
  }
}
