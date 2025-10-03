package tbank.academy.scala.labyrinths

import tbank.academy.scala.labyrinths.dto.Maze

trait Generator {
  def generate(width: Integer, height: Integer): Maze
}
