
```scala
package ohnosequences.reads.test

import java.nio.file._
import scala.collection.JavaConverters._
import java.io._
import ohnosequences.fastarious._, fastq._, Quality._

case object testData {

  lazy val in: File = new File("in.fastq")
  lazy val out: File = new File("out.fastq")

  def inReads: Iterator[FASTQ] =
    lines(in) parseFastqPhred33DropErrors

  def outReads: Iterator[FASTQ] =
    lines(out) parseFastqPhred33DropErrors


  def lines(jFile: File): Iterator[String] =
    Files.lines(jFile.toPath).iterator.asScala

  def writeLines[X](file: File)(it: Iterator[X])(op: X => String): Unit = {

    val wr = new BufferedWriter(new FileWriter(file, true))

    it.foreach { i => { wr.write( op(i) ); wr.newLine } }
    wr.close
  }
}

```




[test/scala/QualityStats.scala]: QualityStats.scala.md
[test/scala/testData.scala]: testData.scala.md
[test/scala/PositionStats.scala]: PositionStats.scala.md
[test/scala/BasicPreprocessing.scala]: BasicPreprocessing.scala.md
[test/scala/SizeStats.scala]: SizeStats.scala.md
[main/scala/positionStats.scala]: ../../main/scala/positionStats.scala.md
[main/scala/paired.scala]: ../../main/scala/paired.scala.md
[main/scala/preprocessing.scala]: ../../main/scala/preprocessing.scala.md
[main/scala/package.scala]: ../../main/scala/package.scala.md
[main/scala/qualityStats.scala]: ../../main/scala/qualityStats.scala.md
[main/scala/sizeStats.scala]: ../../main/scala/sizeStats.scala.md