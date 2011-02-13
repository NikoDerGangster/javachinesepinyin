/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hmm

import java.util.Properties

object Flag {

  val n = 3;
  val p = new Properties();
  val labda: Array[Double] = new Array[Double](n)
  var ngramMinimumShowTimes = 1;

  {
    try {
      p.load(Flag.getClass.getClassLoader().getResourceAsStream("pinyin.properties"));
    } catch  {
      case ex =>
        System.out.println(ex.getMessage());
    }

    labda(0) = 1.0 / 6.0;
    labda(1) = 1.0 / 3.0;
    labda(2) = 1.0 / 2.0;
    ngramMinimumShowTimes = Integer.parseInt(p.getProperty("NgramMinimumShowTimes", "1"));
  }

  def getNgramMinimumShowTimes = ngramMinimumShowTimes  
}
