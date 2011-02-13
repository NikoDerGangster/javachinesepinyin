/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ngram

import ngram.TreeNode._
import junit.framework.TestCase

class NgramTest extends TestCase {

  def testNgram() {
    val a = "a";
    val a1 = "abc";
    val a2 = "acb";
    val b = "b";
    val b1 = "bc";
    val root = new TreeNodeImpl[Character]('r');
    try{
      root.insert(stringToCharArray(a));
      root.insert(stringToCharArray(a2));
      root.insert(stringToCharArray(a1));
      root.insert(stringToCharArray(b));
      root.insert(stringToCharArray(a1));
      root.insert(stringToCharArray(b1));
      TreeNode.printNode("",root);

      root.buildIndex(root.getCount);

      TreeNode.printNode("",root);
    }catch{
      case e => e.printStackTrace
    }
  }
}
