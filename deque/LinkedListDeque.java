package deque;

public class LinkedListDeque<T> implements Deque<T> {

    private int size;
    private DLLNode sentinel;

    // Defines the Doubly Linked List Node Class
    private static class DLLNode<T> {
        private T item;
        private DLLNode<T> next;
        private DLLNode<T> prev;

        DLLNode(T item) {
            this.item = item;
            this.next = null;
            this.prev = null;
        }
    }

    public LinkedListDeque() {
        // Create a sentinel node with the inner value of 69
        this.sentinel = new DLLNode<Integer>(69);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        this.size = 0;
    }

    @Override
    public void addFirst(T item) {
        DLLNode<T> middle = new DLLNode<>(item);
        sentinel.next.prev = middle;
        middle.prev = sentinel;
        middle.next = sentinel.next;
        sentinel.next = middle;
        this.size += 1;
    }

    @Override
    public void addLast(T item) {
        DLLNode<T> middle = new DLLNode<>(item);
        sentinel.prev.next = middle;
        middle.prev = sentinel.prev;
        middle.next = sentinel;
        sentinel.prev = middle;
        this.size += 1;
    }

    @Override
    public void printDeque() {
        if (size() == 0) {
            return;
        }
        DLLNode ref = sentinel.next;
        while (ref.next != sentinel) {
            System.out.print(ref.item + " ");
            ref = ref.next;
        }
        System.out.print(ref.item);
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size() == 0) {
            return null;
        }
        DLLNode ref = sentinel.next.next;
        DLLNode removed = sentinel.next;
        sentinel.next.next.prev = sentinel;
        sentinel.next = ref;
        size--;
        return (T) removed.item;
    }

    @Override
    public T removeLast() {
        if (size() == 0) {
            return null;
        }
        DLLNode ref = sentinel.prev.prev;
        DLLNode removed = sentinel.prev;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = ref;
        size--;
        return (T) removed.item;
    }

    @Override
    public T get(int index) {
        int savedIndex = index;
        DLLNode ref = sentinel.next;
        while (index > 0) {
            index--;
            ref = ref.next;
        }
        return (size() == 0 || savedIndex > size() + 1) ? null : (T) ref.item;
    }

    public T getRecursive(int index) {
        if (index > size() + 1) {
            return null;
        }
        return (T) getRecursiveHelper(index, sentinel.next);
    }

    /**
     * Assists the recursive 'get' function
     * @param index position to retrieve item
     * @param ref reference DLLNode
     * @return item at position denoted by index
     */
    private T getRecursiveHelper(int index, DLLNode ref) {
        if (index == 0) {
            return (T) ref.item;
        } else {
            return (T) getRecursiveHelper(--index, ref.next);
        }
    }

    @Override
    public int size() {
        return this.size;
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
