package tbank.academy.scala.labyrinths.solvers

import tbank.academy.scala.labyrinths.dto.{CellType, Maze, Path, Point}

import scala.annotation.tailrec

private case class AstarState(start: Point, end: Point, current: Point, previous: Option[AstarState], g: Int)

class AstarSolver extends Solver {
  private def h(state: AstarState): Int =
    (state.current.x - state.end.x).abs + (state.current.y - state.end.y).abs

  private def g(state: AstarState): Int =
    state.g

  private def f(state: AstarState): Int =
    g(state) + h(state)

  override def solve(maze: Maze, start: Point, end: Point): Option[Path] = {
    val visitedPoints = List[Point]()
    val queuedPoints = List(AstarState(start, end, start, None, 0))

    iterativeSolve(maze, visitedPoints, queuedPoints)
  }

  @tailrec
  private def iterativeSolve(maze: Maze, visitedPoints: List[Point], queuedPoints: List[AstarState]): Option[Path] = {
    if (queuedPoints.isEmpty) {
      None
    } else {
      val currentState = queuedPoints.minBy(f)

      if (currentState.current == currentState.end)
        Some(buildPath(currentState))
      else {
        val newVisitedPoints = visitedPoints :+ currentState.current
        val nextStates = maze
          .nearby(currentState.current)
          .filter(point => maze.cell(point) == Right(CellType.Empty))
          .filterNot(visitedPoints.contains)
          .map(point => AstarState(currentState.start, currentState.end, point, Some(currentState), currentState.g + 1))
        val newQueuedPoints = queuedPoints
          .filterNot(currentState.equals)
          .appendedAll(nextStates)

        iterativeSolve(maze, newVisitedPoints, newQueuedPoints)
      }
    }
  }

  private def buildPath(currentState: AstarState): Path =
    currentState.previous match {
      case None => Path(List(currentState.current))
      case Some(prev) => Path(buildPath(prev).points :+ currentState.current)
    }
}
