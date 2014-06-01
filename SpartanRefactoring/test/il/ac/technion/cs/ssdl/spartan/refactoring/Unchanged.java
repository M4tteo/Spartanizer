package il.ac.technion.cs.ssdl.spartan.refactoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Collection;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test cases in which the transformation should not do anything
 * 
 * @author Yossi Gil
 * @since 2014/05/24
 */
@RunWith(Parameterized.class)//
public class Unchanged extends AbstractParametrizedTest {
	/**
	 * An object describing the required transformation
	 */
	@Parameter(value = 0) public Spartanization spartanization;
	/**
	 * The name of the specific test for this transformation
	 */
	@Parameter(value = 1) public String name;
	/**
	 * Where the input text can be found
	 */
	@Parameter(value = 2) public File fIn;
	/**
	 * Runs a parameterized test case, based on the instance variables of this
	 * instance, and check that no opportunities are found.
	 */
	@Test public void checkNoOpportunities() {
		assertNotNull("Cannot instantiate Spartanization object", spartanization);
		final CompilationUnit cu = AbstractParametrizedTest.makeAST(fIn);
		assertEquals(0, spartanization.findOpportunities(cu).size());
	}
	/**
	 * Runs a parameterized test case, based on the instance variables of this
	 * instance, and check that no matter what, even if the number of
	 * opportunities is zero, the input does not change.
	 */
	@Test public void checkNoChange() {
		assertNotNull("Cannot instantiate Spartanization object", spartanization);
		final CompilationUnit cu = makeAST(fIn);
		assertEquals(readFile(fIn), rewrite(spartanization, cu, new Document(readFile(fIn))).get());
	}
	/**
	 * @return a collection of cases, where each cases is an array of three
	 *         objects, the spartanization, the test case name, and the input
	 *         file
	 */
	@Parameters(name = "{index}: {0} {1}")//
	public static Collection<Object[]> cases() {
		return new TestSuite.Files() {
			@Override Object[] makeCase(final Spartanization s, final File d, final File f, final String name) {
				if (!name.endsWith(".in"))
					return null;
				if (new File(d, name.replaceAll("\\.in$", ".out")).exists())
					return null;
				return new Object[] { s, name.replaceAll("\\.in$", ""), f };
			}
		}.go();
	}
}