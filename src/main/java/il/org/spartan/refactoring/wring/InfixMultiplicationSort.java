package il.org.spartan.refactoring.wring;

import static il.org.spartan.Utils.*;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.*;

import il.org.spartan.refactoring.engine.*;

/** sorts the arguments of a {@link Operator#PLUS} expression. Extra care is
 * taken to leave intact the use of {@link Operator#PLUS} for the concatenation
 * of {@link String}s.
 * @author Yossi Gil
 * @since 2015-07-17 */
public final class InfixMultiplicationSort extends Wring.InfixSorting implements Kind.Sorting {
  @Override boolean scopeIncludes(final InfixExpression x) {
    return in(x.getOperator(), TIMES);
  }

  @Override boolean sort(final List<Expression> xs) {
    return ExpressionComparator.MULTIPLICATION.sort(xs);
  }
}
