package tbank.academy.scala.labyrinths.parsers

import tbank.academy.scala.labyrinths.dto.Point
import tbank.academy.scala.labyrinths.error.{ArgumentNotFoundError, DomainError, InvalidArgumentError}
import tbank.academy.scala.labyrinths.solvers.{AstarSolver, DijkstraSolver, Solver}

import java.io.{File, FileOutputStream, OutputStream}

object SolverParser {
  def parseAlgorithm(args: List[String]): Either[DomainError, Solver] = {
    val prefix = "--algorithm="
    val value  = args
      .drop(1)
      .findLast(arg => arg.startsWith(prefix))
      .map(arg => arg.drop(prefix.length))

    value match {
      case Some("astar")    => Right(new AstarSolver())
      case Some("dijkstra") => Right(new DijkstraSolver())
      case Some(_)          => Left(InvalidArgumentError("algorithm"))
      case None             => Left(ArgumentNotFoundError("algorithm"))
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
    val prefix     = "--start="
    val pointRegex = "^(\\d+),(\\d+)$".r
    val parsed     = args
      .drop(1)
      .findLast(arg => arg.startsWith(prefix))
      .map(arg => arg.drop(prefix.length))
      .flatMap(arg => pointRegex.findFirstMatchIn(arg))
      .map(regexMatch => Point(regexMatch.group(1).toInt, regexMatch.group(2).toInt))

    parsed match {
      case Some(value) => Right(value)
      case None        => Left(ArgumentNotFoundError("start"))
    }
  }

  def parseEnd(args: List[String]): Either[DomainError, Point] = {
    val prefix     = "--end="
    val pointRegex = "^(\\d+),(\\d+)$".r
    val parsed     = args
      .drop(1)
      .findLast(arg => arg.startsWith(prefix))
      .map(arg => arg.drop(prefix.length))
      .flatMap(arg => pointRegex.findFirstMatchIn(arg))
      .map(regexMatch => Point(regexMatch.group(1).toInt, regexMatch.group(2).toInt))

    parsed match {
      case Some(value) => Right(value)
      case None        => Left(ArgumentNotFoundError("end"))
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
      case None        => Right(System.out)
    }
  }
}
