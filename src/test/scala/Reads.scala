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

  lazy val in: File = new File("in.fastq")
  lazy val out: File = new File("out.fastq")

  def inReads: Iterator[FASTQ] =
    lines(in) parseFastqPhred33DropErrors

  def outReads: Iterator[FASTQ] =
    lines(out) parseFastqPhred33DropErrors

  test("basic stats") {

    println { s"Average length: ${inReads.averageLength}" }
    println { s"Average expected errors: ${inReads.averageExpectedErrors}"  }
    println { s"Maximum expected error: ${inReads.maximumExpectedErrors}"   }
    println { s"Minimum expected errors: ${inReads.minimumExpectedErrors}"  }

    println { "Average expected error per position: \"position: error\"" }

    inReads.averageExpectedErrorPerPosition(maxPos = 249).zipWithIndex foreach { case (err,pos) =>
      println { s"${pos}: ${err}" }
    }
  }

  test("basic preprocessing") {

    def preprocessAndKeepIDs(reads: Iterator[FASTQ]): Iterator[FASTQ] =
      reads
        .map( _ updateSequence { _.longestPrefixWithExpectedErrorsBelow(1) } )
        .filter( _.value.length >= 100 )
        .filter( _.value.quality.expectedErrors <= 0.6 )

    val zz = Files.deleteIfExists(out.toPath)
    val oh = preprocessAndKeepIDs(inReads) appendAsPhred33To out
  }

  test("processed reads stats") {

    println { s"Average length: ${outReads.averageLength}" }
    println { s"Minimum length: ${outReads.minLength}" }
    println { s"Maximum length: ${outReads.maxLength}" }
    println { s"Average expected errors: ${outReads.averageExpectedErrors}"  }
    println { s"Maximum expected error: ${outReads.maximumExpectedErrors}"   }
    println { s"Minimum expected errors: ${outReads.minimumExpectedErrors}"  }

    println { "Average expected error per position: \"position: error\"" }

    outReads.averageExpectedErrorPerPosition(maxPos = 249).zipWithIndex foreach { case (err,pos) =>
      println { s"${pos}: ${err}" }
    }
  }
}
