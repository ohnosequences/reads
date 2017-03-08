package ohnosequences.reads

import ohnosequences.fastarious.fastq._
import spire.implicits._

case object sequences {

  /*
    This class groups several functions on FASTQ sequences
  */
  implicit class SequenceOps(val sequence: Sequence) extends AnyVal {

    def dropTrailingUnder(quality: Int): Sequence =
      sequence dropWhileQuality { _ <= quality }

    def longestPrefixWithExpectedErrorsBelow(threshold: BigDecimal): Sequence = {

      @annotation.tailrec
      def rec(acc: Sequence): Sequence =
        if(acc.isEmpty)
          acc
        else
          if(acc.quality.expectedErrors <= threshold)
            acc
          else
            rec(acc dropRight 1)

      rec(sequence)
    }

    def longestSuffixWithExpectedErrorsBelow(threshold: BigDecimal): Sequence = {

      @annotation.tailrec
      def rec(acc: Sequence): Sequence =
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
