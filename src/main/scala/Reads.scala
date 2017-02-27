package ohnosequences.reads

import ohnosequences.fastarious.fastq._
import spire.math.Real
import spire.implicits._
import sequences._

case object example {

  type Read = FASTQ

  def preprocessAndKeepIDs(reads: Iterator[Read]): Iterator[Read] =
    reads
      .map( _ updateSequence { _.longestPrefixWithExpectedErrorsBelow(2) } )
      .filter( _.value.length >= 100 )
}
