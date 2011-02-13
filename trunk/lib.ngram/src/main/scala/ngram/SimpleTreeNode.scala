/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngram

import java.io.File
import java.util.Comparator

class SimpleNgramModel[T <: Comparable[T]](val r: T) extends NgramModel[T] {
  var root = new TreeNodeImpl[T](r)
  var cmp: Comparator[T] = null
  def getRootNode: TreeNode[T] = root
  def load(file: File): Unit = throw new RuntimeException("unimplements load method.")
  def save(file: File): Unit = throw new RuntimeException("unimplements save method.")
  def compact(file: File): Unit = throw new RuntimeException("unimplements compact method.")
  def getConditionProb(ngram: Array[T]): Double = getRootNode.search(ngram).getProb
}

class TreeNodeImpl[T <: Comparable[T]](val key: T) extends TreeNode[T] with java.io.Serializable {
  type N = TreeNodeImpl[T]

  var count = 0
  var prob: Double = 0.0
  var descendant = new Array[N](0)

  def getKey: T = key
  def getCount = count
  def getProb = prob
  def setProb(prob: Double): Unit = this.prob = prob

  def compare(A: T, B: T) = A.compareTo(B)

  def sortWith(): Array[N] = {
    descendant.sortWith(
      (A: N, B: N )=>{compare(A.key, B.key) < 0}
    )
  }

  def buildIndex(c: Int) {
    prob = count * 1.0 / (c + 1.0)
    if(!descendant.isEmpty) {
      descendant.foreach(_.buildIndex(count))
      descendant = sortWith
    }
  }

  def insert(ngram: Array[T]): TreeNode[T] = {
    count += 1
    if(!ngram.isEmpty){
      var n: N = null
      if(!descendant.isEmpty) {
        n = getNode(ngram.head)
        if(null == n) {
          n = add(ngram.head)
        }
      } else {
        n = add(ngram.head)
      }
      n.insert(ngram.tail)
    } else {
      this
    }
  }

  def add(node: T): N = {
    val ele = new N(node);
    add(ele)
    ele
  }

  def add(e: N) {
//    var i = 0;
    if(descendant.isEmpty) {
      descendant = new Array[N](1)
      descendant(0) = e
    } else {
      val tmp = new Array[N](descendant.length + 1);
      descendant copyToArray tmp
      tmp(descendant.length) = e
      descendant = tmp
      descendant = sortWith
    }
  }

  def search(ngram: Array[T]): TreeNode[T] = {
    val k: T = ngram(0)
    val n: TreeNode[T] = getNode(k)
    if(null != n && ngram.length > 1) {
      val tail = ngram.tail
      n.search(tail)
    } else {
      n
    }
  }

  def getNode(searchItem: T): N = getNode(descendant, descendant.length, searchItem)

  def getNode(list: Array[N], listLength: Int, searchItem: T): N = {
    if(!list.isEmpty) {
      var first = 0
      var last = listLength - 1
      var mid = -1
      var found = false
      while(first <= last && !found){
        mid = (first + last) / 2
        val i = compare(list(mid).key, searchItem)
        if(i == 0) {
          found = true
        } else if (i > 0) {
          last = mid - 1
        } else {
          first = mid + 1;
        }
      }
      if(found) list(mid) else null
    } else {
      null
    }
  }

  def getDescendants(): Array[TreeNode[T]] = descendant.asInstanceOf[Array[TreeNode[T]]]

  override def traversal(op:(TreeNode[T]) => Unit){
    descendant.foreach(op(_))
  }

  def printNode(indent: String) {
    println(indent + key + " - " + count + " - " + prob)
    descendant.foreach(_.printNode(indent+"  "))
  }

  def getNumberOfNodeWhichCountLessThan(lt: Int): Int = {
    val total = (0 /: descendant){(t: Int, n) => t + n.getNumberOfNodeWhichCountLessThan(lt)}
    total + {if(count < lt) 1 else 0}
  }

  def cutNodeWhichCountLessThan(lt: Int) {
    if (lt > 1) {

      if(!descendant.isEmpty){
        var tmp: List[N] = Nil
        descendant.foreach(n => if(n.count >= lt){tmp = n :: tmp})
        descendant = tmp.reverse.toArray
      }
    }
  }
}


