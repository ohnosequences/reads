
```scala
package ohnosequences.reads

import ohnosequences.fastarious._
import spire.implicits._

case object preprocessing {
```

This class groups several functions on sequences with quality

```scala
  implicit class PreprocessingOps(val sequence: SequenceQuality) extends AnyVal {

    def dropTrailingUnder(quality: Int): SequenceQuality =
      sequence dropWhileQuality { _ <= quality }

    def longestPrefixWithExpectedErrorsBelow(threshold: Num): SequenceQuality = {

      @annotation.tailrec
      def rec(acc: SequenceQuality): SequenceQuality =
        if(acc.isEmpty)
          acc
        else
          if(acc.quality.expectedErrors <= threshold)
            acc
          else
            rec(acc dropRight 1)

      rec(sequence)
    }

    def longestSuffixWithExpectedErrorsBelow(threshold: Num): SequenceQuality = {

      @annotation.tailrec
      def rec(acc: SequenceQuality): SequenceQuality =
        if(acc.isEmpty)
          acc
        else
          if(acc.quality.expectedErrors <= threshold)
            acc
          else
            rec(acc drop 1)

      rec(sequence)
    }
  }
}

```




[test/scala/QualityStats.scala]: ../../test/scala/QualityStats.scala.md
[test/scala/testData.scala]: ../../test/scala/testData.scala.md
[test/scala/PositionStats.scala]: ../../test/scala/PositionStats.scala.md
[test/scala/BasicPreprocessing.scala]: ../../test/scala/BasicPreprocessing.scala.md
[test/scala/SizeStats.scala]: ../../test/scala/SizeStats.scala.md
[main/scala/positionStats.scala]: positionStats.scala.md
[main/scala/paired.scala]: paired.scala.md
[main/scala/preprocessing.scala]: preprocessing.scala.md
[main/scala/package.scala]: package.scala.md
[main/scala/qualityStats.scala]: qualityStats.scala.md
[main/scala/sizeStats.scala]: sizeStats.scala.md