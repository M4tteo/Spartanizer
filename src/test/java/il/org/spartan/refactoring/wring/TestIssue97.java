package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.wring.TrimmerTestsUtils.*;

import org.junit.*;

/** @author Stav Namir
 * @since 2016-08-29 */
@SuppressWarnings("static-method") @Ignore("Issue #97 : under construction") public class TestIssue97 {
  @Test public void test01() {
    trimming(
        "\"Spartanizing '\" + javaProject.getElementName() + \"' project \\n\" + \n\"Completed in \" + (1 + i) + \" passes. \\n\" + \n\"Total changes: \" + (initialCount - finalCount) + \"\\n\" + \n\"Suggestions before: \" + initialCount + \"\\n\" + \n\"Suggestions after: \" + finalCount + \"\\n\" + \nmessage")
            .to("\"Spartanizing '\"+javaProject.getElementName()+\"' project \n\"+\"Completed in \"+(1+i)+\" passes. \n\"+\"Total changes:\"+(initialCount-finalCount)+\"\n\"+\"Suggestions before:\"+initialCount+\"\n\"+\"Suggestions after:\"+finalCount+\"\n\"+message");
    /* supposed to be: "Spartanizing '" + javaProject.getElementName() +
     * "' project \n" + // "Completed in " + (1 + i) + " passes. \n" + //
     * "Total changes: " + (initialCount - finalCount) + "\n" + //
     * "Suggestions before: " + initialCount + "\n" + // "Suggestions after: " +
     * finalCount + "\n" + // message */
  }
}
