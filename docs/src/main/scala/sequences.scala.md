
```scala
package ohnosequences.reads

import ohnosequences.fastarious._
import spire.implicits._

case object sequences {
```

This class groups several functions on sequences with quality

```scala
  implicit class SequenceOps(val sequence: SequenceQuality) extends AnyVal {

    def dropTrailingUnder(quality: Int): SequenceQuality =
      sequence dropWhileQuality { _ <= quality }

    def longestPrefixWithExpectedErrorsBelow(threshold: BigDecimal): SequenceQuality = {

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

    def longestSuffixWithExpectedErrorsBelow(threshold: BigDecimal): SequenceQuality = {

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




[test/scala/Reads.scala]: ../../test/scala/Reads.scala.md
[main/scala/sequences.scala]: sequences.scala.md
[main/scala/stats.scala]: stats.scala.md
[main/scala/pairedEnd.scala]: pairedEnd.scala.md