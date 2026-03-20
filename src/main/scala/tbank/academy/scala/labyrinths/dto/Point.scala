package tbank.academy.scala.labyrinths.dto

case class Point(x: Integer, y: Integer) {
  def nearby(): List[Point] = List(
    Point(x + 1, y),
    Point(x - 1, y),
    Point(x, y + 1),
    Point(x, y - 1)
  )
}
