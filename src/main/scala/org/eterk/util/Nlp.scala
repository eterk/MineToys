package org.eterk.util

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import org.eterk.app.ExtractWord.create

import java.io.{File, FileInputStream}
import java.net.URLClassLoader
import java.util.Properties
import scala.collection.mutable


trait Nlp {
  def createChinesePipeline(): StanfordCoreNLP


  def extract(text: String,
              pipeline: StanfordCoreNLP,
              map: collection.mutable.Map[String, collection.mutable.Map[(String, String), Int]]): mutable.Map[String, mutable.Map[(String, String), Int]]
}

object Nlp {

  def apply(): Nlp = NlpInner


  private object NlpOuter extends Nlp {

    // 定义一个函数，用于创建一个自定义的类加载器，用于加载 lib 文件夹中的 jar 包
    private def createCustomClassLoader(): URLClassLoader = {
      // 创建一个文件对象，指定 lib 文件夹的路径
      val libDir = new File("lib")
      println(libDir.getAbsolutePath)
      // 获取 lib 文件夹下的所有 jar 文件
      val jars = libDir.listFiles((dir, name) => name.endsWith(".jar"))
      // 将 jar 文件转换成 URL 数组
      val urls = jars.map(_.toURI.toURL)
      // 创建一个自定义的类加载器，指定 URL 数组和父类加载器
      val classLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader)
      // 返回自定义的类加载器
      classLoader
    }

    // 定义一个函数，用于创建一个中文的 stanford-corenlp 对象
    def createChinesePipeline(): StanfordCoreNLP = {
      // 创建一个自定义的类加载器，用于加载 lib 文件夹中的 jar 包
      val classLoader = createCustomClassLoader()
      // 创建一个属性对象，用于设置语言和模型
      val props = new Properties()
      // 创建一个文件对象，指定 lib 文件夹中的属性文件的路径
      val file = new File("lib/StanfordCoreNLP-chinese.properties")
      // 创建一个输入流对象，用于读取文件内容
      val stream = new FileInputStream(file)
      // 加载属性文件到属性对象中
      props.load(stream)
      // 设置分析器的组件，例如 tokenize, ssplit, pos, lemma, ner
      props.setProperty("annotators", "tokenize, ssplit, pos,lemma, ner")
      // 使用反射来获取 stanford-corenlp 的构造器，使用自定义的类加载器
      val constructor = classLoader.loadClass("edu.stanford.nlp.pipeline.StanfordCoreNLP").getConstructor(classOf[Properties])
      // 使用反射来创建一个中文的 stanford-corenlp 对象，使用属性对象作为参数
      val pipeline = constructor.newInstance(props).asInstanceOf[StanfordCoreNLP]
      // 返回 stanford-corenlp 对象
      pipeline
    }

    // 定义一个函数，用于从一段中文字符串中导出一些名词，人名地名这种
    override def extract(text: String,
                         pipeline: StanfordCoreNLP,
                         map: collection.mutable.Map[String, collection.mutable.Map[(String, String), Int]]): mutable.Map[String, mutable.Map[(String, String), Int]] = {
      // 创建一个注释对象，用于存储分析结果
      val document = new Annotation(text)
      // 使用 stanford-corenlp 对象对文本进行分析
      pipeline.annotate(document)
      // 创建一个空的列表，用于存储名词
      import scala.jdk.CollectionConverters._
      // 遍历每个句子
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
            // 将词的文本，词性，实体类型作为一个键，存储到 map 中，并记录出现的次数
            val key = pos -> ner
            val count = map.getOrElseUpdate(word, collection.mutable.Map(key -> 0))
            val num = count.getOrElseUpdate(key, 0)
            count.update(key, num + 1)
          }
        })
      // 返回 map
      map
    }


  }

  private object NlpInner extends Nlp {


    // 定义一个函数，用于创建一个中文的 stanfordnlp 对象
    override def createChinesePipeline(): StanfordCoreNLP = {
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
    override def extract(text: String,
                         pipeline: StanfordCoreNLP,
                         map: collection.mutable.Map[String, collection.mutable.Map[(String, String), Int]]): mutable.Map[String, mutable.Map[(String, String), Int]] = {
      // 创建一个注释对象，用于存储分析结果
      val document = new Annotation(text)

      pipeline.annotate(document)
      // 创建一个空的列表，用于存储名词

      import scala.jdk.CollectionConverters._

      document
        .get(classOf[CoreAnnotations.SentencesAnnotation])
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
      map
    }


  }

}