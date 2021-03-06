package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.ast.extract.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.refactoring.assemble.*;
import il.org.spartan.refactoring.ast.*;

/** Removes unnecessary uses of Boolean.valueOf, e.g.,
 * <code>Boolean.valueOf(true) </code> into <code>Boolean.TRUE</code>
 * @author Daniel Mittelman <code><mittelmania [at] gmail.com></code>
 * @since 2016-04-04 */
public final class BooleanConstants extends Wring.ReplaceCurrentNode<MethodInvocation> implements Kind.Canonicalization {
  private static String asString(final BooleanLiteral l) {
    return (l.booleanValue() ? "TRU" : "FALS") + "E";
  }

  private static Expression replacement(final Expression x, final BooleanLiteral l) {
    return l == null ? null : subject.operand(x).toQualifier(asString(l));
  }

  private static Expression replacement(final Expression x, final Expression $) {
    return x == null || !"Boolean".equals("" + x) ? null : replacement(x, az.booleanLiteral($));
  }

  @Override String description(final MethodInvocation i) {
    return "Replace valueOf (" + onlyArgument(i) + ") with Boolean." + asString(az.booleanLiteral(onlyArgument(i)));
  }

  @Override Expression replacement(final MethodInvocation i) {
    return !"valueOf".equals(step.name(i).getIdentifier()) ? null : replacement(step.receiver(i), onlyArgument(i));
  }
}
