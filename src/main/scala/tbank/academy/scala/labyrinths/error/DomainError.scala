package tbank.academy.scala.labyrinths.error

sealed trait DomainError

class InvalidMazeSizeError  extends DomainError
class IndexOutOfBoundsError extends DomainError
class UnexpectedError       extends DomainError
class WidthNotFoundError    extends DomainError
class HeightNotFoundError   extends DomainError
