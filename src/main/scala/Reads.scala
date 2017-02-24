package ohnosequences.reads

import ohnosequences.fastarious.fastq._
import spire.math.Real
import spire.implicits._

case object preprocessing {

  type Read = FASTQ

  implicit class ReadPreprocessing(val read: Read) extends AnyVal {

    def dropTrailingUnder(quality: Int): Read =
      read updateSequence { _ dropWhileQuality { _ <= quality } }

    def dropWhileAverage(windowSize: Int, averageQuality: Real): Read = {

      @annotation.tailrec
      def rec(acc: Sequence): Sequence =
        if(acc.isEmpty)
          acc
        else
          if( (acc takeRight windowSize).quality.average <= averageQuality )
            rec(acc dropRight windowSize)
          else
            acc

      read updateSequence rec
    }

    def longestSuffixOver(quality: Int): Read =
      read updateSequence { _ takeWhileQuality { _ >= quality } }

    // TODO move to stats or something
    // def countQualityOver(quality: Int): Int =
    //   s countQuality { _ >= quality }
    //
    // private def numberOfNs: Int =
    //   read.sequence countSequence { _.toUpper == 'N' }
  }
}

case object Example {

  import preprocessing._

  def preprocess(reads: Iterator[Read]): Iterator[Read] =
    reads
      .filter(_.value.quality.average >= 30)
      .map { read =>
        read
          .dropTrailingUnder(quality = 20)
          .dropWhileAverage(windowSize = 10, averageQuality = 35)
          .longestSuffixOver(quality = 28)
      }
      .filter(_.value.length >= 120)
}
