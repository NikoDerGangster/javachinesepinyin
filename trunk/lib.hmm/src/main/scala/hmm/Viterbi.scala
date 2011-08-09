/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hmm

import hmm.exception.HMMRuntimeException

class Result {
  var states: Array[Array[Int]] = null;
  var delta: Array[Array[Double]] = null;
  var psai: Array[Array[Int]] = null;
}

class Viterbi[S <: Comparable[S], O <: Comparable[O]] {
  var stateBank = new NodeBank[S]
  var observeBank = new NodeBank[O]
  var tran: Transition[S] = null
  var pi = new Pi
  var e = new Emission[O]
  var n = 3

  @throws(classOf[HMMRuntimeException])
  def caculateResult(listObserve: List[O]): Result =  {
    val ret = new Result
    if(listObserve.isEmpty) {
      throw new HMMRuntimeException("observe list is empty")
    }

    val o = listObserve(0);
    //Add observe node if absent, and get Node[O] object.
    var o1 = observeBank.add(o)
    
    val relatedStates = e.getStateProbByObserve(o1.name)
    if(relatedStates.isEmpty) {
      throw new HMMRuntimeException("unknown observe object " + o + ".")
    }
    ret.states = new Array[Array[Int]](listObserve.length)
    ret.delta = new Array[Array[Double]](listObserve.length)
    ret.psai = new Array[Array[Int]](listObserve.length)
    ret.states(0) = new Array[Int](relatedStates.size)
    ret.delta(0) = new Array[Double](relatedStates.size)
    ret.psai(0) = new Array[Int](relatedStates.size)
    var index = 0
    for(s <- relatedStates) {
      ret.states(0)(index) = s
      ret.delta(0)(index) = Math.log(pi.getPi(s)) + Math.log(e.getProb(o1.index, s))
      ret.psai(0)(index) = 0
      index += 1
    }

    for(p <- 1 until listObserve.length) {
      val o = listObserve(p)
      var oi = observeBank.add(o)

      val stateSet = e.getStateProbByObserve(oi.name)
      if(stateSet.isEmpty){
        throw new HMMRuntimeException("unknown observe object " + o + ".")
      }
      ret.states(p) = new Array[Int](stateSet.size)
      ret.delta(p) = new Array[Double](stateSet.size)
      ret.psai(p) = new Array[Int](stateSet.size)
      var i = 0
      for(state <- stateSet){
        ret.states(p)(i) = state
        var maxDelta = Double.NegativeInfinity
        var maxPsai = Double.NegativeInfinity
        var ls = 0
        for(j <- 0 until ret.states(p-1).length) {
          val statePath = Viterbi.getStatePath(ret.states, ret.psai, p - 1, n - 1, j)
          val b = Math.log(e.getProb(oi.index, state))
          val Aij = Math.log(tran.getProb(statePath, state))
          val psai_j = ret.delta(p - 1)(j) + Aij
          val delta_j = psai_j + b
          if(delta_j > maxDelta) {
            maxDelta = delta_j
          }
          if(psai_j > maxPsai){
            maxPsai = psai_j
            ls = j
          }
        }

        ret.delta(p)(i) = maxDelta;
        ret.psai(p)(i) = ls
        i += 1
      }
    }
    ret
  }

  @throws(classOf[HMMRuntimeException])
  def caculateWithLog(listObserve: List[O]): List[S] = {
    var path = List[S]()

    val r = caculateResult(listObserve)
    var maxProb = Double.NegativeInfinity
    var pos = 0;

    for(j <- 0 until r.delta(listObserve.length - 1).length) {
      val p = r.delta(listObserve.length - 1)(j)
      if(p > maxProb) {
        maxProb = p
        pos = j
      }
    }
    val statePath = Viterbi.getStatePath(r.states, r.psai, listObserve.length - 1, listObserve.length, pos)
    var i = 0
    for(state <- statePath){
//      path(i) = stateBank.get(state).name
//      i+=1
      path = stateBank.get(state).name :: path
    }
    path.reverse
  }
}

object Viterbi {
  def getStatePath(states: Array[Array[Int]], psai: Array[Array[Int]], end: Int, depth: Int, pos: Int): Array[Int] = {
    val maxDepth = if((end + 1) > depth) depth else end + 1;
    val ret = new Array[Int](maxDepth)
    var p = pos
    for(i <- 0 until maxDepth){
      val state = states(end-i)(p)
      p = psai(end-i)(p)
      ret(ret.length - i - 1) = state
    }
    ret
  }
}