package tbank.academy.scala.labyrinths.parsers

object Parser {
  def parseHelp(args: List[String]): Boolean =
    args.contains("--help") || args.contains("-h")
}
