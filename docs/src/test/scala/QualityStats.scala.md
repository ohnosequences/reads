
```scala
package ohnosequences.reads.test

import ohnosequences.reads._
import testData._

class QualityStats extends org.scalatest.FunSuite {

  val show: qualityStats.QualityStats => Unit =
    stats => {
      println(s" min expected errors: ${stats.minExpectedErrors}")
      println(s"mean expected errors: ${stats.meanExpectedErrors}")
      println(s" max expected errors: ${stats.maxExpectedErrors}")
      println("\n")
    }

  test("calculate quality stats") {

    println(s"in quality stats")
    println("\n")

    val in_stats =
      qualityStats.from(inReads.map(_.sequence.quality))

    show(in_stats)

    println(s"out quality stats")
    println("\n")

    val out_stats =
      qualityStats.from(outReads.map(_.sequence.quality))

    show(out_stats)
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