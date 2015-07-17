package org.spartan.refactoring.spartanizations;

import static org.eclipse.jdt.core.dom.ASTNode.PARENTHESIZED_EXPRESSION;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.CONDITIONAL_AND;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.CONDITIONAL_OR;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.EQUALS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.GREATER;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.GREATER_EQUALS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.LESS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.LESS_EQUALS;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.NOT_EQUALS;
import static org.eclipse.jdt.core.dom.PrefixExpression.Operator.NOT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.spartan.refacotring.utils.Funcs.asAndOrOr;
import static org.spartan.utils.Utils.hasNull;
import static org.spartan.utils.Utils.in;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.spartan.refacotring.utils.As;
import

org.spartan.refacotring.utils.Is;
import org.spartan.utils.Range;

/**
 * Simplifies a negated boolean expression using De-Morgan laws and laws of
 * arithmetics.
 *
 * @author Yossi Gil
 * @since 2014/06/15
 */
public class SimplifyLogicalNegation extends Spartanization {
  /** Instantiates this class */
  public SimplifyLogicalNegation() {
    super("Simplify logical negation", "Simplify logical negation");
  }
  @Override protected final void fillRewrite(final ASTRewrite r, final AST t, final CompilationUnit cu, final IMarker m) {
    cu.accept(new ASTVisitor() {
      @Override public boolean visit(final PrefixExpression e) {
        return !inRange(m, e) ? true : simplifyNot(As.not(e));
      }
      private boolean simplifyNot(final PrefixExpression e) {
        return e == null ? true : simplifyNot(e, getCore(e.getOperand()));
      }
      private boolean simplifyNot(final PrefixExpression e, final Expression inner) {
        return perhapsDoubleNegation(e, inner) //
            || perhapsDeMorgan(e, inner) //
            || perhapsComparison(e, inner) //
            || true;
      }
      boolean perhapsDoubleNegation(final Expression e, final Expression inner) {
        return perhapsDoubleNegation(e, As.not(inner));
      }
      boolean perhapsDoubleNegation(final Expression e, final PrefixExpression inner) {
        return inner != null && replace(e, inner.getOperand());
      }
      boolean perhapsDeMorgan(final Expression e, final Expression inner) {
        return perhapsDeMorgan(e, asAndOrOr(inner));
      }
      boolean perhapsDeMorgan(final Expression e, final InfixExpression inner) {
        return inner != null && deMorgan(e, inner, getCoreLeft(inner), getCoreRight(inner));
      }
      boolean deMorgan(final Expression e, final InfixExpression inner, final Expression left, final Expression right) {
        return deMorgan1(e, inner, parenthesize(left), parenthesize(right));
      }
      boolean deMorgan1(final Expression e, final InfixExpression inner, final Expression left, final Expression right) {
        return replace(e, //
            parenthesize( //
                addExtendedOperands(inner, //
                    makeInfixExpression(not(left), conjugate(inner.getOperator()), not(right)))));
      }
      InfixExpression addExtendedOperands(final InfixExpression from, final InfixExpression $) {
        if (from.hasExtendedOperands())
          addExtendedOperands(from.extendedOperands(), $.extendedOperands());
        return $;
      }
      void addExtendedOperands(final List<Expression> from, final List<Expression> to) {
        for (final Expression e : from)
          to.add(not(e));
      }
      boolean perhapsComparison(final Expression e, final Expression inner) {
        return perhapsComparison(e, asComparison(inner));
      }
      boolean perhapsComparison(final Expression e, final InfixExpression inner) {
        return inner != null && comparison(e, inner);
      }
      boolean comparison(final Expression e, final InfixExpression inner) {
        return replace(e, cloneInfixChangingOperator(inner, ShortestBranchFirst.negate(inner.getOperator())));
      }
      InfixExpression cloneInfixChangingOperator(final InfixExpression e, final Operator o) {
        return e == null ? null : makeInfixExpression(getCoreLeft(e), o, getCoreRight(e));
      }
      Expression parenthesize(final Expression e) {
        if (isSimple(e))
          return (Expression) ASTNode.copySubtree(t, e);
        final ParenthesizedExpression $ = t.newParenthesizedExpression();
        $.setExpression((Expression) ASTNode.copySubtree(t, getCore(e)));
        return $;
      }
      boolean isSimple(final Expression e) {
        return isSimple(e.getClass());
      }
      boolean isSimple(final Class<? extends Expression> c) {
        return in(c, BooleanLiteral.class, //
            CharacterLiteral.class, //
            NullLiteral.class, //
            NumberLiteral.class, //
            StringLiteral.class, //
            TypeLiteral.class, //
            Name.class, //
            QualifiedName.class, //
            SimpleName.class, //
            ParenthesizedExpression.class, //
            SuperMethodInvocation.class, //
            MethodInvocation.class, //
            ClassInstanceCreation.class, //
            SuperFieldAccess.class, //
            FieldAccess.class, //
            ThisExpression.class, //
            null);
      }
      private PrefixExpression not(final Expression e) {
        final PrefixExpression $ = t.newPrefixExpression();
        $.setOperator(NOT);
        $.setOperand(parenthesize(e));
        return $;
      }
      private InfixExpression makeInfixExpression(final Expression left, final Operator o, final Expression right) {
        final InfixExpression $ = t.newInfixExpression();
        $.setLeftOperand((Expression) ASTNode.copySubtree(t, left));
        $.setOperator(o);
        $.setRightOperand((Expression) ASTNode.copySubtree(t, right));
        return $;
      }
      private boolean replace(final ASTNode original, final ASTNode replacement) {
        if (!hasNull(original, replacement))
          r.replace(original, replacement, null);
        return true;
      }
    });
  }
  static Expression getCoreRight(final InfixExpression e) {
    return getCore(e.getRightOperand());
  }
  static Expression getCoreLeft(final InfixExpression e) {
    return getCore(e.getLeftOperand());
  }
  static Operator conjugate(final Operator o) {
    assert Is.deMorgan(o);
    return o.equals(CONDITIONAL_AND) ? CONDITIONAL_OR : CONDITIONAL_AND;
  }
  static Expression getCore(final Expression $) {
    return PARENTHESIZED_EXPRESSION != $.getNodeType() ? $ : getCore(((ParenthesizedExpression) $).getExpression());
  }
  static InfixExpression asComparison(final Expression e) {
    return !(e instanceof InfixExpression) ? null : asComparison((InfixExpression) e);
  }
  static InfixExpression asComparison(final InfixExpression e) {
    return in(e.getOperator(), //
        GREATER, //
        GREATER_EQUALS, //
        LESS, //
        LESS_EQUALS, //
        EQUALS, //
        NOT_EQUALS //
    ) ? e : null;
  }
  @Override protected ASTVisitor fillOpportunities(final List<Range> opportunities) {
    return new ASTVisitor() {
      @Override public boolean visit(final PrefixExpression e) {
        if (hasOpportunity(As.not(e)))
          opportunities.add(new Range(e));
        return true;
      }
      private boolean hasOpportunity(final PrefixExpression e) {
        return e == null ? false : hasOpportunity(getCore(e.getOperand()));
      }
      private boolean hasOpportunity(final Expression inner) {
        return As.not(inner) != null || asAndOrOr(inner) != null || asComparison(inner) != null;
      }
    };
  }

