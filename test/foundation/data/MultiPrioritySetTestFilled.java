package foundation.data;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
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
 * <p>Tests the {@link MultiPrioritySet MultiPrioritySet} starting with a filled set.</p>
 * @author Julian Betz
 * @version 1.00
 */
public class MultiPrioritySetTestFilled {
	private static MultiPrioritySet<String> mps;
	private static SortedSet<String> set;
	private static PriorityQueue<String> queue;
	private static String[] contained = {"AAA", "BBB", "AAAA", "aaa", "AA"};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ArrayList<Comparator<String>> comparators = new ArrayList<Comparator<String>>(1);
		comparators.add((String a, String b) -> a.length() - b.length());
		mps = new MultiPrioritySet<String>(comparators);
		set = new TreeSet<String>();
		queue = new PriorityQueue<String>(comparators.get(0));
	}

	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < contained.length; i++) {
			String s = new String(contained[i]);
			mps.add(s);
			if (set.add(s))
				queue.add(s);
		}
	}

	@After
	public void tearDown() throws Exception {
		mps.clear();
		set.clear();
		queue.clear();
	}
	
	@Test
	public void testAdd() {
		String[] input = {"XXX", "GGG", "A", "012", "AAA", "aaaa", "aaa", new String("GGG")};
		boolean[] expected = {true, true, true, true, false, true, false, false};
		for (int i = 0; i < input.length; i++) {
			assertEquals("Element " + i + " (" + input[i] + ") was not evaluated as expected", mps.add(input[i]), expected[i]);
			if (set.add(input[i]))
				queue.add(input[i]);
			checkElements();
			assertEquals("The size of the set has a wrong value", mps.size(), set.size());
		}
	}
	
	@Test
	public void testAddAll() {
		String[][] input = {{"XXX", "GGG", "A"}, {"012", "AAA", "aaaa"}, {"aaa", new String("GGG")}};
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
	
	@Test
	public void testIntegrate() {
		String[] input = {"XXX", "GGG", "A", "012", "AAA", "aaaa", "aaa", new String("GGG")};
		boolean[] expected = {false, false, false, false, true, false, true, true};
		for (int i = 0; i < input.length; i++) {
			assertNotEquals("Element " + i + " (" + input[i] + ") did not change the reference as expected", input[i] == mps.integrate(input[i]), expected[i]);
			if (set.add(input[i]))
				queue.add(input[i]);
			checkElements();
			assertEquals("The size of the set has a wrong value", mps.size(), set.size());
		}
	}
	
	@Test
	public void testIntegrateAll() {
		String[][] input = {{"XXX", "GGG", "A"}, {"012", "AAA", "aaaa"}, {"aaa", new String("GGG")}};
		boolean[][] expected = {{false, false, false}, {false, true, false}, {true, true}};
		for (int i = 0; i < input.length; i++) {
			ArrayList<String> list = new ArrayList<String>(input[i].length);
			for (int j = 0; j < input[i].length; j++) {
				list.add(input[i][j]);
				if (set.add(input[i][j]))
					queue.add(input[i][j]);
			}
			mps.integrateAll(list);
			checkElements();
			assertEquals("The size of the set has a wrong value", mps.size(), set.size());
			for (int j = 0; j < input[i].length; j++)
				assertNotEquals("Element " + i + "." + j + " (" + input[i][j] + ") did not change the reference as expected", input[i][j] == mps.integrate(input[i][j]), expected[i][j]);
		}
	}
	
	@Test
	public void testRemove() {
		String[] input = {"XXX", "GGG", "A", "012", "AAA", "aaaa", "aaa", new String("GGG")};
		boolean[] expected = {false, false, false, false, true, false, true, false};
		for (int i = 0; i < input.length; i++) {
			assertEquals("Element " + i + " (" + input[i] + ") was not evaluated as expected", mps.remove(input[i]), expected[i]);
			set.remove(input[i]);
			queue.remove(input[i]);
			checkElements();
			assertEquals("The size of the set has a wrong value", mps.size(), set.size());
		}
	}

	@Test
	public void testRemoveAll() {
		String[][] input = {{"XXX", "GGG", "A"}, {"012", "AAA", "aaaa"}, {"aaa", "BBB"}};
		boolean[] expected = {false, true, true};
		for (int i = 0; i < input.length; i++) {
			ArrayList<String> list = new ArrayList<String>(input[i].length);
			for (int j = 0; j < input[i].length; j++)
				list.add(input[i][j]);
			assertEquals("Set " + i + " was not evaluated as expected", mps.removeAll(list), expected[i]);
			set.removeAll(list);
			queue.removeAll(list);
			checkElements();
			assertEquals("The size of the set has a wrong value", mps.size(), set.size());
		}
	}

	@Test
	public void testRetainAll() {
		String[][] input = {{"aaa", "AAA"}, {"012", new String("AAA"), new String("aaa")}, {"XXX", "GGG", "A"}};
		boolean[] expected = {true, false, true};
		for (int i = 0; i < input.length; i++) {
			ArrayList<String> list = new ArrayList<String>(input[i].length);
			for (int j = 0; j < input[i].length; j++)
				list.add(input[i][j]);
			assertEquals("Set " + i + " was not evaluated as expected", mps.retainAll(list), expected[i]);
			set.retainAll(list);
			queue.retainAll(list);
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
