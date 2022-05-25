package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comp;

    public MaxArrayDeque(Comparator<T> c) {
        this.comp = c;
    }

    public T max() {
        if (this.size() == 0) {
            return null;
        }
        T max_item = this.get(0);
        int i = this.size() - 1;
        while (i >= 0) {
            if (comp.compare(max_item, this.get(i)) < 0) {
                max_item = this.get(i);
            }
            i--;
        }
        return max_item;
    }

    public T max(Comparator<T> c) {
        Comparator<T> ref = this.comp;
        this.comp = c;
        T max_item = max();
        this.comp = ref;
        return max_item;
    }
}
