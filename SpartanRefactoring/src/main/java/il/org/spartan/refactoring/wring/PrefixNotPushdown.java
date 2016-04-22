package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.Extract.core;
import static il.org.spartan.refactoring.utils.Funcs.asAndOrOr;
import static il.org.spartan.refactoring.utils.Funcs.asBooleanLiteral;
import static il.org.spartan.refactoring.utils.Funcs.asComparison;
import static il.org.spartan.refactoring.utils.Funcs.asNot;
import static il.org.spartan.refactoring.utils.Funcs.duplicate;
import static il.org.spartan.refactoring.utils.Funcs.left;
import static il.org.spartan.refactoring.utils.Funcs.logicalNot;
import static il.org.spartan.refactoring.utils.Funcs.right;
import static il.org.spartan.refactoring.utils.Restructure.flatten;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.CONDITIONAL_AND;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.CONDITIONAL_OR;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.GREATER;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.GREATER_EQUALS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.LESS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.LESS_EQUALS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.NOT_EQUALS;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.PrefixExpression;

import il.org.spartan.refactoring.preferences.PluginPreferencesResources.WringGroup;
import il.org.spartan.refactoring.utils.Extract;
import il.org.spartan.refactoring.utils.Is;
import il.org.spartan.refactoring.utils.Subject;

/**
 * A {@link Wring} that pushes down "<code>!</code>", the negation operator as
 * much as possible, using the de-Morgan and other simplification rules.
 *
 * @author Yossi Gil
 * @since 2015-7-17
 */
public final class PrefixNotPushdown extends Wring.ReplaceCurrentNode<PrefixExpression> {
  /**
   * @param o JD
   * @return the operator that produces the logical negation of the parameter
   */
  public static Operator conjugate(final Operator o) {
    return o == null ? null
        : o.equals(CONDITIONAL_AND) ? CONDITIONAL_OR //
            : o.equals(CONDITIONAL_OR) ? CONDITIONAL_AND //
                : o.equals(EQUALS) ? NOT_EQUALS
                    : o.equals(NOT_EQUALS) ? EQUALS
                        : o.equals(LESS_EQUALS) ? GREATER
                            : o.equals(GREATER) ? LESS_EQUALS //
                                : o.equals(GREATER_EQUALS) ? LESS //
                                    : o.equals(LESS) ? GREATER_EQUALS : null;
  }
  /**
   * A utility function, which tries to simplify a boolean expression, whose top
   * most parameter is logical negation.
   *
   * @param e JD
   * @return the simplified parameter
   */
  public static Expression simplifyNot(final PrefixExpression e) {
    return pushdownNot(asNot(Extract.core(e)));
  }
  private static Expression applyDeMorgan(final InfixExpression inner) {
    final List<Expression> operands = new ArrayList<>();
    for (final Expression e : Extract.operands(flatten(inner)))
      operands.add(logicalNot(e));
    return Subject.operands(operands).to(conjugate(inner.getOperator()));
  }
  private static Expression comparison(final InfixExpression e) {
    return Subject.pair(left(e), right(e)).to(conjugate(e.getOperator()));
  }
  private static boolean hasOpportunity(final Expression inner) {
    return Is.booleanLiteral(inner) || asNot(inner) != null || asAndOrOr(inner) != null || asComparison(inner) != null;
  }
  private static boolean hasOpportunity(final PrefixExpression e) {
    return e != null && hasOpportunity(core(e.getOperand()));
  }
  static Expression notOfLiteral(final BooleanLiteral l) {
    final BooleanLiteral $ = duplicate(l);
    $.setBooleanValue(!l.booleanValue());
    return $;
  }
  private static Expression perhapsComparison(final Expression inner) {
    return perhapsComparison(asComparison(inner));
  }
  private static Expression perhapsComparison(final InfixExpression inner) {
    return inner == null ? null : comparison(inner);
  }
  private static Expression perhapsDeMorgan(final Expression e) {
    return perhapsDeMorgan(asAndOrOr(e));
  }
  private static Expression perhapsDeMorgan(final InfixExpression e) {
    return e == null ? null : applyDeMorgan(e);
  }
  private static Expression perhapsDoubleNegation(final Expression e) {
    return perhapsDoubleNegation(asNot(e));
  }
  private static Expression perhapsDoubleNegation(final PrefixExpression e) {
    return e == null ? null : tryToSimplify(core(e.getOperand()));
  }
  static Expression perhapsNotOfLiteral(final Expression e) {
    return !Is.booleanLiteral(e) ? null : notOfLiteral(asBooleanLiteral(e));
  }
  static Expression pushdownNot(final Expression e) {
    Expression $;
    return ($ = perhapsNotOfLiteral(e)) != null//
        || ($ = perhapsDoubleNegation(e)) != null//
        || ($ = perhapsDeMorgan(e)) != null//
        || ($ = perhapsComparison(e)) != null //
            ? $ : null;
  }
  private static Expression pushdownNot(final PrefixExpression e) {
    return e == null ? null : pushdownNot(core(e.getOperand()));
  }
  private static Expression tryToSimplify(final Expression e) {
    final Expression $ = pushdownNot(asNot(e));
    return $ != null ? $ : e;
  }
  @Override public boolean scopeIncludes(final PrefixExpression e) {
    return e != null && asNot(e) != null && hasOpportunity(asNot(e));
  }
  @Override Expression replacement(final PrefixExpression e) {
    return simplifyNot(e);
  }
  @Override String description(@SuppressWarnings("unused") final PrefixExpression _) {
    return "Pushdown logical negation ('!')";
  }
  @Override WringGroup wringGroup() {
    return WringGroup.REORDER_EXPRESSIONS;
  }
}
