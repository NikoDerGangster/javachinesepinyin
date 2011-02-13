/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hmm

import junit.framework.TestCase;
import ngram.TreeNode

class ViterbiTest extends TestCase {
  def testViterbi(): Unit = {
    try{
      val viterbi = new Viterbi[String,String]()
      initTestData(viterbi);
      viterbi.n = 2

      val o = List("T","H","T","H","T","H")
      val s = viterbi.caculateWithLog(o)
      val sb = new StringBuilder
      for(state <- s){
        sb.append(state).append(" ");
      }
      assert (sb.toString().trim().equals("three three three three three two"));
      println(sb)
    }catch{
      case ex =>
        ex.printStackTrace
    }
  }

  def initTestData(v: Viterbi[String, String]) {
    val s1 = v.stateBank.add("one");
    val s2 = v.stateBank.add("two");
    val s3 = v.stateBank.add("three");

    val o1 = v.observeBank.add("H");
    val o2 = v.observeBank.add("T");

    //transition
    //0.8 0.1 0.1
    //0.1 0.8 0.1
    //0.1 0.1 0.8
    v.tran = Transition[String]("root")
    v.tran.setStateBank(v.stateBank);
    v.tran.setProb(s1.index, s1.index, 0.3);
    v.tran.setProb(s1.index, s2.index, 0.3);
    v.tran.setProb(s1.index, s3.index, 0.4);
    v.tran.setProb(s2.index, s1.index, 0.2);
    v.tran.setProb(s2.index, s2.index, 0.6);
    v.tran.setProb(s2.index, s3.index, 0.2);
    v.tran.setProb(s3.index, s1.index, 0.2);
    v.tran.setProb(s3.index, s2.index, 0.2);
    v.tran.setProb(s3.index, s3.index, 0.6);
//    v.tran.root.buildIndex(v.tran.root.getCount);
    TreeNode.printNode("", v.tran.root)
    //emission
    //    o1 o2
    //s1  0.5 0.5
    //s2  0.8 0.2
    //s3  0.2 0.8
    v.e.setProb(o1.index, s1.index, 0.5);
    v.e.setProb(o2.index, s1.index, 0.5);
    v.e.setProb(o1.index, s2.index, 0.8);
    v.e.setProb(o2.index, s2.index, 0.2);
    v.e.setProb(o1.index, s3.index, 0.2);
    v.e.setProb(o2.index, s3.index, 0.8);
    v.e.setObserveBank(v.observeBank);

    //Pi = [0.2 0.4 0.4]
    v.pi.setPi(s1.index, 0.2);
    v.pi.setPi(s2.index, 0.4);
    v.pi.setPi(s3.index, 0.4);
  }
}
