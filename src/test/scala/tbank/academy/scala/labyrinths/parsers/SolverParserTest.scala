package tbank.academy.scala.labyrinths.parsers

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import tbank.academy.scala.labyrinths.dto.Point
import tbank.academy.scala.labyrinths.solvers.DijkstraSolver

import java.io.File

class SolverParserTest extends AnyFlatSpec with Matchers {
  private val BASE_ARGS = "solve --algorithm=astar --file=/tests/cases/4_check_maze_solving/maze.txt --start=2,2 --end=10,10"
    .split(" ")
    .toList

  it should "Проверка валидного parseAlgorithm" in {
    SolverParser.parseAlgorithm(BASE_ARGS) match {
      case Left(_) =>
        fail()
      case Right(value) =>
        value shouldBe a [DijkstraSolver]
    }
  }

  it should "Проверка валидного parseMazeFile" in {
    SolverParser.parseMazeFile(BASE_ARGS) shouldBe Right(new File("/tests/cases/4_check_maze_solving/maze.txt"))
  }

  it should "Проверка валидного parseStart" in {
    SolverParser.parseStart(BASE_ARGS) shouldBe Right(Point(2, 2))
  }

  it should "Проверка валидного parseEnd" in {
    SolverParser.parseEnd(BASE_ARGS) shouldBe Right(Point(10, 10))
  }

  it should "Проверка parseOutput = stdout" in {
    SolverParser.parseOutput(BASE_ARGS) shouldBe Right(System.out)
  }
}
