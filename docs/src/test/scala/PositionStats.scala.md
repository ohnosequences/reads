
```scala
package ohnosequences.reads.test

import ohnosequences.reads._
import testData._

class PositionStats extends org.scalatest.FunSuite {

  val show: ((positionStats.PositionData, Position)) => Unit =
    {
      case (posData, pos) => {

        val stats = positionStats.positionDataToStats(posData)

        println(s"Position ${pos}")
        println(s"total sequences: ${posData.number}")
        println(s"As (mean): ${stats.meanAs}")
        println(s"Ts (mean): ${stats.meanTs}")
        println(s"Cs (mean): ${stats.meanCs}")
        println(s"Gs (mean): ${stats.meanGs}")
        println(s"Ns (mean): ${stats.meanNs}")
        println(s"expected errors (mean): ${stats.meanExpectedErrors}")
        println(s"expected errors (total): ${posData.expectedErrorsSum}")
        println(s"expected sequences with an error here: ${posData.expectedErrorsSum / posData.number.toDouble}")
        println("\n")
      }
    }

  // approx 4min on a std laptop
  test("calculate position stats") {

    println(s"in position stats")

    val in_stats =
      positionStats.positionDataWithMax(250)( inReads.map(_.sequence.pSymbols) )

    in_stats.zipWithIndex foreach show

    println(s"out position stats")

    val out_stats =
      positionStats.positionDataWithMax(250)( outReads.map(_.sequence.pSymbols) )

    out_stats.zipWithIndex foreach show
  }
}

```




[test/scala/QualityStats.scala]: QualityStats.scala.md
[test/scala/testData.scala]: testData.scala.md
[test/scala/PositionStats.scala]: PositionStats.scala.md
[test/scala/BasicPreprocessing.scala]: BasicPreprocessing.scala.md
[test/scala/SizeStats.scala]: SizeStats.scala.md
[main/scala/positionStats.scala]: ../../main/scala/positionStats.scala.md
[main/scala/paired.scala]: ../../main/scala/paired.scala.md
[main/scala/preprocessing.scala]: ../../main/scala/preprocessing.scala.md
[main/scala/package.scala]: ../../main/scala/package.scala.md
[main/scala/qualityStats.scala]: ../../main/scala/qualityStats.scala.md
[main/scala/sizeStats.scala]: ../../main/scala/sizeStats.scala.md