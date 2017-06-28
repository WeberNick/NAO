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
import java.util.NoSuchElementException;

/**
 * <p>Tests the {@link MultiPrioritySet MultiPrioritySet} starting with a filled set. In this setting the content of the set is not
 * compared to another set.</p>
 * @author Julian Betz
 * @version 1.00
 */
public class MultiPrioritySetTestFilledNoComparison {
	private static MultiPrioritySet<String> mps;
	private static String[] contained = {"AAA", "BBB", "AAAA", "aaa", "AAA", "AA"};
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ArrayList<Comparator<String>> comparators = new ArrayList<Comparator<String>>(1);
		comparators.add((String a, String b) -> a.length() - b.length());
		mps = new MultiPrioritySet<String>(comparators);
	}
	
	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < contained.length; i++)
			mps.add(new String(contained[i]));
	}
	
	@After
	public void tearDown() throws Exception {
		mps.clear();
	}

	@Test(expected = NoSuchElementException.class)
	public void testClear() {
		mps.clear();
		Iterator<String> it = mps.iterator();
		assertFalse("The set does still contain elements", it.hasNext());
		assertEquals("The size of the set has a wrong value", mps.size(), 0);
		it.next();
	}
	
	@Test
	public void testContains() {
		String[] input = {"XXX", "GGG", "A", "012", "AAA", "aaaa", "aaa", "GGG"};
		boolean[] expected = {false, false, false, false, true, false, true, false};
		for (int i = 0; i < expected.length; i++)
			assertEquals("Element " + i + " was not evaluated as expected", mps.contains(input[i]), expected[i]);
	}
	
	@Test
	public void testContainsAll() {
		String[][] input = {{"XXX", "GGG", "A"}, {"012", "AAA", "aaaa"}, {"aaa", "AAA"}};
		boolean[] expected = {false, false, true};
		for (int i = 0; i < input.length; i++) {
			ArrayList<String> list = new ArrayList<String>(input[i].length);
			for (int j = 0; j < input[i].length; j++)
				list.add(input[i][j]);
			assertEquals("Set " + i + " was not evaluated as expected", mps.containsAll(list), expected[i]);
		}
	}
	
	@Test
	public void testToArray() {
		Object[] array = mps.toArray();
		Iterator<String> it = mps.iterator();
		for (int i = 0; it.hasNext(); i++)
			assertEquals("Element " + i + " (" + array[i] + ") is not the element contained in the set", array[i], it.next());
		String[] actual = mps.toArray(new String[0]);
		it = mps.iterator();
		for (int i = 0; it.hasNext(); i++)
			assertEquals("Element " + i + " (" + actual[i] + ") is not the element contained in the set", actual[i], it.next());
		actual = mps.toArray(new String[mps.size()]);
		it = mps.iterator();
		for (int i = 0; it.hasNext(); i++)
			assertEquals("Element " + i + " (" + actual[i] + ") is not the element contained in the set", actual[i], it.next());
		actual = mps.toArray(new String[mps.size() + 1]);
		it = mps.iterator();
		for (int i = 0; actual[i] != null; i++)
			assertEquals("Element " + i + " (" + actual[i] + ") is not the element contained in the set", actual[i], it.next());
	}
}
