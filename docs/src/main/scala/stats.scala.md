
```scala
package ohnosequences.reads

import ohnosequences.fastarious._, Quality._
import spire.implicits._

case object stats {

  type Position         = Int
  type ErrorProbability = ohnosequences.fastarious.ErrorP

  case class SizeStats(
    val minSize : Int,
    val meanSize: Double,
    val maxSize : Int
  )

  implicit class sizeOps(val reads: Iterator[SequenceQuality]) extends AnyVal {

    def sizeStats: SizeStats = {

      val (minSize, sum, no, maxSize) =
        reads.foldLeft( (Int.MaxValue: Int, 0:BigInt, 0:BigInt, 0) ) {

          case ((min, s, n, max), read) => {

            val l = read.length

            val _min = if(l <= min) l else min
            val _s  = s + l
            val _n  = n + 1
            val _max = if(l >= max) l else max

            (_min, _s, _n, _max)
          }
        }

      SizeStats(minSize, if(sum == 0) 0: Double else sum.toDouble / no.toDouble, maxSize )
    }
  }

  case class QualityStats(
    val minExpectedErrors   : Num,
    val meanExpectedErrors  : Num,
    val maxExpectedErrors   : Num
  )

  implicit class qualityOps(val reads: Iterator[SequenceQuality]) extends AnyVal {

    def qualityStats: QualityStats = {

      val (minErr, sum, no, maxErr) =
        reads.foldLeft( (Float.MaxValue: Double, 0:Double, 0:BigInt, 0: Double) ) {

          case ((min, s, n, max), read) => {

            val l = read.quality.expectedErrors

            val _min = if(l <= min) l else min
            val _s  = s + l
            val _n  = n + 1
            val _max = if(l >= max) l else max

            (_min, _s, _n, _max)
          }
        }

      QualityStats(minErr, if(sum == 0) 0: Double else sum.toDouble / no.toDouble, maxErr )
    }
  }

  case class PositionStats(
    val maxPosition: Position,
    val meanExpectedErrorsPerPosition: Seq[ErrorProbability],
    val meanNs: Seq[Double],
    val meanAs: Seq[Double],
    val meanTs: Seq[Double],
    val meanCs: Seq[Double],
    val meanGs: Seq[Double]
  )

  val toCharError: Iterator[SequenceQuality] => Iterator[Seq[(Char,Double)]] =
    _.map {
      r =>
        (r.sequence.letters zip r.quality.scores.map(_.asPhredScore.errorProbability))
    }

  implicit class positionOps(val seqs: Iterator[Seq[(Char,ErrorProbability)]]) extends AnyVal {

    def positionStats(maxPos: Int): PositionStats = {

      val initialPositionData =
        PositionData(
          number = 0,
          errorProbabilitySum = 0,
          As = 0,
          Ts = 0,
          Cs = 0,
          Gs = 0,
          Ns = 0
        )

      val posDatas: Seq[PositionData] =
        seqs.foldLeft[Seq[PositionData]]( (0 to maxPos).map { _ => initialPositionData } ){
          (acc: Seq[PositionData], seq: Seq[(Char, ErrorProbability)]) =>
            seq.zipWithIndex.foldLeft[Seq[PositionData]](acc) {
              (pdatas: Seq[PositionData], ce: ((Char, ErrorProbability), Int)) => {

                val ((char, errProb), pos) = ce

                // get previous
                val pdata = pdatas(pos)
                // update
                pdatas.updated(
                  pos,
                  PositionData(
                    number    = pdata.number + 1,
                    errorProbabilitySum  = pdata.errorProbabilitySum + errProb,
                    As = if(char.toUpper == 'A') pdata.As + 1 else pdata.As,
                    Ts = if(char.toUpper == 'T') pdata.Ts + 1 else pdata.Ts,
                    Cs = if(char.toUpper == 'C') pdata.Cs + 1 else pdata.Cs,
                    Gs = if(char.toUpper == 'G') pdata.Gs + 1 else pdata.Gs,
                    Ns = if(char.toUpper == 'N') pdata.Ns + 1 else pdata.Ns
                  )
                )
              }
            }
          }

      PositionStats(
        maxPosition = maxPos,
        meanExpectedErrorsPerPosition = posDatas map { pd => if(pd.number == 0) 0: Double else pd.errorProbabilitySum / pd.number.toDouble },
        meanAs = posDatas map { pd => if(pd.number == 0) 0:Double else pd.As.toDouble / pd.number.toDouble },
        meanTs = posDatas map { pd => if(pd.number == 0) 0:Double else pd.Ts.toDouble / pd.number.toDouble },
        meanCs = posDatas map { pd => if(pd.number == 0) 0:Double else pd.Cs.toDouble / pd.number.toDouble },
        meanGs = posDatas map { pd => if(pd.number == 0) 0:Double else pd.Gs.toDouble / pd.number.toDouble },
        meanNs = posDatas map { pd => if(pd.number == 0) 0:Double else pd.Ns.toDouble / pd.number.toDouble }
      )
    }
  }

  case class PositionData(
    val number: BigInt,
    val errorProbabilitySum: Double,
    val As: BigInt,
    val Ts: BigInt,
    val Cs: BigInt,
    val Gs: BigInt,
    val Ns: BigInt
  )

  implicit class statsOps(val reads: Iterator[SequenceQuality]) extends AnyVal {

    def averageLength: Double =
      sizeAndAvgLength._2

    def maxLength: Int =
      reads.map(_.length).max

    def minLength: Int =
      reads.map(_.length).min

    def averageExpectedErrors: Double =
      sizeAndAvgEE._2

    def maximumExpectedErrors: Double =
      reads.map(_.quality.expectedErrors).max

    def minimumExpectedErrors: Double =
      reads.map(_.quality.expectedErrors).min

    def expectedErrors: Iterator[Seq[ErrorProbability]] =
      reads.map(_.quality.scores.map(_.asPhredScore.errorProbability))

    def averageExpectedErrorPerPosition(maxPos: Position): Seq[ErrorProbability] =
      sizeAndSumPerPosition(expectedErrors, maxPos)
        .map { case (size,sum) => if(size == 0) 0: Double else sum / size }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // utilities
    private def sizeAndAvgEE: (Int, Double) = {

      val (sumEE, size) =
        reads.foldLeft( (0: Double, 0) ){
          (acc, r) => {
            (acc._1 + r.quality.expectedErrors, acc._2 + 1)
          }
        }

      if(size == 0) (0,0) else (size, sumEE / size)
    }

    private def sizeAndAvgLength: (Int, Double) = {

      val (sum, size) =
        reads.foldLeft( (0: BigInt, 0) ){
          (acc, r) => {
            (acc._1 + r.length, acc._2 + 1)
          }
        }

      if(size == 0) (0,0) else (size, sum.toDouble / size)
    }


    private def sizeAndSumPerPosition(values: Iterator[Seq[Double]], maxPos: Int): Seq[(Int,ErrorProbability)] =
      values.foldLeft( (0 to maxPos).map { i => (0,0:Double) } ){
        (accValues, vs) => {
          // if vs is empty we return accValues, if not
          vs.zipWithIndex.foldLeft(accValues) {
            (acc, vn) => {
              // value, index
              val (v, n) = vn
              // get previous
              val (accNo, accSum) = acc(n)
              // update
              acc.updated( n, (accNo + 1, accSum + v) )
            }
          }
        }
      }

  }
}

```




[test/scala/Reads.scala]: ../../test/scala/Reads.scala.md
[main/scala/sequences.scala]: sequences.scala.md
[main/scala/stats.scala]: stats.scala.md
[main/scala/pairedEnd.scala]: pairedEnd.scala.md