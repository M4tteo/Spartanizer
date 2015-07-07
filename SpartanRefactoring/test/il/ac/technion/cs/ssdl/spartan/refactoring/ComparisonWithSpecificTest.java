package il.ac.technion.cs.ssdl.spartan.refactoring;

import static il.ac.technion.cs.ssdl.spartan.refactoring.ComparisonWithSpecific.applicable;
import static il.ac.technion.cs.ssdl.spartan.refactoring.ComparisonWithSpecific.isSpecific;
import static il.ac.technion.cs.ssdl.spartan.refactoring.ComparisonWithSpecific.withinDomain;
import static il.ac.technion.cs.ssdl.spartan.refactoring.TESTUtils.assertNotEvenSimilar;
import static il.ac.technion.cs.ssdl.spartan.refactoring.TESTUtils.assertSimilar;
import static org.eclipse.jdt.core.dom.ASTNode.CHARACTER_LITERAL;
import static org.eclipse.jdt.core.dom.ASTNode.NULL_LITERAL;
import static org.eclipse.jdt.core.dom.ASTNode.NUMBER_LITERAL;
import static org.eclipse.jdt.core.dom.ASTNode.THIS_EXPRESSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import il.ac.technion.cs.ssdl.spartan.utils.As;
import il.ac.technion.cs.ssdl.spartan.utils.Funcs;

/**
 * * Unit tests for the nesting class Unit test for the containing class. Note
 * our naming convention: a) test methods do not use the redundant "test"
 * prefix. b) test methods begin with the name of the method they check.
 *
 * @author Yossi Gil
 * @since 2014-07-10
 *
 */
// @RunWith(PowerMockRunner.class) //
// @PrepareForTest({ Expression.class, /* ASTNode.class */ }) //
// @PrepareForTest({ TEST.AFinalClass.class, TEST.Node.class, ASTNode.class,
// Expression.class, }) //
@FixMethodOrder(MethodSorters.JVM) //
@SuppressWarnings({ "static-method", "javadoc" }) //
public class ComparisonWithSpecificTest {
  private static final String P0 = "package p; public class Blah { public void y() {  if (null == a)  return;}}";
  private static final String P1 = "package p; public class Blah { public void y() {  if (a == null)  return;}}";

  @Test public void oneO() {
    TESTUtils.assertOneOpportunity(s(), P0);
  }

  @Test public void one1() {
    final CompilationUnit u = (CompilationUnit) As.COMPILIATION_UNIT.ast(P0);
    final Document rewrite = TESTUtils.rewrite(s(), u, new Document(P0));
    assertNotNull(rewrite);
  }

  @Test public void one2true() {
    final String expected = P1;
    final CompilationUnit u = (CompilationUnit) As.COMPILIATION_UNIT.ast(P0);
    final Document rewrite = TESTUtils.rewrite(s(), u, new Document(P0));
    assertSimilar(expected, rewrite);
  }

  @Test public void one2true0() {
    final CompilationUnit u = (CompilationUnit) As.COMPILIATION_UNIT.ast(P0);
    TESTUtils.rewrite(s(), u, new Document(P0));
    assertNotNull(u);
  }

  @Test public void one2true1() {
    final String expected = P1;
    final CompilationUnit u = (CompilationUnit) As.COMPILIATION_UNIT.ast(P0);
    final Document d = new Document(P0);
    assertNotNull(d);
    final Document rewrite = TESTUtils.rewrite(s(), u, d);
    assertSimilar(expected, rewrite);
  }

  @Test public void one2true2() {
    final Document d = new Document(P0);
    assertNotNull(d);
    assertSimilar(P0, d);
  }

  @Test public void one2true3() {
    final Document d = new Document(P0);
    assertNotNull(d);
    assertEquals(P0, d.get());
  }

  @Test public void one2true4() throws MalformedTreeException, IllegalArgumentException, BadLocationException {
    final String expected = P1;
    final CompilationUnit u = (CompilationUnit) As.COMPILIATION_UNIT.ast(P0);
    final Document d = new Document(P0);
    assertNotNull(d);
    final ComparisonWithSpecific s = s();
    final Document rewrite;
    s.createRewrite(u, null).rewriteAST(d, null).apply(d);
    rewrite = d;
    assertSimilar(expected, rewrite);
  }

