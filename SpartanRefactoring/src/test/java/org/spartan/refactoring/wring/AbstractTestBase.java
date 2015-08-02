package org.spartan.refactoring.wring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.spartan.refactoring.spartanizations.TESTUtils.peelExpression;
import static org.spartan.refactoring.spartanizations.TESTUtils.peelStatement;
import static org.spartan.refactoring.spartanizations.TESTUtils.wrapExpression;
import static org.spartan.refactoring.spartanizations.TESTUtils.wrapStatement;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.internal.builders.IgnoredClassRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameter;

@SuppressWarnings({ "javadoc", "restriction" }) //
@RunWith(IgnoredClassRunner.class) //
public abstract class AbstractTestBase {
  /** The name of the specific test for this transformation */
  @Parameter(0) public String name;
  /** Where the input text can be found */
  @Parameter(1) public String input;
  @Test public void inputNotNull() {
    assertNotNull(input);
  }
  @Test public void peelableinput() {
    assertEquals(input, peelExpression(wrapExpression(input)));
    assertEquals(input, peelStatement(wrapStatement(input)));
  }
  protected static Collection<Object[]> collect(final String[][] cases) {
    final Collection<Object[]> $ = new ArrayList<>(cases.length);
    for (final String[] t : cases) {
      if (t == null)
        break;
      $.add(t);
    }
    return $;
  }
}