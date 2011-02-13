/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hmm

import scala.collection.mutable.Map

class Node[T <: Comparable[T]](var index: Int, var name: T) {
  def this(name: T) = this(-1, name)
}

class NodeBank[T <: Comparable[T]] extends java.io.Serializable {
  type N = Node[T]
  var bank: List[T] = Nil
  val indexMap: Map[T, Int] = Map[T, Int]()
  def add[U <: T](node: T): N = {
    val name = node
    if(!indexMap.contains(name)){
      val index = bank.size
      if(bank.isEmpty) {
        bank = name :: bank
      } else {
        bank = bank ::: List(name)
      }
      indexMap(name) = index
      new Node(index,name)
    } else {
      get(name)
    }
  }

  def get(index: Int): N =
    if (bank.size > index) {
      new Node(index,bank(index));
    } else {
      null;
    }

  def get(name: T): N = 
    if(indexMap.contains(name)){
      get(indexMap(name))
    } else {
      null
    }

  def keySet = indexMap.keySet
}

object Observe {
  def apply[T <: Comparable[T]](name: T): Node[T] = {
    new Node[T](name)
  }

  def apply[T <: Comparable[T]](index: Int, name: T): Node[T] = {
    new Node[T](index,name)
  }
}

object State {
  def apply[T <: Comparable[T]](name: T): Node[T] = {
    new Node[T](name)
  }

  def apply[T <: Comparable[T]](index: Int, name: T): Node[T] = {
    new Node[T](index, name)
  }
}