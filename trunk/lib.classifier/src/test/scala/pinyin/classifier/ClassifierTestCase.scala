/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pinyin.classifier

import junit.framework.TestCase

class ClassifierTestCase extends TestCase {

  def testWordToPinyin() {
    val wtp = new WordToPinyin
    wtp load "wtp.m"
    wtp.init
    println("wtp init completed.")
//    wtp.stateBank.indexMap.toList.foreach(println(_))

    wtp.observeBank.indexMap.toList.foreach {
      x => {
        val word = x._1
        val relatedStates = wtp.viterbi.e.getStateProbByObserve(word)
        for(val state <- relatedStates) {
          println(word + " " + wtp.getStateBy(state))
        }
      }
    }
  }

}
