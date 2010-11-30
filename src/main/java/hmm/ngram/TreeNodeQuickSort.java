/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmm.ngram;

import java.util.Comparator;

/**
 *
 * @author ray
 */
public class TreeNodeQuickSort<T> implements TreeNodeSortor<T> {

    Comparator<TreeNode<T>> comparator = null;

    public void setComparator(Comparator<TreeNode<T>> comparator) {
        this.comparator = comparator;
    }

    public TreeNode<T>[] sort(TreeNode<T>[] values) {
        if (values == null || values.length == 0) {
            return values;
        }
        int number = values.length;
        quicksort(values, 0, number - 1);
        return values;
    }

    private void quicksort(TreeNode<T>[] numbers, int low, int high) {
        int i = low, j = high;
        // Get the pivot element from the middle of the list
        TreeNode<T> pivot = numbers[(low + high) / 2];

        // Divide into two lists
        while (i <= j) {
            // If the current value from the left list is smaller then the pivot
            // element then get the next element from the left list
            int c = comparator.compare(numbers[i], pivot);
            while (c < 0) {
                i++;
                c = comparator.compare(numbers[i], pivot);
            }
            // If the current value from the right list is larger then the pivot
            // element then get the next element from the right list
            c = comparator.compare(numbers[j], pivot);
            while (c > 0) {
                j--;
                c = comparator.compare(numbers[j], pivot);
            }

            // If we have found a values in the left list which is larger then
            // the pivot element and if we have found a value in the right list
            // which is smaller then the pivot element then we exchange the
            // values.
            // As we are done we can increase i and j
            if (i <= j) {
                exchange(numbers, i, j);
                i++;
                j--;
            }
        }
        // Recursion
        if (low < j) {
            quicksort(numbers, low, j);
        }
        if (i < high) {
            quicksort(numbers, i, high);
        }
    }

    private void exchange(TreeNode<T>[] numbers, int i, int j) {
        TreeNode<T> temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
    }
}
