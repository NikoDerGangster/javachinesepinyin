/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pinyin.classifier

import hmm._
import ngram._
import pinyin.utils._

import java.io._
import java.util._
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

import scala.collection.JavaConversions._
import scala.collection.mutable.Map

class WordToPinyin extends HMMClassifier[String, Character] {
  var pii = Map[Int, Int]()
  var emisMatrix = Map[Int, Map[Int, Int]]()
  var stateBank = new NodeBank[String]
  var observeBank = new NodeBank[Character]();
  var ngram: TreeNode[String] = new TreeNodeImpl[String]("r");

  override def init() {
    ngram.buildIndex(ngram.getCount)

    viterbi.stateBank = stateBank
    viterbi.observeBank = observeBank

    viterbi.tran = Transition[String](ngram, stateBank)

    viterbi.e = Emission(emisMatrix)
    viterbi.e.observeBank = observeBank

    viterbi.pi = Pi(pii)

    pii.clear
    emisMatrix.clear
    pii = null
    emisMatrix = null
  }

  override def getStateBy(id: Int): String = stateBank.get(id).name

  override def save(filename: String): Unit = {
    try {
      ngram.buildIndex(ngram.getCount);
//            ngram.cutCountLowerThan(Flag.getInstance().getNgramMinimumShowTimes());
      val oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filename)));
      oos.writeObject(stateBank);
      oos.writeObject(observeBank);
      oos.writeObject(pii);
      oos.writeObject(emisMatrix);
      oos.writeObject(ngram);
      oos.close();
    } catch {
      case ex =>
        ex.printStackTrace();
    }
  }

  override def load(is: InputStream): Unit = {
    try {
      val ois = new ObjectInputStream(new GZIPInputStream(is));
      stateBank = ois.readObject().asInstanceOf[NodeBank[String]];
      observeBank = ois.readObject().asInstanceOf[NodeBank[Character]];
      pii = ois.readObject().asInstanceOf[Map[Int,Int]];
      emisMatrix = ois.readObject().asInstanceOf[Map[Int,Map[Int,Int]]];
      ngram = ois.readObject().asInstanceOf[TreeNode[String]];
      is.close();
    } catch {
      case ex =>
        ex.printStackTrace();
    }
  }

  def parseLine(line: String, reverse: Boolean):(String,String) = {
    if ("".equals(line) || line.startsWith("c")) {
      null
    } else {
      val pair = line.split("\\s");
      if (null != pair && pair.length == 2) {
        if(!reverse){
          (pair(0), pair(1))
        }else{
          (pair(1),pair(0))
        }
      } else {
        null
      }
    }
  }

  override def train(filename: String): Unit = {
    if(filename.endsWith(".txt")){
      trainHMM(filename)
    }
  }

  private def statisticNGram(list: List[(String,String)]): Unit = {
    val chs = new Array[String](list.size())
    for {
      i <- 0 until list.size()
      val t = list.get(i)
      val c = t._1
    } {
      chs(i) = c
    }
    ngram.insert(chs);
  }

  def trainHMM(filename: String): Unit = {
    val file = new File(filename)
    if (file.exists()) {
      println("[WordToPinyin] train file " + file.getAbsolutePath())
      try {
        val br = new BufferedReader(
          new InputStreamReader(
            new FileInputStream(file), "UTF-8"))
        var line = br.readLine
        val window = new Window[(String,String)](Flag.n)
        while (null != line) {
          val cur = parseLine(line.trim, true)

          if (null == cur) {
            window.clear()
          } else {
            window.add(cur)
            val s = cur._1//if(isHeadOfWord) cur._1.substring(4) else cur._1
            val state = stateBank.add(s)
            val isHeadOfWord = cur._2.startsWith("Head")
            val o = Character.valueOf(if(isHeadOfWord) cur._2.substring(4).charAt(0) else cur._2.charAt(0))
            val observe = observeBank.add(o)

            //Pii
//            if(isHeadOfWord)
            {
              val index = state.index
              val c = pii.getOrElse(index,0) + 1
              pii.put(index, c)
            }

            //Transition
            val array = window.toList
            statisticNGram(array)

            //Emission
            val si = state.index
            val oi = observe.index
            val row = emisMatrix.getOrElseUpdate(si, Map[Int,Int]())
            val count = row.getOrElse(oi, 0) + 1
            row.put(oi, count)
          }
          line = br.readLine;
        }
        br.close();
      } catch {
        case ex: IOException =>
          ex.printStackTrace();
      }
    }
  }

}
