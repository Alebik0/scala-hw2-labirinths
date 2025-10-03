package tbank.academy.scala.labyrinths.dto

trait CellType

object CellType {
  case object Wall extends CellType
  case object Path extends CellType
}
