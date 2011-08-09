/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pinyin.classifier

import hmm._
import ngram._
import java.io._
import java.util._

import scala.collection.JavaConversions._

trait SeqLabelClassifier[S <: Comparable[S], O <: Comparable[O]] {

  def init():Unit
  def setProperty(key: String, value: String): Unit
  def load(filename: String): Unit
  def load(is: InputStream): Unit
  def save(filename: String): Unit
  def train(filename: String): Unit
  def labelStateOfNodes(o: java.util.List[O]): Result
  def classify(o: java.util.List[O]): java.util.List[S]
  def getStateBy(id: Int): S
}

trait AbstractClassifier[S <: Comparable[S], O <: Comparable[O]] extends SeqLabelClassifier[S,O] {
  implicit def toScalaListFromJavaList[T](jList: java.util.List[T]) = jList.toArray.asInstanceOf[Array[T]].toList
  
  val p = new Properties()
  def setProperty(key: String, value: String): Unit = p.setProperty(key, value)
  def load(filename: String): Unit = 
    if(new File(filename).exists) {
      load(new FileInputStream(filename))
    } else {
      load(this.getClass.getClassLoader.getResourceAsStream(filename))
    }
}

class HMMClassifier[S <: Comparable[S], O <: Comparable[O]] extends AbstractClassifier[S, O] {
  def init(): Unit = throw new RuntimeException
  def load(is: InputStream): Unit = throw new RuntimeException
  def save(filename: String): Unit = throw new RuntimeException
  def train(filename: String): Unit = throw new RuntimeException
  def getStateBy(id: Int): S = throw new RuntimeException

  val viterbi = new Viterbi[S,O]();
  def labelStateOfNodes(o: java.util.List[O]): Result = viterbi.caculateResult(o)
  def classify(o: java.util.List[O]): java.util.List[S] = viterbi.caculateWithLog(o)
}


