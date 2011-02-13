/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hmm

import scala.collection.mutable.Map

class Emission[T <: Comparable[T]] {

  val matrix = Map[Int,Map[Int, Double]]()
  var total = 0
  var observeBank = new NodeBank[T]()

  def getObserveBank = observeBank

  def setObserveBank(bank: NodeBank[T]): Unit = {
    observeBank = bank
  }

  def getProb(o: Int, s: Int): Double = {
    val map = matrix.get(o)
    map match {
      case None =>
        1.0 / total
      case Some(e) =>
        e.getOrElse(s,1.0/total)
    }
  }

  def getStateProbByObserve(observe: T) = {
    val index = observeBank.get(observe).index
    matrix.get(index) match {
      case Some(map) =>
        map.keySet
      case None =>
        val m = add(observe)
        m.keySet
    }
  }

  private def add(observe: T): Map[Int, Double] = {
    val keySet = observeBank.keySet
    var simSet = Set[T]();
    val o = observe.toString
    for(k <- keySet){
      val key = k.toString
      if(key startsWith o){
        simSet += k
      }
    }
    val probMap = Map[Int,Double]()
    val index = observeBank.get(observe).index
    for(k <- simSet) {
      matrix.get(observeBank.get(k).index) match {
        case Some(map) =>
          val set = map.keySet
          set.foreach(state => probMap.put(state, map(state)))
        case None =>
      }
    }

    matrix.put(index, probMap)
    probMap
  }

  def setProb(o: Int, s: Int, prob: Double) {
    matrix.get(o) match {
      case Some(map)=>
        map.put(s,prob)
      case None =>
        val map = Map[Int,Double]();
        matrix.put(o, map)
        map.put(s, prob)
    }
  }
}

object Emission {
  def apply[T <: Comparable[T]](emisMatrix: Map[Int, Map[Int, Int]]): Emission[T] = {
    val emis = new Emission[T]
    val states = emisMatrix.keySet
    for(state <- states){
      val mapO = emisMatrix(state)
      var sum = 1
      val observes = mapO.keySet
      observes.foreach(sum += mapO(_))
      observes.foreach(o => emis.setProb(o, state, mapO(o).asInstanceOf[Double]/sum))
      emis.total += sum
    }
    emis
  }
}