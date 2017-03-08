package ohnosequences.reads

import ohnosequences.fastarious._, fastq._
import java.io.File
import java.nio.file._
import scala.collection.JavaConverters._


case object paired {

  type Read = FASTQ

  case class ReadPair(val pair: (Read, Read)) extends AnyVal {

    def left  : Read =
      pair._1

    def right : Read =
      pair._2
  }

  val parseFromFastqPhred33Lines: (Iterator[String], Iterator[String]) => Iterator[Option[ReadPair]] =
    (leftLines, rightLines) =>
      (leftLines.parseFastqPhred33 zip rightLines.parseFastqPhred33)
        .map {
          _ match {
            case (Some(r1), Some(r2)) => Some( ReadPair(r1 -> r2) )
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
