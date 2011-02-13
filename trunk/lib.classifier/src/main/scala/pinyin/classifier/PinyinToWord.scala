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

class PinyinToWord extends HMMClassifier[Character, String] {
  var pii = Map[Int, Int]()
  var emisMatrix = Map[Int, Map[Int, Int]]()
  var stateBank = new NodeBank[Character]
  var observeBank = new NodeBank[String]();
  var ngram: TreeNode[Character] = new TreeNodeImpl[Character]('r');

  override def init() {
    ngram.buildIndex(ngram.getCount)

    viterbi.stateBank = stateBank
    viterbi.observeBank = observeBank

    viterbi.tran = Transition[Character](ngram, stateBank)

    viterbi.e = Emission(emisMatrix)
    viterbi.e.observeBank = observeBank

    viterbi.pi = Pi(pii)

    pii.clear
    emisMatrix.clear
    pii = null
    emisMatrix = null
  }

  override def getStateBy(id: Int): Character = stateBank.get(id).name

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
      stateBank = ois.readObject().asInstanceOf[NodeBank[Character]];
      observeBank = ois.readObject().asInstanceOf[NodeBank[String]];
      pii = ois.readObject().asInstanceOf[Map[Int,Int]];
      emisMatrix = ois.readObject().asInstanceOf[Map[Int,Map[Int,Int]]];
      ngram = ois.readObject().asInstanceOf[TreeNode[Character]];
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
    } else if(filename.endsWith(".idx")) {
      trainNgram(filename)
    }
  }

  private def statisticNGram(list: List[(String,String)]): Unit = {
    val chs = new Array[Character](list.size())
    for {
      i <- 0 until list.size()
      val t = list.get(i)
      val c = Character.valueOf(if(t._1.startsWith("Head")) t._1.charAt(4) else t._1.charAt(0))
    } {
      chs(i) = c
    }
    ngram.insert(chs);
  }

  private def statisticNGram(list: scala.List[Node[Character]]): Unit = {
    val chs = new Array[Character](list.size())
    for{
      i <- 0 until list.size()
      val node = list(i)
      val c = node.name
    } {
      chs(i) = c
    }
    ngram.insert(chs);
  }

  def trainHMM(filename: String): Unit = {
    val file = new File(filename)
    if (file.exists()) {
      println("[PinyinToWord] train file " + file.getAbsolutePath())
      try {
        val br = new BufferedReader(
          new InputStreamReader(
            new FileInputStream(file), "UTF-8"))
        var line = br.readLine
        val window = new Window[(String,String)](Flag.n)
        while (null != line) {
          val cur = parseLine(line.trim, false)

          if (null == cur) {
            window.clear()
          } else {
            window.add(cur)
            val isHeadOfWord = cur._1.startsWith("Head")
            val s = if(isHeadOfWord) cur._1.substring(4) else cur._1
            val state = stateBank.add(Character.valueOf(s.charAt(0)))
            val o = cur._2
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

  def trainNgram(filename: String): Unit = {
    val file = new File(filename)
    if (file.exists()) {
      println("[PinyinToWord] train file " + file.getAbsolutePath())
      try {
        val br = new BufferedReader(
          new InputStreamReader(
            new FileInputStream(file), "UTF-8"))

        var doc = Idx.parsePlainText(br);
        while (doc != null) {
          val idx = new Idx(doc);

          val content = idx.getTagValue("DRECONTENT");
          val window = new Window[Node[Character]](Flag.n);
          for (i <- 0 until content.length) {
            val s = Character.valueOf(content.charAt(i))
            val state = stateBank.get(s)
            if (state == null) {
              window.clear();
            } else {
              window.add(state);
              {
                val index = state.index
                val c = pii.getOrElse(index,0) + 1
                pii.put(index, c);
              }

              val array = window.toList
//              for (int j = 0; j < array.length; j++) {
              statisticNGram(array);
//              }
            }
          }

          doc = Idx.parsePlainText(br);
        }
        br.close();
      } catch {
        case ex =>
          ex.printStackTrace();
      }
    }
  }
}
