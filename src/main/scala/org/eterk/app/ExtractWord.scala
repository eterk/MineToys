package org.eterk.app

import edu.stanford.nlp.ling.CoreAnnotations
import org.eterk.util.Util

import java.io.File
import scala.collection.mutable
import scala.language.implicitConversions
import scala.util.Try


object ExtractWord extends App {

  import edu.stanford.nlp.pipeline._

  import java.util.Properties


  // 定义一个函数，用于创建一个中文的 stanfordnlp 对象
  def createChinesePipeline(): StanfordCoreNLP = {
    // 创建一个属性对象，用于设置语言和模型
    val props = new Properties()

    val stream = this.getClass.getResourceAsStream("/StanfordCoreNLP-chinese.properties")

    props.load(stream)

    props.setProperty("annotators", "tokenize, ssplit, pos,lemma, ner")


    // 创建一个中文的 stanfordnlp 对象
    val pipeline = new StanfordCoreNLP(props)
    // 返回 stanfordnlp 对象
    pipeline
  }

  // 定义一个函数，用于从一段中文字符串中导出一些名词，人名地名这种
  def extract(text: String, pipeline: StanfordCoreNLP, map: collection.mutable.Map[String, collection.mutable.Map[(String, String), Int]]) = {
    // 创建一个注释对象，用于存储分析结果
    val document = new Annotation(text)

    pipeline.annotate(document)
    // 创建一个空的列表，用于存储名词

    import scala.jdk.CollectionConverters._

    document.get(classOf[CoreAnnotations.SentencesAnnotation])
      .asScala
      .flatMap(_.get(classOf[CoreAnnotations.TokensAnnotation]).asScala)
      .foreach(token => {
        // 获取词的文本
        val word: String = token.get(classOf[CoreAnnotations.TextAnnotation])
        if (word.length >= 2) {
          // 获取词的词性
          val pos: String = token.get(classOf[CoreAnnotations.PartOfSpeechAnnotation])
          // 获取词的实体类型
          val ner: String = token.get(classOf[CoreAnnotations.NamedEntityTagAnnotation])


          val key = pos -> ner
          val count = map.getOrElseUpdate(word, create.tupled(key))
          val num = count.getOrElseUpdate(pos -> ner, 0)
          count.update(key, num + 1)

        }

      })

  }

  def wikiFreWord(num: Int): Seq[String] = {
    //    https://en.wiktionary.org/wiki/Appendix:Mandarin_Frequency_lists

    val source = scala.io.Source.fromFile(getClass.getResource("/chinese_fre_word.txt").getFile)
    val seq =
      source
        .getLines()
        .map(l => l.split("\\|")(2))
        .filter(_.length > 1)
        .toSeq


    source.close()
    seq

  }

  val create: (String, String) => mutable.Map[(String, String), Int] = (pos: String, ner: String) => collection.mutable.Map((pos -> ner, 0))


  private def extractHotWord(text: String,
                             exceptWord: Seq[String],
                             outputPath: String,
                             fileName: String,
                             limit: Int): String = {
    val map: mutable.Map[String, mutable.Map[(String, String), Int]] = collection.mutable.Map[String, collection.mutable.Map[(String, String), Int]]()

    // 调用函数，创建一个中文的 stanfordnlp 对象
    val pipeline = createChinesePipeline()
    //
    //    // 调用函数，从测试字符串中导出一些名词，人名地名这种
    extract(text, pipeline, map)

    import org.eterk.util.SeqExtension._

    val res =
      map.flatMap {
        case (word, v) =>
          v.map {
            case ((pos, ner), fre) =>
              s"${word},${pos},${ner},${fre}"
          }
      }.mkString("\n")


    import org.eterk.script.Scrapy.saveAsTxt

    val tempFile = saveAsTxt(res, "mid_" + fileName, outputPath)


    val seq =
      redCsv(tempFile)
        .filter(wordFilter)
        .groupMap(_.word)(_.info)
        .toSeq
        .sortBy(x => x._2.map(_._3).sum)(Ordering[Int].reverse)

    val word = seq
      .showChange {
        _.map(x => modify(x._1))
          .distinct
      }
      .showChange(_.diff(exceptWord))

    val str = appendWeight(word)
      .map(x => x._1 + " " + x._2.toInt)
      .take(limit)
      .mkString("\n")


    saveAsTxt(str, fileName, outputPath)
  }

  def appendWeight(words: Seq[String]): Seq[(String, Double)] = {
    val func = Util.normalize(100, 1, words.size)
    words
      .zip((1 to words.size).reverse)
      .map {
        case (word, index) => (word, func(index))
      }
  }


  def wordFreAgg(seq: Seq[WordInfo]): Seq[(String, Int)] = {
    seq
      .groupMapReduce(_.word)(_.frequency)((a, b) => a + b)
      .toSeq
      .sortBy(_._2)(Ordering[Int].reverse)
  }


  val modify: PartialFunction[String, String] = {
    case some if Seq("率", "为", "王", "派", "死").exists(some.endsWith) && some.length >= 3 => some.dropRight(1)
    case some1 if some1.startsWith("王司马") => some1.drop(1)
    case other => other
  }


  private def wordFilter(x: WordInfo): Boolean = {
    import x._

    val notIllegalWord = word.length <= 4

    val nerN = Seq("PERSON").contains(ner)
    val nerR = Seq("NUMBER", "DATE", "TIME").contains(ner)
    val posR = Seq("PN", "P", "AD", "VE", "VV", "DT", "VA").contains(pos)
    val freR = frequency > 2
    val uselessWord = posR || nerR
    ((freR && !uselessWord) || nerN) && notIllegalWord
  }

  private case class WordInfo(word: String,
                              pos: String,
                              ner: String,
                              frequency: Int) {
    def info: (String, String, Int) = (pos, ner, frequency)
  }

  private def redCsv(path: String): Seq[WordInfo] = {
    val source = scala.io.Source.fromFile(path)
    val seq =
      source
        .getLines()
        .map {
          case a@s"${word},${pos},${ner},${fre}" =>
            Try(
              WordInfo(word, pos, ner, fre.toInt)
            ).recover {
              case e =>
                msg(a)
                WordInfo("", "", "", 1)
            }.get

          case other => msg(other)
            WordInfo("", "", "", 1)
        }
        .toSeq


    source.close()
    seq
  }


  /**
   * a unique key to start up App,as simple,short as possible;
   */
  override def appKey: String = "ew"

  override def paramTypeSeq: Seq[String] = Seq("FILE_DIR:txt", "RADIO:1000,2000,3000", "DIR", "TEXT", "INT")

  override def execute(params: String*): Unit = {

    val Seq(txtFileDir, wordFre, outputDir, outputName, limit) = params

    val files: Seq[String] = Util.filterFiles(txtFileDir, _.endsWith("txt"), recursive = false)


    val text: String = Util.concatFiles(files, "utf-8")

    val exceptWord = wikiFreWord(wordFre.toInt)


    extractHotWord(text, exceptWord, outputDir, outputName + ".txt", limit.toInt)


  }

}

