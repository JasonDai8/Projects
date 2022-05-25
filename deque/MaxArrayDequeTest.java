package deque;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    public static Deque<Integer> ad = new MaxArrayDeque<Integer>(new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    });

    @Test
    public void addIsEmptySizeTest() {

        assertTrue("A newly initialized deque should be empty", ad.isEmpty());

        ad.addFirst(1);
        assertFalse("ad should now contain 1 item", ad.isEmpty());

        ad.addFirst(2);
        assertFalse("ad should now contain 2 item", ad.isEmpty());

        ad.addFirst(20);
        ad.addFirst(-1);
        ad.addFirst(3);
        ad.printDeque();

        System.out.println(((MaxArrayDeque) ad).max());

        System.out.println(((MaxArrayDeque) ad).max(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Math.min(o1, o2);
            }
        }));


        // Reset the linked list deque at the END of the test.
        ad = new ArrayDeque<Integer>();
    }
}