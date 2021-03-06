package il.org.spartan.refactoring.assemble;

import static il.org.spartan.refactoring.ast.extract.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.*;

import il.org.spartan.refactoring.ast.*;

public enum flatten {
  ;
  /** Flatten the list of arguments to an {@link InfixExpression}, e.g., convert
   * an expression such as <code>(a + b) + c</code> whose inner form is roughly
   * "+(+(a,b),c)", into <code>a + b + c</code>, whose inner form is (roughly)
   * "+(a,b,c)".
   * @param $ JD
   * @return a duplicate of the argument, with the a flattened list of
   *         operands. */
  public static InfixExpression of(final InfixExpression $) {
    assert $ != null;
    final Operator o = $.getOperator();
    assert o != null;
    return subject.operands(flatten.into(o, hop.operands($), new ArrayList<Expression>())).to(duplicate.of($).getOperator());
  }

  private static List<Expression> add(final Expression x, final List<Expression> $) {
    $.add(x);
    return $;
  }

  private static List<Expression> into(final Operator o, final Expression x, final List<Expression> $) {
    final Expression core = core(x);
    final InfixExpression inner = az.infixExpression(core);
    return inner == null || inner.getOperator() != o ? add(!iz.noParenthesisRequired(core) ? x : core, $)
        : flatten.into(o, duplicate.adjust(o, hop.operands(inner)), $);
  }

  private static List<Expression> into(final Operator o, final List<Expression> xs, final List<Expression> $) {
    for (final Expression e : xs)
      into(o, e, $);
    return $;
  }
}
