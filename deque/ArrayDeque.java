package deque;

import java.util.Arrays;

public class ArrayDeque<T> implements Deque<T> {

    private Object[] saved;
    private int storedSize;
    private int arraySize;
    private int head;
    private int tail;

    public ArrayDeque() {
        arraySize = 8;
        storedSize = 0;
        saved = new Object[8];
        head = 0;
        tail = -1;
    }

    @Override
    public void addFirst(T item) {

        if (storedSize >= arraySize) {
            resize(arraySize * 2);
        }

        // We have room in the existing array to add new elements
        int insertIndex = head - 1;
        if (insertIndex < 0) {
            insertIndex = arraySize - 1;
        }
        saved[insertIndex] = item;
        head = insertIndex;
        if (tail == -1) {
            tail = head;
        }
        storedSize += 1;

    }

    @Override
    public void addLast(T item) {
        if (storedSize >= arraySize) {
            resize(arraySize * 2);
        }

        // We have room in the existing array to add new elements
        int insertIndex = tail + 1;
        if (insertIndex >= arraySize) {
            insertIndex = 0;
        }
        saved[insertIndex] = item;
        tail = insertIndex;
        storedSize += 1;
    }

    /*
    Resizes the array and allocates more space
     */
    private void resize(int size) {
        if (tail == -1) {
            tail = 0;
        }
        Object[] resized = new Object[size];

        for (int i = 0; i < storedSize; i++) {
            resized[i] = this.get(i);
        }
        tail = storedSize - 1;
        head = 0;
        saved = resized;
        arraySize = size;
    }

    @Override
    public void printDeque() {
        if (storedSize == 0) {
            return;
        }
        for (int i = 0; i < storedSize - 1; i++) {
            System.out.print(this.get(i) + " ");
        }
        System.out.println(this.get(storedSize - 1));
    }

    @Override
    public T removeFirst() {
        if (size() == 0 || saved[head] == null) {
            return null;
        }
        if ((storedSize - 1) <= 0.25 * arraySize && arraySize >= 16) {
            resize(arraySize / 2);
        }
        storedSize--;
        T ref = (T) saved[head];
        saved[head] = null;
        head++;
        if (head > arraySize - 1) {
            head = 0;
        }
        return ref;
    }

    @Override
    public T removeLast() {
        if (size() == 0 || saved[tail] == null) {
            return null;
        }
        if ((storedSize - 1) <= 0.25 * arraySize && arraySize >= 16) {
            resize(arraySize / 2);
        }
        storedSize--;
        T ref = (T) saved[tail];
        saved[tail] = null;
        tail--;
        if (tail < 0) {
            tail = arraySize - 1;
        }
        return ref;
    }

    @Override
    public T get(int index) {
        return (T) saved[(head + index) % arraySize];
    }

    @Override
    public int size() {
        return this.storedSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Deque)) {
            return false;
        }
        Deque otherLst = (Deque) obj;
        Deque ref = this;

        if (otherLst.size() != ref.size()) {
            return false;
        }

        int comp = otherLst.size() - 1;
        while (comp >= 0) {
            if (!otherLst.get(comp).equals(ref.get(comp))) {
                return false;
            }
            comp--;
        }

        return true;
    }
}
