package com.ripplemap.ds;

import com.ripplemap.algo.SlidingWindowPeakImpact;
import com.ripplemap.algo.ThresholdRuleValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Phase 2: Linear Data Structures Unit Tests")
public class LinearDataStructuresTest {

    @Test
    @DisplayName("Singly Linked List: CRUD, Iterative & Recursive Reverse, Middle Pointer")
    void testSinglyLinkedList() {
        CustomSinglyLinkedList<Integer> list = new CustomSinglyLinkedList<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());

        // Insertions
        list.addLast(10);
        list.addFirst(5);
        list.addLast(20);
        list.add(1, 8); // [5, 8, 10, 20]
        assertEquals(4, list.size());
        assertEquals(5, list.get(0));
        assertEquals(8, list.get(1));
        assertEquals(10, list.get(2));
        assertEquals(20, list.get(3));

        // Find middle on even elements: [5, 8, 10, 20] -> 10
        assertEquals(10, list.findMiddle());

        list.addLast(30); // [5, 8, 10, 20, 30]
        // Find middle on odd elements -> 10
        assertEquals(10, list.findMiddle());

        // Iterative reverse: [30, 20, 10, 8, 5]
        list.reverse();
        assertEquals(30, list.get(0));
        assertEquals(5, list.get(4));

        // Recursive reverse: restores to [5, 8, 10, 20, 30]
        list.reverseRecursive();
        assertEquals(5, list.get(0));
        assertEquals(30, list.get(4));

        // Deletions
        assertTrue(list.removeValue(10));
        assertFalse(list.contains(10));
        assertEquals(4, list.size());

        assertEquals(5, list.removeFirst());
        assertEquals(3, list.size());

        assertEquals(20, list.remove(1)); // removes element at index 1 (which was 20)
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("Doubly Linked List: O(1) removeNode and moveToHead for LRU")
    void testDoublyLinkedList() {
        CustomDoublyLinkedList<String> dll = new CustomDoublyLinkedList<>();
        CustomDoublyLinkedList.Node<String> nodeA = dll.addLast("A");
        CustomDoublyLinkedList.Node<String> nodeB = dll.addLast("B");
        CustomDoublyLinkedList.Node<String> nodeC = dll.addLast("C");
        assertEquals(3, dll.size());

        // Move B to head: order becomes B, A, C
        dll.moveToHead(nodeB);
        assertEquals("B", dll.getHead().data);
        assertEquals("C", dll.getTail().data);

        // Remove node A: order becomes B, C
        dll.removeNode(nodeA);
        assertEquals(2, dll.size());
        assertEquals("B", dll.getHead().data);
        assertEquals("C", dll.getHead().next.data);

        // Remove head and tail
        assertEquals("B", dll.removeFirst());
        assertEquals("C", dll.removeLast());
        assertTrue(dll.isEmpty());
    }

    @Test
    @DisplayName("Circular Linked List: Round-Robin Navigation for Event Replay")
    void testCircularLinkedList() {
        CustomCircularLinkedList<String> cll = new CustomCircularLinkedList<>();
        assertTrue(cll.isEmpty());

        cll.insert("Event 1");
        cll.insert("Event 2");
        cll.insert("Event 3");
        assertEquals(3, cll.size());

        // Round robin verification
        assertEquals("Event 1", cll.current());
        assertEquals("Event 2", cll.next());
        assertEquals("Event 3", cll.next());
        assertEquals("Event 1", cll.next()); // wraps back to 1
        assertEquals("Event 3", cll.previous()); // previous from 1 is 3
    }

    @Test
    @DisplayName("Dynamic Array (CustomArrayList): Automatic Resizing & Operations")
    void testCustomArrayList() {
        CustomArrayList<Integer> arr = new CustomArrayList<>(2);
        assertEquals(0, arr.size());

        // Exceed initial capacity to test doubling
        for (int i = 0; i < 15; i++) {
            arr.add(i * 10);
        }
        assertEquals(15, arr.size());
        assertTrue(arr.capacity() >= 16);

        assertEquals(0, arr.get(0));
        assertEquals(140, arr.get(14));

        // Insert at index
        arr.add(5, 999);
        assertEquals(16, arr.size());
        assertEquals(999, arr.get(5));

        // Remove at index
        assertEquals(999, arr.remove(5));
        assertEquals(15, arr.size());

        // Remove value
        assertTrue(arr.remove((Integer) 140));
        assertEquals(14, arr.size());
        assertFalse(arr.contains(140));
    }

    @Test
    @DisplayName("Stack Implementations: ArrayStack and LinkedListStack")
    void testStacks() {
        testStackBehavior(new ArrayStack<>());
        testStackBehavior(new LinkedListStack<>());
    }

    private void testStackBehavior(CustomStack<String> stack) {
        assertTrue(stack.isEmpty());
        stack.push("Alpha");
        stack.push("Beta");
        stack.push("Gamma");
        assertEquals(3, stack.size());
        assertEquals("Gamma", stack.peek());

        assertEquals("Gamma", stack.pop());
        assertEquals("Beta", stack.pop());
        assertEquals("Alpha", stack.pop());
        assertTrue(stack.isEmpty());
        assertThrows(NoSuchElementException.class, stack::pop);
    }

