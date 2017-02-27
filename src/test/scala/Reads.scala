package ohnosequences.reads.test

import org.scalatest.FunSuite

import ohnosequences.reads._
import example._
import java.nio.file._
import scala.collection.JavaConverters._
import java.io._
import ohnosequences.fastarious._, fastq._

class ReadsTest extends FunSuite {

  def lines(jFile: File): Iterator[String] =
    Files.lines(jFile.toPath).iterator.asScala

  def inReads: Iterator[FASTQ] =
    lines(in) parseFastqPhred33DropErrors

  def outReads: Iterator[FASTQ] =
    lines(out) parseFastqPhred33DropErrors

  def avgEE(reads: Iterator[FASTQ]): BigDecimal = {
    val (sumEE, size) =
      reads.foldLeft( (0: BigDecimal, 0) ){
        (acc, r) => (acc._1 + r.value.quality.expectedNumberOfErrors, acc._2 + 1) }

    if(size == 0) 0 else sumEE / size
  }

  val in  = new File("in.fastq")
  val out = new File("out.fastq")

  def runPreprocessing: Unit =
    { val uh = example.preprocessAndKeepIDs( inReads ) appendAsPhred33To out; () }

  test("example Illumina read preprocessing") {

    val zz = Files.deleteIfExists(out.toPath)

    runPreprocessing
  }

  test("expected number of errors stats") {

    println { s"Average expected error (raw): ${avgEE(inReads)}" }
    println { s"Average expected error (filtered): ${avgEE(outReads)}" }
  }
}