  /**
   * A static nested class hosting unit tests for the nesting class Unit test
   * for the containing class. Note our naming convention: a) test methods do
   * not use the redundant "test" prefix. b) test methods begin with the name of
   * the method they check.
   *
   * @author Yossi Gil
   * @since 2014-06-14
   */
  @FixMethodOrder(MethodSorters.NAME_ASCENDING) @SuppressWarnings({ "static-method", "javadoc" }) //
  public static class TEST {
    @Test public void asComparisonTypicalInfixIsNotNull() {
      final InfixExpression e = mock(InfixExpression.class);
      doReturn(GREATER).when(e).getOperator();
      assertNotNull(asComparison(e));
    }
    @Test public void asComparisonTypicalInfixIsCorrect() {
      final InfixExpression i = mock(InfixExpression.class);
      doReturn(GREATER).when(i).getOperator();
      assertEquals(i, asComparison(i));
    }
    @Test public void asComparisonTypicalExpression() {
      final InfixExpression i = mock(InfixExpression.class);
      doReturn(GREATER).when(i).getOperator();
      assertNotNull(asComparison(i));
    }
    @Test public void asComparisonPrefixlExpression() {
      final PrefixExpression p = mock(PrefixExpression.class);
      when(p.getOperator()).thenReturn(NOT);
      assertNull(asComparison(p));
    }
    @Test public void asComparisonTypicalInfixFalse() {
      final InfixExpression i = mock(InfixExpression.class);
      doReturn(CONDITIONAL_AND).when(i).getOperator();
      assertNull(asComparison(i));
    }
    @Test public void asComparisonTypicalExpressionFalse() {
      final InfixExpression i = mock(InfixExpression.class);
      doReturn(CONDITIONAL_OR).when(i).getOperator();
      assertNull(asComparison(i));
    }
    @Test public void isDeMorganAND() {
      assertTrue(Is.deMorgan(CONDITIONAL_AND));
    }
    @Test public void isDeMorganOR() {
      assertTrue(Is.deMorgan(CONDITIONAL_OR));
    }
    @Test public void isDeMorganGreater() {
      assertFalse(Is.deMorgan(GREATER));
    }
    @Test public void isDeMorganGreaterEuals() {
      assertFalse(Is.deMorgan(GREATER_EQUALS));
    }
    @Test public void inTypicalTrue() {
      assertTrue(in("A", "A", "B", "C"));
    }
    @Test public void inTypicalFalse() {
      assertFalse(in("X", "A", "B", "C"));
    }
  }
}