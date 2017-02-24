package ohnosequences.reads

import ohnosequences.fastarious.fastq._
import spire.math.Real
import spire.implicits._

case object sequences {

  /*
    This class groups several functions on FASTQ sequences
  */
  implicit class SequenceOps(val sequence: Sequence) extends AnyVal {

    def dropTrailingUnder(quality: Int): Sequence =
      sequence dropWhileQuality { _ <= quality }

    def dropWhileAverage(windowSize: Int, averageQuality: Real): Sequence = {

      @annotation.tailrec
      def rec(acc: Sequence): Sequence =
        if(acc.isEmpty)
          acc
        else
          if( (acc takeRight windowSize).quality.average <= averageQuality )
            rec(acc dropRight windowSize)
          else
            acc

      rec(sequence)
    }

    def longestSuffixOver(quality: Int): Sequence =
      sequence takeWhileQuality { _ >= quality } }

}
