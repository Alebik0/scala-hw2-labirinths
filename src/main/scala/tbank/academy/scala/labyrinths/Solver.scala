package tbank.academy.scala.labyrinths

import tbank.academy.scala.labyrinths.dto.{Maze, Path, Point}

trait Solver {
    def solve[F[_]](maze: Maze, start: Point, end: Point): F[Option[Path]]
}
