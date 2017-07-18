
```scala
package ohnosequences.reads.test

import ohnosequences.reads._
import testData._

class SizeStats extends org.scalatest.FunSuite {

  val show: sizeStats.SizeStats => Unit =
    stats => {

      println(s"Number of sequences: ${stats.total}")
      println(s"           Min size: ${stats.minSize}")
      println(s"          Mean size: ${stats.meanSize}")
      println(s"           Max size: ${stats.maxSize}")
      println("\n")

    }

  test("calculate size stats") {

    println(s"in size stats")
    println("\n")

    val in_stats =
      sizeStats.from(inReads.map(_.sequence.sequence))

    show(in_stats)

    println(s"out size stats")
    println("\n")

    val out_stats =
      sizeStats.from(outReads.map(_.sequence.sequence))

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