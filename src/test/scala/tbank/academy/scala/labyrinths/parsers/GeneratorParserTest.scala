package tbank.academy.scala.labyrinths.parsers

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import tbank.academy.scala.labyrinths.error.ArgumentNotFoundError
import tbank.academy.scala.labyrinths.generators.{DfsGenerator, PrimGenerator}

class GeneratorParserTest extends AnyFlatSpec with Matchers {
  private val BASE_ARGS: List[String] = "generate --width=20 --height=20 --seed=1234 --algorithm=dfs"
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

  it should "Проверка валидного parseGenerator #1" in {
    GeneratorParser.parseGenerator(1234, BASE_ARGS) shouldBe a[DfsGenerator]
  }

  it should "Проверка валидного parseGenerator #2" in {
    GeneratorParser.parseGenerator(1234, List("generate", "--algorithm=prim")) shouldBe a[PrimGenerator]
  }

  it should "Проверка невалидного parseWidth #1" in {
    GeneratorParser.parseWidth(List("generate", "--width=-1")) shouldBe Left(ArgumentNotFoundError("width"))
  }

  it should "Проверка невалидного parseWidth #2" in {
    GeneratorParser.parseWidth(List("generate", "--width=1")) shouldBe Left(ArgumentNotFoundError("width"))
  }

  it should "Проверка невалидного parseWidth #3" in {
    GeneratorParser.parseWidth(List("generate", "--width=2")) shouldBe Left(ArgumentNotFoundError("width"))
  }

  it should "Проверка невалидного parseHeight #1" in {
    GeneratorParser.parseHeight(List("generate", "--height=-1")) shouldBe Left(ArgumentNotFoundError("height"))
  }

  it should "Проверка невалидного parseHeight #2" in {
    GeneratorParser.parseHeight(List("generate", "--height=1")) shouldBe Left(ArgumentNotFoundError("height"))
  }

  it should "Проверка невалидного parseHeight #3" in {
    GeneratorParser.parseHeight(List("generate", "--height=2")) shouldBe Left(ArgumentNotFoundError("height"))
  }

  it should "Проверка невалидного parseSeed" in {
    GeneratorParser.parseSeed(List("generate", "--seed=qwe")) shouldBe 123
  }

  it should "Проверка невалидного parseGenerator" in {
    GeneratorParser.parseGenerator(1234, List("generate", "--algorithm=qwe")) shouldBe a[DfsGenerator]
  }
}
