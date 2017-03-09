// package ohnosequences.reads.test
//
// import org.scalatest.FunSuite
//
// import ohnosequences.reads._
// import example._
// import scala.collection.JavaConverters._
// import java.nio.file._
// import java.io._
// import ohnosequences.fastarious._, fastq._, Quality._
//
// case object tcr {
//
//   def lines(jFile: File): Iterator[String] =
//     Files.lines(jFile.toPath).iterator.asScala
//
//   def writeLines[X](file: File)(it: Iterator[X])(op: X => String): Unit = {
//
//     val wr = new BufferedWriter(new FileWriter(file, true))
//
//     it.foreach { i => { wr.write( op(i) ); wr.newLine } }
//     wr.close
//   }
//
//   val r1File =
//     new File("TCR7_S5_L001_R1_001.fastq")
//
//   val r2File =
//     new File("TCR7_S5_L001_R2_001.fastq")
//
//   val umisFile =
//     new File("UMIs.csv")
//
//   def pairedReads: Iterator[(FASTQ, FASTQ)] =
//     (lines(r1File).parseFastqPhred33 zip lines(r2File).parseFastqPhred33) collect { case (Some(r1), Some(r2)) => (r1,r2) }
//
//   val isUMI: Sequence => Boolean =
//     seq =>
//       (for {
//         a <- seq.at(0).map{ _._1 };
//         b <- seq.at(5).map{ _._1 };
//         c <- seq.at(10).map{ _._1 };
//         d <- seq.at(15).map{ _._1 }
//       } yield { a == 'T' && b == 'T' && c == 'T' && d =='T' }) getOrElse false
//
//   val getUMIFragment: FASTQ => Sequence =
//     _.value.drop(29).take(16)
//
//   val hasUMI: FASTQ => Boolean =
//     getUMIFragment andThen isUMI
//
//   def umis: Iterator[UMIData] =
//     pairedReads collect {
//
//       case (r1,r2) if(hasUMI(r1)) => UMIData(getUMIFragment(r1), umiRead = r1, other = r2)
//       case (r1,r2) if(hasUMI(r2)) => UMIData(getUMIFragment(r2), umiRead = r2, other = r1)
//     }
//
//   case class UMIData(umi: Sequence, val umiRead: FASTQ, val other: FASTQ)
//
//   import paired._
//
//   sealed trait UMIPair {
//     def umi: Sequence
//     def reads: ReadPair
//   }
//   case class LeftUMI(val umi: Sequence, val reads: ReadPair)
//   case class RightUMI(val umi: Sequence, val reads: ReadPair)
// }
//
// case object stats {
//
//   val sizeAndSumPerPosition: (Iterator[Seq[BigDecimal]], Int) => Seq[(Int,BigDecimal)] =
//     (values: Iterator[Seq[BigDecimal]], maxLength: Int) =>
//       values.foldLeft((1 to maxLength).map(_ => (0,0:BigDecimal))){
//         (accValues, vs) => {
//           vs.zipWithIndex.foldLeft(accValues) {
//             (acc, vn) => {
//               val (v, n) = vn; val (accNo, accSum) = acc(n)
//               acc.updated( n, (accNo + 1, accSum + v) )
//             }
//           }
//         }
//       }
// }
//
// class UMITest extends FunSuite {
//
//   import tcr._
//
//   test("UMI counts") {
//
//     val checkOutput =
//       Files.deleteIfExists(new File("umis.csv").toPath)
//
//     writeLines(umisFile)(umis){ x => s"${x.umi.sequence},${x.umi.quality.expectedErrors}" }
//
//     // much memory, so big
//     // val umisMap =
//     //   umis.foldLeft[Map[String, List[UMIData]]](Map()) {
//     //
//     //     (acc,umiData) => {
//     //       acc get umiData.umi.sequence match {
//     //
//     //         case Some(other)  =>
//     //           println{s"adding read for UMI ${umiData.umi.sequence}"}; acc.updated(umiData.umi.sequence, umiData :: other)
//     //         case None         =>
//     //           println{s"new UMI: ${umiData.umi.sequence}"}; acc + (umiData.umi.sequence -> List(umiData))
//     //       }
//     //     }
//     //   }
//
//     val umiCounts =
//       umis.foldLeft[collection.mutable.Map[String,Int]](new collection.mutable.HashMap[String,Int]){ (map, x) =>
//         (map get x.umi.sequence) match {
//           case Some(count)  => map.update(x.umi.sequence, count + 1); map
//           case None         => map + (x.umi.sequence -> 1)
//         }
//       }
//
//     lazy val noUMIs =
//       umiCounts.size
//
//     lazy val avgSeqsPerUMI =
//       umiCounts.valuesIterator.sum / noUMIs
//
//     lazy val maxSeqsPerUMI =
//       umiCounts.valuesIterator.max
//
//     println { s"Number of different UMIs: ${noUMIs}" }
//     println { s"Maximum number of seqs per UMI: ${maxSeqsPerUMI}" }
//     println { s"Average seqs per UMI: ${avgSeqsPerUMI}" }
//   }
// }
