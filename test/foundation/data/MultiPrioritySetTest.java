package foundation.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import foundation.data.MultiPrioritySet;

/**
 * <p>Tests the {@link MultiPrioritySet MultiPrioritySet} in various settings.</p>
 * @author Julian Betz
 * @version 1.00
 */
@RunWith(Suite.class)
@SuiteClasses({ MultiPrioritySetTestEmpty.class, MultiPrioritySetTestFilled.class,
	MultiPrioritySetTestFilledNoComparison.class })
public class MultiPrioritySetTest {
	
}
