
```scala
package ohnosequences.reads

import ohnosequences.fastarious.Quality

case object qualityStats {

  case class QualityStats(
    val minExpectedErrors   : Num,
    val meanExpectedErrors  : Num,
    val maxExpectedErrors   : Num
  )

  val from: Iterator[Quality] => QualityStats =
    quals => {

      val (minErr, errSum, no, maxErr) =
        quals.foldLeft( (Double.MaxValue: Double, 0: Double, 0: BigInt, 0: Double) ) {
          case ((_minErr, _errSum, _no, _maxErr), qual) => {

            val ee =
              qual.expectedErrors

            (
              if(ee < _minErr) ee else _minErr  ,
              ee + _errSum                      ,
              1 + _no                           ,
              if(ee > _maxErr) ee else _maxErr
            )
          }
        }

      QualityStats(
        minExpectedErrors   = minErr,
        meanExpectedErrors  = if(errSum == 0) 0: Double else errSum.toDouble / no.toDouble, maxExpectedErrors   = maxErr
      )
    }
}

```




[test/scala/QualityStats.scala]: ../../test/scala/QualityStats.scala.md
[test/scala/testData.scala]: ../../test/scala/testData.scala.md
[test/scala/PositionStats.scala]: ../../test/scala/PositionStats.scala.md
[test/scala/BasicPreprocessing.scala]: ../../test/scala/BasicPreprocessing.scala.md
[test/scala/SizeStats.scala]: ../../test/scala/SizeStats.scala.md
[main/scala/positionStats.scala]: positionStats.scala.md
[main/scala/paired.scala]: paired.scala.md
[main/scala/preprocessing.scala]: preprocessing.scala.md
[main/scala/package.scala]: package.scala.md
[main/scala/qualityStats.scala]: qualityStats.scala.md
[main/scala/sizeStats.scala]: sizeStats.scala.md