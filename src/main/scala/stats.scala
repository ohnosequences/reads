package ohnosequences.reads

import ohnosequences.fastarious._, fastq._, Quality._
import spire.implicits._

case object stats {

  type Position         = Int
  type ErrorProbability = BigDecimal

  case class SizeStats(
    val minSize : Int,
    val meanSize: BigDecimal,
    val maxSize : Int
  )

  implicit class sizeOps(val reads: Iterator[FASTQ]) extends AnyVal {

    def sizeStats: SizeStats = {

      val (minSize, sum, no, maxSize) =
        reads.foldLeft( (Int.MaxValue: Int, 0:BigInt, 0:BigInt, 0) ) {

          case ((min, s, n, max), read) => {

            val l = read.value.length

            val _min = if(l <= min) l else min
            val _s  = s + l
            val _n  = n + 1
            val _max = if(l >= max) l else max

            (_min, _s, _n, _max)
          }
        }

      SizeStats(minSize, if(sum == 0) 0: BigDecimal else BigDecimal(sum) / BigDecimal(no), maxSize )
    }
  }

  case class QualityStats(
    val minExpectedErrors   : BigDecimal,
    val meanExpectedErrors  : BigDecimal,
    val maxExpectedErrors   : BigDecimal
  )

  implicit class qualityOps(val reads: Iterator[FASTQ]) extends AnyVal {

    def qualityStats: QualityStats = {

      val (minErr, sum, no, maxErr) =
        reads.foldLeft( (Float.MaxValue: BigDecimal, 0:BigDecimal, 0:BigInt, 0: BigDecimal) ) {

          case ((min, s, n, max), read) => {

            val l = read.value.quality.expectedErrors

            val _min = if(l <= min) l else min
            val _s  = s + l
            val _n  = n + 1
            val _max = if(l >= max) l else max

            (_min, _s, _n, _max)
          }
        }

      QualityStats(minErr, if(sum == 0) 0: BigDecimal else sum / BigDecimal(no), maxErr )
    }
  }

  case class PositionStats(
    val maxPosition: Position,
    val meanExpectedErrorsPerPosition: Seq[ErrorProbability],
    val meanNs: Seq[BigDecimal],
    val meanAs: Seq[BigDecimal],
    val meanTs: Seq[BigDecimal],
    val meanCs: Seq[BigDecimal],
    val meanGs: Seq[BigDecimal]
  )

  val toCharError: Iterator[FASTQ] => Iterator[Seq[(Char,BigDecimal)]] =
    _.map {
      r =>
        (r.value.sequence zip r.value.quality.scores.map(_.asPhredScore.errorProbability))
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
        meanExpectedErrorsPerPosition = posDatas map { pd => if(pd.number == 0) 0: BigDecimal else pd.errorProbabilitySum / BigDecimal(pd.number) },
        meanAs = posDatas map { pd => if(pd.number == 0) 0:BigDecimal else BigDecimal(pd.As) / BigDecimal(pd.number) },
        meanTs = posDatas map { pd => if(pd.number == 0) 0:BigDecimal else BigDecimal(pd.Ts) / BigDecimal(pd.number) },
        meanCs = posDatas map { pd => if(pd.number == 0) 0:BigDecimal else BigDecimal(pd.Cs) / BigDecimal(pd.number) },
        meanGs = posDatas map { pd => if(pd.number == 0) 0:BigDecimal else BigDecimal(pd.Gs) / BigDecimal(pd.number) },
        meanNs = posDatas map { pd => if(pd.number == 0) 0:BigDecimal else BigDecimal(pd.Ns) / BigDecimal(pd.number) }
      )
    }
  }

  case class PositionData(
    val number: BigInt,
    val errorProbabilitySum: BigDecimal,
    val As: BigInt,
    val Ts: BigInt,
    val Cs: BigInt,
    val Gs: BigInt,
    val Ns: BigInt
  )

  implicit class statsOps(val reads: Iterator[FASTQ]) extends AnyVal {

    def averageLength: BigDecimal =
      sizeAndAvgLength._2

    def maxLength: Int =
      reads.map(_.value.length).max

    def minLength: Int =
      reads.map(_.value.length).min

    def averageExpectedErrors: BigDecimal =
      sizeAndAvgEE._2

    def maximumExpectedErrors: BigDecimal =
      reads.map(_.value.quality.expectedErrors).max

    def minimumExpectedErrors: BigDecimal =
      reads.map(_.value.quality.expectedErrors).min

    def expectedErrors: Iterator[Seq[ErrorProbability]] =
      reads.map(_.value.quality.scores.map(_.asPhredScore.errorProbability))

    def averageExpectedErrorPerPosition(maxPos: Position): Seq[ErrorProbability] =
      sizeAndSumPerPosition(expectedErrors, maxPos)
        .map { case (size,sum) => if(size == 0) 0: BigDecimal else sum / size }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // utilities
    private def sizeAndAvgEE: (Int, BigDecimal) = {

      val (sumEE, size) =
        reads.foldLeft( (0: BigDecimal, 0) ){
          (acc, r) => {
            (acc._1 + r.value.quality.expectedErrors, acc._2 + 1)
          }
        }

      if(size == 0) (0,0) else (size, sumEE / size)
    }

    private def sizeAndAvgLength: (Int, BigDecimal) = {

      val (sum, size) =
        reads.foldLeft( (0: BigInt, 0) ){
          (acc, r) => {
            (acc._1 + r.value.length, acc._2 + 1)
          }
        }

      if(size == 0) (0,0) else (size, BigDecimal(sum) / size)
    }


    private def sizeAndSumPerPosition(values: Iterator[Seq[BigDecimal]], maxPos: Int): Seq[(Int,ErrorProbability)] =
      values.foldLeft( (0 to maxPos).map { i => (0,0:BigDecimal) } ){
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
