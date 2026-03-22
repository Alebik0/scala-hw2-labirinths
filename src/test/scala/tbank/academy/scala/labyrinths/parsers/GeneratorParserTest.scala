package tbank.academy.scala.labyrinths.parsers

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import tbank.academy.scala.labyrinths.generators.DfsGenerator

class GeneratorParserTest extends AnyFlatSpec with Matchers {
  private val BASE_ARGS = "generate --width=20 --height=20 --seed=1234 --algorithm=dfs"
    .split(" ")
    .toList

  it should "Проверка валидного parseWidth" in {
    GeneratorParser.parseWidth(BASE_ARGS) shouldBe Right(20)
  }

  it should "Проверка валидного parseHeight" in {
    GeneratorParser.parseHeight(BASE_ARGS) shouldBe Right(20)
  }

  it should "Проверка валидного parseSeed" in {
    GeneratorParser.parseSeed(BASE_ARGS) shouldBe 1234
  }

  it should "Проверка валидного parseEnd" in {
    GeneratorParser.parseGenerator(1234, BASE_ARGS) shouldBe a[DfsGenerator]
  }
}
