package deque;

import org.junit.Test;

import static org.junit.Assert.*;

/* Performs some basic array deque tests. */
public class ArrayDequeTest {

    /** You MUST use the variable below for all of your tests. If you test
     * using a local variable, and not this static variable below, the
     * autograder will not grade that test. If you would like to test
     * ArrayDeques with types other than Integer (and you should),
     * you can define a new local variable. However, the autograder will
     * not grade that test.
     */

    public static Deque<Integer> ad = new ArrayDeque<Integer>();

    @Test
    public void addIsEmptySizeTest() {

        assertTrue("A newly initialized deque should be empty", ad.isEmpty());

        ad.addFirst(0);
        assertFalse("ad should now contain 1 item", ad.isEmpty());

        ad.addFirst(0);
        assertFalse("ad should now contain 2 item", ad.isEmpty());

        // Reset the linked list deque at the END of the test.
        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void addRemoveFirstTest() {

        ad.addFirst(69);
        ad.printDeque();
        assertFalse("ad should now contain 1 item", ad.isEmpty());

        ad.printDeque();
        assertEquals("First item should be 69", 69, (int) ad.removeFirst());

        ad.addFirst(10);
        ad.addFirst(11);

        assertEquals("First item should be 11", 11, (int) ad.removeFirst());
        assertEquals("First item should be 10", 10, (int) ad.removeFirst());

        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);
        ad.addFirst(11);

        // Reset the linked list deque at the END of the test.
        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void addRemoveLastTest() {

        ad.addFirst(1);
        ad.addLast(2);
        ad.addLast(3);

        assertEquals("First item should be 1", 1, (int) ad.removeFirst());
        assertEquals("Last item should be 3", 3, (int) ad.removeLast());
        assertEquals("Last item should be 2", 2, (int) ad.removeLast());

        // Reset the linked list deque at the END of the test.
        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void printDeque() {

        ad.printDeque();

        ad.addFirst(1);
        ad.printDeque();

        ad.addLast(2);
        ad.printDeque();

        ad.addLast(3);
        ad.printDeque();

        // Reset the linked list deque at the END of the test.
        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void getTest() {

        ad.addFirst(1);
        assertEquals("First item should be 1", 1, (int) ad.get(0));
        ad.printDeque();
        ad.addLast(2);
        ad.printDeque();
        ad.addLast(3);
        ad.addLast(4);
        ad.addLast(5);
        ad.addLast(6);

        assertEquals("First item should be 1", 1, (int) ad.get(0));
        assertEquals("Second item should be 2", 2, (int) ad.get(1));
        assertEquals("Third item should be 3", 3, (int) ad.get(2));

        // Reset the linked list deque at the END of the test.
        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void testEquals() {

        ad.addFirst(1);
        ad.addLast(2);
        ad.addLast(3);

        Deque<Integer> ad2 = new ArrayDeque<Integer>();
        ad2.addFirst(1);
        ad2.addLast(2);
        ad2.addLast(3);

        assertTrue("should be identical", ad.equals(ad2));

        ad2.addLast(3);
        assertFalse("should not be identical", ad.equals(ad2));


        Deque<Integer> lld2 = new LinkedListDeque<Integer>();
        lld2.addFirst(1);
        lld2.addLast(2);
        lld2.addLast(3);
        lld2.addLast(3);

        assertTrue("lld should now contain 1 item", ad2.equals(lld2));

        // Reset the linked list deque at the END of the test.
        ad = new ArrayDeque<Integer>();
    }

    @Test
    public void testStringLL() {
        ArrayDeque<String> stringLL = new ArrayDeque<String>();
        stringLL.addFirst("testing");
        assertEquals("String should be `testing`", "testing", stringLL.get(0));
    }

    //@Test
//    public void testLL() {
//        System.out.println(Arrays.toString(((ArrayDeque) ad).saved));
//        ad.addFirst(3);
//        ad.addFirst(2);
//        ad.addFirst(1);
//        ad.addLast(4);
//        ad.addLast(5);
//        ad.addLast(6);
//        ad.addLast(7);
//        System.out.println(Arrays.toString(((ArrayDeque) ad).saved));
//        System.out.println(((ArrayDeque) ad).head);
//        System.out.println(((ArrayDeque) ad).tail);
//
//        ad.addLast(8);
//        System.out.println(Arrays.toString(((ArrayDeque) ad).saved));
//        System.out.println(((ArrayDeque) ad).head);
//        System.out.println(((ArrayDeque) ad).tail);
//
//        ad.addLast(9);
//        System.out.println(Arrays.toString(((ArrayDeque) ad).saved));
//        System.out.println(((ArrayDeque) ad).head);
//        System.out.println(((ArrayDeque) ad).tail);
//
//        ad.addLast(10);
//        ad.addLast(11);
//        ad.addLast(12);
//        System.out.println(Arrays.toString(((ArrayDeque) ad).saved));
//        System.out.println(((ArrayDeque) ad).head);
//        System.out.println(((ArrayDeque) ad).tail);
//
//        ad.addFirst(0);
//        System.out.println(Arrays.toString(((ArrayDeque) ad).saved));
//        System.out.println(((ArrayDeque) ad).head);
//        System.out.println(((ArrayDeque) ad).tail);
//
//        ad.addLast(13);
//        ad.addLast(14);
//        ad.addLast(15);
//        ad.addLast(16);
//        ad.addLast(17);
//        ad.addLast(18);
//        System.out.println(Arrays.toString(((ArrayDeque) ad).saved));
//        System.out.println(((ArrayDeque) ad).head);
//        System.out.println(((ArrayDeque) ad).tail);
//    }

    @Test
    public void testLL() {

        for (int i = 0; i < 9; i++) {
            System.out.println("i = " + i);
            ad.addLast(i);
            ad.printDeque();
        }
        System.out.println("Size " + ad.size());
        ad.printDeque();
        for (int i = 0; i < 50; i++) {
            ad.addFirst(100 + i);
        }
        ad.printDeque();
        for (int i = 0; i < 59; i++) {
            System.out.println(ad.get(i));
        }
        System.out.println("Size " + ad.size());

        for (int i = 0; i < 20; i++) {
            System.out.println(ad.removeFirst());
            System.out.println(ad.removeLast());
        }
        ad.printDeque();
        System.out.println(ad.size());
        for (int i = 0; i < 7; i++) {
            System.out.println(ad.removeFirst());
            System.out.println(ad.removeLast());
        }
        ad.printDeque();

        System.out.println(ad.removeFirst());
        System.out.println(ad.removeLast());
        ad.printDeque();

        System.out.println(ad.removeLast());
        ad.printDeque();

        System.out.println(ad.removeLast());
        ad.printDeque();

        System.out.println(ad.removeLast());
        ad.printDeque();

        ad.addFirst(4);
        ad.addFirst(3);
        ad.addFirst(2);
        ad.addFirst(1);
        ad.printDeque();

        ad.addFirst(4);
        ad.addFirst(3);
        ad.addFirst(2);
        ad.addFirst(1);
        ad.addFirst(1);

        ad.printDeque();

        System.out.println(ad.removeFirst());
        System.out.println(ad.removeFirst());
        System.out.println(ad.removeLast());
        System.out.println(ad.removeLast());
        System.out.println(ad.removeLast());
        System.out.println(ad.removeLast());
        System.out.println(ad.removeLast());
        System.out.println(ad.removeLast());
        System.out.println(ad.removeLast());
        System.out.println(ad.removeLast());

        ad = new ArrayDeque<Integer>();
    }

}
