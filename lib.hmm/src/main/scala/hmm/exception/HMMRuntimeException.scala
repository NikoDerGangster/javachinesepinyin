/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hmm.exception

class HMMRuntimeException(val detail: String) extends Exception {
  def this() = this("")

  override def getMessage(): String = {
    return detail;
  }

  override def toString(): String = {
    return "HMM Runtime Exception : " + detail;
  }
}
