
```scala
package ohnosequences.reads

import ohnosequences.fastarious.Sequence

case object sizeStats {

  case class SizeStats(
    val total     : BigInt,
    val minSize   : Int,
    val meanSize  : Num,
    val maxSize   : Int
  )

  val from: Iterator[Sequence] => SizeStats =
    seqs => {

      val (min, len_sum, no, max) =
        seqs.foldLeft( (Int.MaxValue: Int, 0: BigInt, 0: BigInt, 0) ) {
          case ((_min, _len_sum, _no, _max), seq) => {

            (
              if(seq.length < _min) seq.length else _min  ,
              seq.length + _len_sum                       ,
              1 + _no                                     ,
              if(seq.length > _max) seq.length else _max
            )
          }
        }

        SizeStats(
          total     = no,
          minSize   = if(no == 0) 0 else min,
          maxSize   = if(no == 0) 0 else max,
          meanSize  = if(no == 0) 0: Double else len_sum.toDouble / no.toDouble
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