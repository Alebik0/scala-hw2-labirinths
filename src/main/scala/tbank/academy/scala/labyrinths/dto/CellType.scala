package tbank.academy.scala.labyrinths.dto

trait CellType

object CellType {
  case object Wall extends CellType
  case object Path extends CellType
  case object Empty extends CellType
  case object Start extends CellType
  case object End extends CellType
}
