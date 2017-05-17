package ohnosequences.reads.test

import org.scalatest.FunSuite

import ohnosequences.reads._, stats._, sequences._
import java.nio.file._
import scala.collection.JavaConverters._
import java.io._
import ohnosequences.fastarious._, fastq._, Quality._

class ReadsTest extends FunSuite {

  def lines(jFile: File): Iterator[String] =
    Files.lines(jFile.toPath).iterator.asScala

  def writeLines[X](file: File)(it: Iterator[X])(op: X => String): Unit = {

    val wr = new BufferedWriter(new FileWriter(file, true))

    it.foreach { i => { wr.write( op(i) ); wr.newLine } }
    wr.close
  }

  def basicStats(reads: => Iterator[SequenceQuality]): Unit = {

    println { s"Average length: ${reads.averageLength}"                   }

    val maxLength =
      reads.maxLength

    println { s"Maximum length: ${maxLength}" }

    println { s"Average expected errors: ${reads.averageExpectedErrors}"  }
    println { s"Maximum expected error: ${reads.maximumExpectedErrors}"   }
    println { s"Minimum expected errors: ${reads.minimumExpectedErrors}"  }

    println { "Average expected error per position: \"position: error\"" }

    reads.averageExpectedErrorPerPosition(maxPos = maxLength - 1).zipWithIndex foreach { case (err,pos) =>
      println { s"${pos}: ${err}" }
    }
  }

  lazy val in: File = new File("in.fastq")
  lazy val out: File = new File("out.fastq")

  def inReads: Iterator[FASTQ] =
    lines(in) parseFastqPhred33DropErrors

  def outReads: Iterator[FASTQ] =
    lines(out) parseFastqPhred33DropErrors

  def tcrReads: Iterator[FASTQ] =
    lines(new File("data/in/TCR7_S5_L001_R1_001.fastq")) parseFastqPhred33DropErrors

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  test("basic stats") { basicStats(inReads.map(_.sequence)) }

  test("basic preprocessing") {

    def preprocessAndKeepIDs(reads: Iterator[FASTQ]): Iterator[FASTQ] =
      reads
        .map( _ updateSequence { _.longestPrefixWithExpectedErrorsBelow(1) } )
        .filter( _.sequence.length >= 100 )
        .filter( _.sequence.quality.expectedErrors <= 0.6 )

    val zz = Files.deleteIfExists(out.toPath)
    val oh = preprocessAndKeepIDs(inReads) appendAsPhred33To out
  }

  ignore("TCR reads position based stats") {

    val posStats = toCharError(tcrReads.map(_.sequence)).positionStats(449)

    println { "Ts per position" }
    posStats.meanTs.zipWithIndex.foreach { case (ratio, pos) =>
      println { s"${pos}: ${ratio}" }
    }
  }

  test("processed reads stats") { basicStats(outReads.map(_.sequence)) }
  ignore("TCR reads size stats") { println { tcrReads.map(_.sequence).sizeStats } }
  ignore("TCR reads quality stats") { println { tcrReads.map(_.sequence).qualityStats } }
}
