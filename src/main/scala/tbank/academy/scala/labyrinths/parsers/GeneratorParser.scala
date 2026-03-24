package tbank.academy.scala.labyrinths.parsers

import tbank.academy.scala.labyrinths.error.{ArgumentNotFoundError, DomainError}
import tbank.academy.scala.labyrinths.generators.{DfsGenerator, Generator, PrimGenerator}

object GeneratorParser {
  private val DEFAULT_SEED = 123

  private def DEFAULT_GENERATOR(seed: Int) = new DfsGenerator(seed)

  def parseWidth(args: List[String]): Either[DomainError, Int] = {
    val maybeWidth = args
      .drop(1)
      .findLast(arg => arg.startsWith("--width="))
      .flatMap(arg => arg.drop(8).toIntOption)

    maybeWidth match {
      case Some(width) => Right(width)
      case None        => Left(ArgumentNotFoundError("width"))
    }
  }

  def parseHeight(args: List[String]): Either[DomainError, Int] = {
    val maybeHeight = args
      .drop(1)
      .findLast(arg => arg.startsWith("--height="))
      .flatMap(arg => arg.drop(9).toIntOption)

    maybeHeight match {
      case Some(height) => Right(height)
      case None         => Left(ArgumentNotFoundError("height"))
    }
  }

  def parseSeed(args: List[String]): Int =
    args
      .drop(1)
      .findLast(arg => arg.startsWith("--seed="))
      .flatMap(arg => arg.drop(7).toIntOption)
      .getOrElse(DEFAULT_SEED)

  def parseGenerator(seed: Int, args: List[String]): Generator =
    args
      .drop(1)
      .findLast(arg => arg.startsWith("--algorithm="))
      .flatMap(arg =>
        arg.drop(12) match {
          case "dfs"  => Some(new DfsGenerator(seed))
          case "prim" => Some(new PrimGenerator(seed))
          case _      => None
        }
      )
      .getOrElse(DEFAULT_GENERATOR(seed))
}
