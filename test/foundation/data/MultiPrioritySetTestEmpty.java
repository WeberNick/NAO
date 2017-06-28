package foundation.data;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import foundation.data.MultiPrioritySet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * <p>Tests the {@link MultiPrioritySet MultiPrioritySet} starting with an empty set.</p>
 * @author Julian Betz
 * @version 1.00
 */
public class MultiPrioritySetTestEmpty {
	private static MultiPrioritySet<String> mps;
	private static SortedSet<String> set;
	private static PriorityQueue<String> queue;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ArrayList<Comparator<String>> comparators = new ArrayList<Comparator<String>>(1);
		comparators.add((String a, String b) -> a.length() - b.length());
		mps = new MultiPrioritySet<String>(comparators);
		set = new TreeSet<String>();
		queue = new PriorityQueue<String>(comparators.get(0));
	}

	@After
	public void tearDown() throws Exception {
		mps.clear();
		set.clear();
		queue.clear();
	}
	
	@Test
	public void testAdd() {
		String[] input = {"AAA", "BBB", "AAAA", "aaa", "AAA", "AA"};
		boolean[] expected = {false, false, false, false, true, false};
		for (int i = 0; i < input.length; i++) {
			assertTrue("Element " + i + " (" + input[i] + ") was not evaluated as expected", mps.add(input[i]) ^ expected[i]);
			if (set.add(input[i]))
				queue.add(input[i]);
			checkElements();
			assertEquals("The size of the set has a wrong value", mps.size(), set.size());
		}
	}

	@Test
	public void testAddAll() {
		String[][] input = {{"AAA", "BBB"}, {"AAAA", new String("AAA")}, {new String("AAA"), new String("BBB")}};
		boolean[] expected = {true, true, false};
		for (int i = 0; i < input.length; i++) {
			ArrayList<String> list = new ArrayList<String>(input[i].length);
			for (int j = 0; j < input[i].length; j++) {
				list.add(input[i][j]);
				if (set.add(input[i][j]))
					queue.add(input[i][j]);
			}
			assertEquals("Set " + i + " was not evaluated as expected", mps.addAll(list), expected[i]);
			checkElements();
			assertEquals("The size of the set has a wrong value", mps.size(), set.size());
		}
	}
	
	private void checkElements() {
		Iterator<String> jt = set.iterator();
		for (Iterator<String> it = mps.iterator(); it.hasNext();) {
			assertTrue("The expected size of the set is less than its actual size", jt.hasNext());
			assertEquals("The content of the set does not equal the expected elements", it.next(), jt.next());
		}
		assertFalse("The expected size of the set is greater than its actual size", jt.hasNext());
		ArrayList<String> list = new ArrayList<String>(queue.size());
		for (Iterator<String> it = mps.iterator(0); it.hasNext();) {
			String s = queue.poll();
			assertNotNull("The expected size of the set is less than its actual size", s);
			assertTrue("The content of the set does not equal the expected elements", queue.comparator().compare(it.next(), s) == 0);
			list.add(s);
		}
		assertNull("The expected size of the set is greater than its actual size", queue.poll());
		queue.addAll(list);
	}
}
