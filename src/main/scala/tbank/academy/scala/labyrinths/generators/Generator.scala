package tbank.academy.scala.labyrinths.generators

import tbank.academy.scala.labyrinths.dto.Maze
import tbank.academy.scala.labyrinths.error.DomainError

trait Generator {
  def generate(width: Integer, height: Integer): Either[DomainError, Maze]
}
