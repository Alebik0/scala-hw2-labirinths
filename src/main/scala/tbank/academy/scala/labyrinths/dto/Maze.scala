package tbank.academy.scala.labyrinths.dto

import tbank.academy.scala.labyrinths.error.{DomainError, IndexOutOfBoundsError}

case class Maze(cells: Vector[Vector[CellType]]) {
  def height: Int = cells.length

  def width: Int = cells.headOption match {
    case Some(value) => value.length
    case None => 0
  }

  private def contains(point: Point): Boolean =
    0 <= point.y &&
      point.y < cells.length &&
      0 <= point.x &&
      point.x < cells(point.y).length

  def isBorder(point: Point): Boolean =
    contains(point) && (
      0 == point.y ||
        0 == point.x ||
        point.y == cells.length - 1 ||
        point.x == cells(point.y).length - 1
      )

  def isNotBorder(point: Point): Boolean = !isBorder(point)

  def cell(point: Point): Either[DomainError, CellType] =
    if (contains(point))
      Right(cells(point.y)(point.x))
    else
      Left(new IndexOutOfBoundsError())

  def nearby(point: Point): List[Point] =
    point
      .nearby()
      .filter(contains)

  def nearbyCells(point: Point): List[CellType] =
    point
      .nearby()
      .filter(contains)
      .map(p => cells(p.y)(p.x))

  def edit(point: Point, cellType: CellType): Maze =
    Maze(
      cells
        .zipWithIndex
        .map(
          rowWithIndex =>
            if (rowWithIndex._2 == point.y)
              rowWithIndex
                ._1
                .zipWithIndex
                .map(
                  colWithIndex =>
                    if (colWithIndex._2 == point.x)
                      cellType
                    else
                      colWithIndex._1
                )
            else
              rowWithIndex._1
        )
    )

  def toHumanReadableString: String =
    cells
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
}

object Maze {
  def filledWithWalls(width: Int, height: Int): Maze =
    Maze(Vector.fill(width, height)(CellType.Wall))
}
