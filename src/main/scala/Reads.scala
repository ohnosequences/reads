package ohnosequences.reads

import ohnosequences.fastarious.fastq._
import spire.math.Real
import spire.implicits._
import sequences._

case object example {

  type Read = FASTQ

  def preprocessAndKeepIDs(reads: Iterator[Read]): Iterator[Read] =
    reads
      .filter(_.value.quality.average >= 30)
      .map { read =>
        read updateSequence { seq =>
          seq
            .dropTrailingUnder(quality = 20)
            .dropWhileAverage(windowSize = 10, averageQuality = 35)
            .longestSuffixOver(quality = 28)
        }
      }
      .filter(_.value.length >= 120)
}
