/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngram

import java.io.File

trait NgramModel[T <: Comparable[T]] {
  def getRootNode: TreeNode[T]
  def load(file: File): Unit
  def save(file: File): Unit
  def compact(file: File): Unit
  def getConditionProb(ngram: Array[T]): Double
}

trait TreeNode[T <: Comparable[T]] {
  def getKey: T
  def getCount: Int
  def getProb: Double
  def setProb(prob: Double): Unit
  def insert(ngram: Array[T]): TreeNode[T]
  def search(ngram: Array[T]): TreeNode[T]
  def traversal(op:(TreeNode[T]) => Unit)
  def getDescendants(): Array[TreeNode[T]]
  def buildIndex(c: Int)
}

object TreeNode{
  def stringToCharArray(s: String): Array[Character] = {
    val array = new Array[Character](s.length);
    for (i <- 0 until s.length) {
      array(i) = s.charAt(i);
    }
    return array;
  }

  def printNode[T <: Comparable[T]](indent: String, node: TreeNode[T]) {
    println(indent + node.getKey + " - " + node.getCount + " - " + node.getProb)
    node.getDescendants.foreach(printNode(indent+"  ", _))
  }
}