package tbank.academy.scala.labyrinths.parsers

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class ParserTest extends AnyFlatSpec with Matchers {
  it should "Проверка вызова help" in {
    Parser.parseHelp(List("--help")) shouldBe true
  }

  it should "Проверка вызова короткого help" in {
    Parser.parseHelp(List("solve", "-h")) shouldBe true
  }

  it should "Проверка стандартного вызова команд" in {
    Parser.parseHelp(List("solve --algorithm=astar --file=/tests/cases/4_check_maze_solving/maze.txt --start=2,2 --end=10,10")) shouldBe false
  }
}