    @Test
    @DisplayName("Threshold Rule Validator: Balanced Brackets & Postfix Evaluation")
    void testThresholdRuleValidator() {
        // Balanced brackets
        assertTrue(ThresholdRuleValidator.isBalanced("((severity > 5) && [delay < 24])"));
        assertTrue(ThresholdRuleValidator.isBalanced("{[()]}"));
        assertFalse(ThresholdRuleValidator.isBalanced("((severity > 5)"));
        assertFalse(ThresholdRuleValidator.isBalanced("([)]"));

        // Infix to Postfix & Evaluation: (3 + 4) * 2 = 14
        String postfix = ThresholdRuleValidator.infixToPostfix("( 3 + 4 ) * 2");
        assertEquals("3 4 + 2 *", postfix);
        double result = ThresholdRuleValidator.evaluatePostfix(postfix);
        assertEquals(14.0, result, 1e-6);

        // Power operator: 2 ^ 3 = 8
        assertEquals(8.0, ThresholdRuleValidator.evaluatePostfix("2 3 ^"), 1e-6);
    }

    @Test
    @DisplayName("Queue Implementations: LinkedListQueue and ArrayCircularQueue")
    void testQueues() {
        testQueueBehavior(new LinkedListQueue<>());
        testQueueBehavior(new ArrayCircularQueue<>(4));
    }

    private void testQueueBehavior(CustomQueue<Integer> queue) {
        assertTrue(queue.isEmpty());
        queue.enqueue(100);
        queue.enqueue(200);
        queue.enqueue(300);
        assertEquals(3, queue.size());
        assertEquals(100, queue.peek());

        assertEquals(100, queue.dequeue());
        assertEquals(200, queue.dequeue());

        queue.enqueue(400);
        queue.enqueue(500);

        assertEquals(300, queue.dequeue());
        assertEquals(400, queue.dequeue());
        assertEquals(500, queue.dequeue());
        assertTrue(queue.isEmpty());
        assertThrows(NoSuchElementException.class, queue::dequeue);
    }

    @Test
    @DisplayName("CustomDeque & SlidingWindowPeakImpact Monotonic Deque")
    void testDequeAndSlidingWindow() {
        CustomDeque<String> deque = new CustomDeque<>();
        deque.addFirst("Middle");
        deque.addFirst("Front");
        deque.addLast("Back"); // [Front, Middle, Back]
        assertEquals(3, deque.size());
        assertEquals("Front", deque.peekFirst());
        assertEquals("Back", deque.peekLast());

        assertEquals("Front", deque.removeFirst());
        assertEquals("Back", deque.removeLast());
        assertEquals("Middle", deque.removeFirst());
        assertTrue(deque.isEmpty());

        // Monotonic sliding window peak impact test
        // Impacts: [1.2, 3.5, 2.1, 4.8, 0.9, 5.2], window k = 3
        // Window 0: [1.2, 3.5, 2.1] -> max 3.5
        // Window 1: [3.5, 2.1, 4.8] -> max 4.8
        // Window 2: [2.1, 4.8, 0.9] -> max 4.8
        // Window 3: [4.8, 0.9, 5.2] -> max 5.2
        double[] impacts = {1.2, 3.5, 2.1, 4.8, 0.9, 5.2};
        CustomArrayList<SlidingWindowPeakImpact.WindowResult> windowResults =
            SlidingWindowPeakImpact.calculatePeakImpacts(impacts, 3);

        assertEquals(4, windowResults.size());
        assertEquals(3.5, windowResults.get(0).maxImpact, 1e-6);
        assertEquals(4.8, windowResults.get(1).maxImpact, 1e-6);
        assertEquals(4.8, windowResults.get(2).maxImpact, 1e-6);
        assertEquals(5.2, windowResults.get(3).maxImpact, 1e-6);
    }

    @Test
    @DisplayName("CustomHashMap & CustomHashSet: Chaining, Rehashing, and CRUD")
    void testHashMapAndSet() {
        CustomHashMap<String, Integer> map = new CustomHashMap<>(4, 0.75f);
        assertTrue(map.isEmpty());

        map.put("NodeA", 100);
        map.put("NodeB", 200);
        map.put("NodeC", 300);
        map.put("NodeD", 400); // Triggers rehashing

        assertEquals(4, map.size());
        assertEquals(100, map.get("NodeA"));
        assertEquals(200, map.get("NodeB"));
        assertEquals(300, map.get("NodeC"));
        assertEquals(400, map.get("NodeD"));

        // Overwrite existing key
        assertEquals(100, map.put("NodeA", 999));
        assertEquals(999, map.get("NodeA"));

        // Remove key
        assertEquals(200, map.remove("NodeB"));
        assertNull(map.get("NodeB"));
        assertFalse(map.containsKey("NodeB"));
        assertEquals(3, map.size());

        // CustomHashSet test
        CustomHashSet<String> set = new CustomHashSet<>();
        assertTrue(set.add("TSMC"));
        assertTrue(set.add("Samsung"));
        assertFalse(set.add("TSMC")); // duplicate returns false
        assertEquals(2, set.size());
        assertTrue(set.contains("TSMC"));
        assertTrue(set.remove("TSMC"));
        assertFalse(set.contains("TSMC"));
    }
}
