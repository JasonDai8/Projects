package deque;

import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic linked list deque tests. */
public class LinkedListDequeTest {

    /** You MUST use the variable below for all of your tests. If you test
     * using a local variable, and not this static variable below, the
     * autograder will not grade that test. If you would like to test
     * LinkedListDeques with types other than Integer (and you should),
     * you can define a new local variable. However, the autograder will
     * not grade that test.
     */

    public static Deque<Integer> lld = new LinkedListDeque<Integer>();

    @Test
    public void addIsEmptySizeTest() {

        assertTrue("A newly initialized LLDeque should be empty", lld.isEmpty());

        lld.addFirst(0);
        assertFalse("lld should now contain 1 item", lld.isEmpty());

        lld.addFirst(0);
        assertFalse("lld should now contain 2 item", lld.isEmpty());

        // Reset the linked list deque at the END of the test.
        lld = new LinkedListDeque<Integer>();
    }

    @Test
    public void addRemoveFirstTest() {

        lld.addFirst(69);
        assertFalse("lld should now contain 1 item", lld.isEmpty());

        assertEquals("First item should be 69", 69, (int) lld.removeFirst());

        lld.addFirst(10);
        lld.addFirst(11);

        assertEquals("First item should be 11", 11, (int) lld.removeFirst());
        assertEquals("First item should be 10", 10, (int) lld.removeFirst());

        // Reset the linked list deque at the END of the test.
        lld = new LinkedListDeque<Integer>();
    }

    @Test
    public void addRemoveLastTest() {

        lld.addFirst(1);
        lld.addLast(2);
        lld.addLast(3);

        assertEquals("First item should be 1", 1, (int) lld.removeFirst());
        assertEquals("Last item should be 3", 3, (int) lld.removeLast());
        assertEquals("Last item should be 2", 2, (int) lld.removeLast());

        // Reset the linked list deque at the END of the test.
        lld = new LinkedListDeque<Integer>();
    }

    @Test
    public void printDeque() {

        lld.printDeque();

        lld.addFirst(1);
        lld.printDeque();

        lld.addLast(2);
        lld.printDeque();

        lld.addLast(3);
        lld.printDeque();

        // Reset the linked list deque at the END of the test.
        lld = new LinkedListDeque<Integer>();
    }

    @Test
    public void getTest() {

        lld.addFirst(1);
        lld.addLast(2);
        lld.addLast(3);

        assertEquals("First item should be 1", 1, (int) lld.get(0));
        assertEquals("Second item should be 2", 2, (int) lld.get(1));
        assertEquals("Third item should be 3", 3, (int) lld.get(2));

        // Reset the linked list deque at the END of the test.
        lld = new LinkedListDeque<Integer>();
    }

    @Test
    public void getRecursiveTest() {

        lld.addFirst(1);
        lld.addLast(2);
        lld.addLast(3);

        assertEquals("First item should be 1", 1, (int) ((LinkedListDeque<Integer>) lld).getRecursive(0));
        assertEquals("Second item should be 2", 2, (int) ((LinkedListDeque<Integer>) lld).getRecursive(1));
        assertEquals("Third item should be 3", 3, (int) ((LinkedListDeque<Integer>) lld).getRecursive(2));

        // Reset the linked list deque at the END of the test.
        lld = new LinkedListDeque<Integer>();
    }

    @Test
    public void testEquals() {

        lld.addFirst(1);
        lld.addLast(2);
        lld.addLast(3);

        Deque<Integer> lld2 = new LinkedListDeque<Integer>();
        lld2.addFirst(1);
        lld2.addLast(2);
        lld2.addLast(3);

        assertTrue("lld should now contain 1 item", lld.equals(lld2));

        lld2.addLast(3);
        assertFalse("lld should now contain 1 item", lld.equals(lld2));

        // Reset the linked list deque at the END of the test.
        lld = new LinkedListDeque<Integer>();
    }

    @Test
    public void testStringLL() {
        LinkedListDeque<String> stringLL = new LinkedListDeque<String>();
        stringLL.addFirst("testing");
        assertEquals("String should be `testing`", "testing", stringLL.get(0));
    }
}
