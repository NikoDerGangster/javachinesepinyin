/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hmm

import scala.collection.mutable.Map

class Pi {

  val pi = Map[Int, Double]();
  var total = 1;

  def getPi(index: Int): Double = pi.getOrElse(index,1.0/total)

  def setPi(index: Int, prob: Double): Unit = pi.put(index,prob)
}

object Pi {
  def apply(pii: Map[Int, Int]): Pi = {
    val keySet = pii.keySet;
    val pi = new Pi()
    var t = 0
    keySet.foreach(t += pii(_))
    pi.total = t
    for (i <- keySet) {
      pi.setPi(i, pii(i).asInstanceOf[Double] / t);
    }
    pi
  }
}