  @Test public void one2true5() {
    final String expected = P1;
    final CompilationUnit u = (CompilationUnit) As.COMPILIATION_UNIT.ast(P0);
    final Document d = new Document(P0);
    assertNotNull(d);
    final ComparisonWithSpecific s = s();
    final Document rewrite = TESTUtils.rewrite(s, u, d);
    assertSimilar(expected, rewrite);
  }

  @Test public void one2true6() throws MalformedTreeException, IllegalArgumentException, BadLocationException {
    final String expected = P1;
    final CompilationUnit u = (CompilationUnit) As.COMPILIATION_UNIT.ast(P0);
    final Document d = new Document(P0);
    assertNotNull(d);
    final ComparisonWithSpecific s = s();
    final Document rewrite;
    final ASTRewrite r = s.createRewrite(u, null);
    r.rewriteAST(d, null).apply(d);
    rewrite = d;
    assertSimilar(expected, rewrite);
  }

  @Test public void one2true7() throws MalformedTreeException, IllegalArgumentException, BadLocationException {
    final String expected = P1;
    final CompilationUnit u = (CompilationUnit) As.COMPILIATION_UNIT.ast(P0);
    final Document d = new Document(P0);
    assertNotNull(d);
    final ComparisonWithSpecific s = s();
    final ASTRewrite r = s.createRewrite(u, null);
    r.rewriteAST(d, null).apply(d);
    final Document rewrite;
    rewrite = d;
    assertSimilar(expected, rewrite);
  }

  private ComparisonWithSpecific s() {
    return new ComparisonWithSpecific();
  }

  @Test public void one2false() {
    final String expected = P0;
    final CompilationUnit u = (CompilationUnit) As.COMPILIATION_UNIT.ast(P0);
    final Document rewrite = TESTUtils.rewrite(s(), u, new Document(P0));
    assertNotNull(rewrite);
    assertNotEvenSimilar(expected, rewrite.get());
  }

  @Test public void canMakeExpression() {
    e("2+2");
  }

  @Test public void callIsSpecific() {
    isSpecific(e("2+2"));
  }

  @Test public void callIsSpecificFalse() {
    assertFalse(isSpecific(e("2+2")));
  }

  @Test public void getNodeType() {
    assertEquals(ASTNode.THIS_EXPRESSION, e("this").getNodeType());
  }

  @Test public void isOneOf() {
    assertTrue(Funcs.isOneOf(e("this"), CHARACTER_LITERAL, NUMBER_LITERAL, NULL_LITERAL, THIS_EXPRESSION));
  }

  @Test public void callIsSpecificTrue() {
    assertTrue(isSpecific(e("this")));
  }

  @Test public void cisSpecificFalse1() {
    assertFalse(isSpecific(e("a")));
  }

  @Test public void breakExpression() {
    final InfixExpression i = i("a == this");
    assertEquals(ASTNode.INFIX_EXPRESSION, i.getNodeType());
  }

  @Test public void applicableCompareWithThis() {
    assertFalse(applicable(i("a == this")));
  }

  @Test public void applicableThisCompareWithThis() {
    assertTrue(applicable(i("this ==this")));
  }

  @Test public void withDomainTrue1() {
    assertTrue(withinDomain(i("a == this")));
  }

  @Test public void withDomainTrue2() {
    assertTrue(withinDomain(i("this == null")));
  }

  @Test public void withDomainTrue3() {
    assertTrue(withinDomain(i("12 == this")));
  }

  @Test public void withDomainTrue4() {
    assertTrue(withinDomain(i("a == 11")));
  }

  private InfixExpression i(final String s) {
    return (InfixExpression) e(s);
  }

  private static Expression e(final String s2) {
    return (Expression) As.EXPRESSION.ast(s2);
  }

  @Test public void withDomainFalse2() {
    assertFalse(withinDomain(i("13455643294 < 22")));
  }

  @Test public void withDomainFalse3() {
    assertFalse(withinDomain(i("1 < 102333")));
  }
}