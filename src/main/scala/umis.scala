package ohnosequences.reads

import ohnosequences.fastarious._, fastq._
import paired._

case object umis {

  // magic numbers for now
  val isUMI: Sequence => Boolean =
    seq =>
      {
        for(
          a <- seq.at(0).map{ _._1 };
          b <- seq.at(5).map{ _._1 };
          c <- seq.at(10).map{ _._1 };
          d <- seq.at(15).map{ _._1 }
        ) yield { a == 'T' && b == 'T' && c == 'T' && d =='T' }
      } getOrElse false

  val getUMIFragment: FASTQ => Sequence =
    _.value.drop(29).take(16)

  val hasUMI: FASTQ => Boolean =
    getUMIFragment andThen isUMI

  val someHasUMI: ReadPair => Boolean =
    pair => hasUMI(pair.left) || hasUMI(pair.right)

  val umisAndOthers: Iterator[ReadPair] => Iterator[(UMIPair, ReadPair)] =
    readPairs => {

      val (withUMIs, others) =
        readPairs partition someHasUMI

      val umiPairs: Iterator[UMIPair] =
        withUMIs collect {
          case p @ ReadPair((l,r)) if( hasUMI(l) ) => LeftUMI(getUMIFragment(l), p)
          case p @ ReadPair((l,r)) if( hasUMI(r) ) => RightUMI(getUMIFragment(r), p)
        }

      umiPairs zip others
    }

  val umisOnly: Iterator[ReadPair] => Iterator[UMIPair] =
    pairs => umisAndOthers(pairs) map { _._1 }

  sealed trait UMIPair {
    def umi: Sequence
    def reads: ReadPair
  }
  case class LeftUMI(val umi: Sequence, val reads: ReadPair)  extends UMIPair
  case class RightUMI(val umi: Sequence, val reads: ReadPair) extends UMIPair
}
