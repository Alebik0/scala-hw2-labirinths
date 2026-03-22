package tbank.academy.scala.labyrinths

import tbank.academy.scala.labyrinths.dto.Point
import tbank.academy.scala.labyrinths.error.{ArgumentNotFoundError, DomainError, InvalidArgumentError}
import tbank.academy.scala.labyrinths.generators.{DfsGenerator, Generator, PrimGenerator}
import tbank.academy.scala.labyrinths.solvers.{AstarSolver, Solver}

import java.io.{File, FileOutputStream, OutputStream}

object Parser {
  private val DEFAULT_SEED = 123

  private def DEFAULT_GENERATOR(seed: Int) = new DfsGenerator(seed)

  def parseHelp(args: List[String]): Boolean =
    args == List("--help") || args == List("-h")

  def parseWidth(args: List[String]): Either[DomainError, Int] = {
    val maybeWidth = args
      .drop(1)
      .findLast(arg => arg.startsWith("--width="))
      .flatMap(arg => arg.drop(8).toIntOption)

    maybeWidth match {
      case Some(width) => Right(width)
      case None => Left(ArgumentNotFoundError("width"))
    }
  }

  def parseHeight(args: List[String]): Either[DomainError, Int] = {
    val maybeHeight = args
      .drop(1)
      .findLast(arg => arg.startsWith("--height="))
      .flatMap(arg => arg.drop(9).toIntOption)

    maybeHeight match {
      case Some(height) => Right(height)
      case None => Left(ArgumentNotFoundError("height"))
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
          case "dfs" => Some(new DfsGenerator(seed))
          case "prim" => Some(new PrimGenerator(seed))
          case _ => None
        }
      )
      .getOrElse(DEFAULT_GENERATOR(seed))

  def parseAlgorithm(args: List[String]): Either[DomainError, Solver] = {
    val prefix = "--algorithm="
    val value = args
      .drop(1)
      .findLast(arg => arg.startsWith(prefix))
      .map(arg => arg.drop(prefix.length))

    value match {
      case Some("astar") => Right(new AstarSolver())
      case Some(_) => Left(InvalidArgumentError("algorithm"))
      case None => Left(ArgumentNotFoundError("algorithm"))
    }
  }

  def parseMazeFile(args: List[String]): Either[DomainError, File] = {
    val prefix = "--file="
    val parsed = args
      .drop(1)
      .findLast(arg => arg.startsWith(prefix))
      .map(arg => arg.drop(prefix.length))
      .map(arg => new File(arg))

    parsed match {
      case Some(value) =>
        Right(value)
      case None => Left(ArgumentNotFoundError("file"))
    }
  }

  def parseStart(args: List[String]): Either[DomainError, Point] = {
    val prefix = "--start="
    val pointRegex = "^(\\d+),(\\d+)$".r
    val parsed = args
      .drop(1)
      .findLast(arg => arg.startsWith(prefix))
      .map(arg => arg.drop(prefix.length))
      .flatMap(arg => pointRegex.findFirstMatchIn(arg))
      .map(regexMatch => Point(regexMatch.group(1).toInt, regexMatch.group(2).toInt))

    parsed match {
      case Some(value) => Right(value)
      case None => Left(ArgumentNotFoundError("start"))
    }
  }

  def parseEnd(args: List[String]): Either[DomainError, Point] = {
    val prefix = "--end="
    val pointRegex = "^(\\d+),(\\d+)$".r
    val parsed = args
      .drop(1)
      .findLast(arg => arg.startsWith(prefix))
      .map(arg => arg.drop(prefix.length))
      .flatMap(arg => pointRegex.findFirstMatchIn(arg))
      .map(regexMatch => Point(regexMatch.group(1).toInt, regexMatch.group(2).toInt))

    parsed match {
      case Some(value) => Right(value)
      case None => Left(ArgumentNotFoundError("end"))
    }
  }

  def parseOutput(args: List[String]): Either[DomainError, OutputStream] = {
    val prefix = "--output="
    val parsed = args
      .drop(1)
      .findLast(arg => arg.startsWith(prefix))
      .map(arg => arg.drop(prefix.length))
      .map(arg => new FileOutputStream(arg))

    parsed match {
      case Some(value) => Right(value)
      case None => Right(System.out)
    }
  }
}
