package tbank.academy.scala.labyrinths.generators

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class GeneratorTest extends AnyFlatSpec with Matchers {
  it should "Проверка генерации с алгоритмом DFS" in {
    val generator = new DfsGenerator(1234)
    val result    = generator.generate(20, 20)
    result.map(m => m.toHumanReadableString) shouldBe Right(
      """####################
        |#  #  #  ##   #    #
        |##  #   #   #   ## #
        |# #  ##   # ## #   #
        |# ## # ###   # # ###
        |#    #    # ## # # #
        |# ## # ##  #   #   #
        |# #  # #  #  ## ## #
        |##  #  # #  # #    #
        |#  #  ## # #    ####
        |# ### #  # # ###   #
        |# ###  # # #     # #
        |#  #  #  # ######  #
        |# #  #  #       # ##
        |# # ###  # # ## #  #
        |# #  # # # ##   ## #
        |# ## #    #   ##   #
        |#  #  # ##  ##  ## #
        |##   #     ####    #
        |####################""".stripMargin
    )
  }

  it should "Проверка генерации с алгоритмом Прима" in {
    val generator = new PrimGenerator(1234)
    val result    = generator.generate(20, 20)
    result.map(m => m.toHumanReadableString) shouldBe Right(
      """####################
        |#   # ### # #   #  #
        |# #    #      ##  ##
        |# ## ## # # #    ###
        |#   ###    # # #  ##
        |## # # # ##   # #  #
        |##          ##  ## #
        |#  ## ## ##   #   ##
        |##  #  # #  #  # # #
        |# # # ##  # ##     #
        |#    #  ##   # # ###
        |## #   # # # # # # #
        |#   # #  ## ##  #  #
        |# # # ##      #   ##
        |#  #     ## # # #  #
        |# # ### ####  ## ###
        |#      # ## #      #
        |# # ##       ## # ##
        |# # #  # # #   #   #
        |####################""".stripMargin
    )
  }
}
