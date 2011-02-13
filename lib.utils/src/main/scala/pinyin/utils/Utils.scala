/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pinyin.utils

class Window[T](var size: Int) {

  private var datas = List[T]();

  def this() = this(0)

  def clear() {
    datas = List[T]()
  }

  def add(data: T) {
    if(null != data){
      if (datas.size > size) {
        datas = datas.tail
      }
      datas = datas ::: List[T](data)
    }
  }
  
  def toList() = datas
}
