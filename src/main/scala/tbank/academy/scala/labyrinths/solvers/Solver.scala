package tbank.academy.scala.labyrinths.solvers

import tbank.academy.scala.labyrinths.dto.{Maze, Path, Point}

trait Solver {
  def solve(maze: Maze, start: Point, end: Point): Option[Path]
}
