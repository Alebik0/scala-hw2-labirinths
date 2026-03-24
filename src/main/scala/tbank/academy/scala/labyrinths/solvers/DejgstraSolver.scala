package tbank.academy.scala.labyrinths.solvers

import tbank.academy.scala.labyrinths.dto.{CellType, Maze, Path, Point}

import scala.annotation.tailrec

/** @param start
  *   Стартовая позиция в лабиринте
  * @param end
  *   Конечная позиция в лабиринте
  * @param current
  *   Текущее положение в лабиринте
  * @param previous
  *   Прошлое состояние
  * @param g
  *   Расстояние от start
  */
private case class DijkstraState(start: Point, end: Point, current: Point, previous: Option[DijkstraState], g: Int)

/** Алгоритм Дейстры аналогичен алгоритму 0-1 BFS на наших лабиринтах, так что этот класс реализовывает второй алгоритм
  * с сохранением следующих ячеек в стеке
  */
class DijkstraSolver extends Solver {
  override def solve(maze: Maze, start: Point, end: Point): Option[Path] = {
    val visitedPoints = Set[Point]()
    val pointsStack   = List(DijkstraState(start, end, start, None, 0))

    iterativeSolve(maze, visitedPoints, pointsStack)
  }

  @tailrec
  private def iterativeSolve(maze: Maze, visitedPoints: Set[Point], pointsStack: List[DijkstraState]): Option[Path] =
    pointsStack match {
      case Nil =>
        None
      case currentState :: pointsStackTail =>
        if (currentState.current == currentState.end)
          Some(buildPath(currentState))
        else {
          val newVisitedPoints = visitedPoints + currentState.current
          val nextStates       = maze
            .nearby(currentState.current)
            .filter(point => maze.cell(point) == Right(CellType.Empty))
            .filterNot(visitedPoints.contains)
            .map(point =>
              DijkstraState(currentState.start, currentState.end, point, Some(currentState), currentState.g + 1)
            )
          val newQueuedPoints = pointsStackTail
            .appendedAll(nextStates)

          iterativeSolve(maze, newVisitedPoints, newQueuedPoints)
        }
    }

  private def buildPath(currentState: DijkstraState): Path =
    currentState.previous match {
      case None       => Path(List(currentState.current))
      case Some(prev) => Path(buildPath(prev).points :+ currentState.current)
    }
}
