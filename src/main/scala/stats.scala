package ohnosequences.reads

import ohnosequences.fastarious._, fastq._, Quality._

case object stats {

  type Position         = Int
  type ErrorProbability = BigDecimal

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
