package tbank.academy.scala.labyrinths.error

sealed trait DomainError

case class InvalidMazeSizeError()              extends DomainError
case class IndexOutOfBoundsError()             extends DomainError
case class ArgumentNotFoundError(name: String) extends DomainError
case class InvalidArgumentError(name: String)  extends DomainError
case class UnexpectedError()                   extends DomainError
case class InputFileReadError(reason: String)  extends DomainError
