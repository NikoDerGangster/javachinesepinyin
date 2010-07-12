/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmm;

/**
 * Graphic Model Node
 * @author ray
 */
public interface Node<T> {

    public int getIndex();

    public void setIndex(int index);

    public T getName();

    public void setName(T name);
}
