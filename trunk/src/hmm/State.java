/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmm;

/**
 *
 * @author ray
 */
public class State<T> implements Node<T>, java.io.Serializable {

    private int index = -1;
    private T name = null;

    public State(T name) {
        this.name = name;
    }

    public State(T name, int index) {
        this.name = name;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public T getName() {
        return name;
    }

    public void setName(T name) {
        this.name = name;
    }
}
