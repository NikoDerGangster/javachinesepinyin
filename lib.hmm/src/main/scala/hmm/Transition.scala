/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hmm

import ngram.TreeNode
import ngram.TreeNodeImpl

class Transition[T <: Comparable[T]] {

  var root: TreeNode[T] = null;
  var stateBank = new NodeBank[T]()

  def setStateBank(bank: NodeBank[T]) {
    stateBank = bank
  }

  def setProb(s1: Int, s2: Int, prob: Double) {
    val ngram = new Array[Comparable[T]](2).asInstanceOf[Array[T]]
    ngram(0) = stateBank.get(s1).name
    ngram(1) = stateBank.get(s2).name
    val node = root.insert(ngram)
    node.setProb(prob)
  }

  def getPorb(s: Array[Int]): Double = {
    val ngram = new Array[Comparable[T]](s.length).asInstanceOf[Array[T]]
    for(i <- 0 until s.length) {
      ngram(i) = stateBank.get(i).name
    }
    getProb(ngram, ngram.length)
  }

  def getProb(c: Array[Int], s: Int): Double = {
    val ngram = new Array[Comparable[T]](c.length + 1).asInstanceOf[Array[T]]
    for(i <- 0 until c.length) {
      ngram(i) = stateBank.get(c(i)).name
    }
    ngram(c.length) = stateBank.get(s).name
    getProb(ngram, ngram.length)
  }

  def getProb(s1: Int, s2: Int): Double = {
    val ngram = new Array[Comparable[T]](2).asInstanceOf[Array[T]]
    ngram(0) = stateBank.get(s1).name
    ngram(1) = stateBank.get(s2).name
    getProb(ngram)
  }

  def getProb(ngram: Array[T], n: Int): Double = {
    if(n == 2){
      getProb(ngram)
    } else {
      var ret = 0.00000000001D
      for (i <- n until 0) {
        val igram = new Array[Comparable[T]](i).asInstanceOf[Array[T]]
        for(j <- 1 to i) {
          igram(i-j) = ngram(n-j)
        }
        ret += Flag.labda(i - 1) * getProb(igram)
      }
      ret
    }
  }

  private def getProb(ngram: Array[T]): Double = {
    val node = root.search(ngram)
    if(null != node){
      node.getProb
    } else {
      1.0 / root.getCount
    }
  }
}


object Transition {
  def apply[T <: Comparable[T]](r: T): Transition[T] = {
    val t = new Transition[T]()
    t.root = new TreeNodeImpl[T](r)
    t
  }
  
  def apply[T <: Comparable[T]](root: TreeNode[T], bank: NodeBank[T]): Transition[T] = {
    val t = new Transition[T]()
    t.root = root
    t.setStateBank(bank)
    t
  }
}