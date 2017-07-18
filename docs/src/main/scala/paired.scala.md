
```scala
package ohnosequences.reads

import ohnosequences.fastarious._, fastq._

import java.io.File
import java.nio.file._
import scala.collection.JavaConverters._

case object paired {

  case class ReadPair(val left: Read, val right: Read) {

    def pair: (Read, Read) =
      (left, right)
  }

  val parseFromFastqPhred33Lines: (Iterator[String], Iterator[String]) => Iterator[Option[ReadPair]] =
    (leftLines, rightLines) =>
      (leftLines.parseFastqPhred33 zip rightLines.parseFastqPhred33)
        .map {
          _ match {
            case (Some(r1), Some(r2)) => Some( ReadPair(r1, r2) )
            case _                    => None
          }
        }

  val parseFromFastqPhred33Files: (File, File) => Iterator[Option[ReadPair]] =
    (leftFile, rightFile) => parseFromFastqPhred33Lines( lines(leftFile), lines(rightFile) )

  val parseFromFastqPhred33LinesDropErrors: (Iterator[String], Iterator[String]) => Iterator[ReadPair] =
    (l,r) => parseFromFastqPhred33Lines(l,r) collect { case Some(rp) => rp }

  val parseFromFastqPhred33FilesDropErrors: (File, File) => Iterator[ReadPair] =
    (lf, rf) => parseFromFastqPhred33Files(lf,rf) collect { case Some(rp) => rp }

  private def lines(jFile: File): Iterator[String] =
    Files.lines(jFile.toPath).iterator.asScala
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