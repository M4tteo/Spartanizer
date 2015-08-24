package org.spartan.refactoring.spartanizations;

import static org.spartan.refactoring.utils.Funcs.same;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.spartan.refactoring.utils.AncestorSearch;
import org.spartan.refactoring.utils.Is;
import org.spartan.refactoring.utils.Occurrences;
import org.spartan.utils.Range;

/**
 * @author Ofir Elmakias <code><Elmakias [at] outlook.com></code> (original)
 * @since 2015/08/12
 */
public class SafeRenameReturnVariableToDollar extends Spartanization {
  /** Instantiates this class */
  public SafeRenameReturnVariableToDollar() {
    super("Rename returned variable to '$'", "Rename the variable returned by a function to '$'");
  }
  static boolean declaredInMethod = false;
  @Override protected final void fillRewrite(final ASTRewrite r, final AST t, final CompilationUnit cu, final IMarker m) {
    cu.accept(new ASTVisitor() {
      @Override public boolean visit(final ReturnStatement rs) {
        final Expression n = rs.getExpression();
        if (!Is.simpleName(n) || n == null || !inRange(m, n))
          return true;
        ASTNode md = n;
        while (!Is.methodDeclaration(md))
          md = md.getParent();
        if (!declaredHere(n, md))
          return true;
        md.accept(new ASTVisitor() {
          @Override public boolean visit(final SimpleName sn) {
            if (same(sn, t.newSimpleName("$")))
              return true;
            if (same(sn, n))
              r.replace(sn, t.newSimpleName("$"), null);
            return true;
          }
        });
        return true;
      }
      private boolean declaredHere(final Expression n, final ASTNode md) {
        declaredInMethod = false;
        md.accept(new ASTVisitor() {
          @Override public boolean visit(final VariableDeclarationFragment v) {
            if (v.getName().toString().equals(n.toString()))
              declaredInMethod = true;
            return true;
          }
        });
        return declaredInMethod;
      }
    });
  }
  static List<VariableDeclarationFragment> getCandidates(final MethodDeclaration d) {
    if (d == null)
      return null;
    final List<VariableDeclarationFragment> $ = new ArrayList<>();
    d.accept(new ASTVisitor() {
      /**
       * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
       *      AnonymousClassDeclaration)
       * @param _ ignored, we don't want to visit declarations inside anonymous
       *          classes
       */
      @Override public boolean visit(@SuppressWarnings("unused") final AnonymousClassDeclaration _) {
        return false;
      }
      @Override public boolean visit(final VariableDeclarationStatement n) {
        $.addAll(n.fragments());
        return true;
      }
    });
    return $;
  }
  static List<ReturnStatement> getReturnStatements(final ASTNode container) {
    final List<ReturnStatement> $ = new ArrayList<>();
    container.accept(new ASTVisitor() {
      /**
       * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.
       *      AnonymousClassDeclaration)
       * @param _ ignored, we don't want to visit declarations inside anonymous
       *          classes
       */
      @Override public boolean visit(@SuppressWarnings("unused") final AnonymousClassDeclaration _) {
        return false;
      }
      @Override public boolean visit(final ReturnStatement node) {
        $.add(node);
        return true;
      }
    });
    return $;
  }
  static VariableDeclarationFragment selectReturnVariable(final MethodDeclaration m) {
    final List<VariableDeclarationFragment> vs = getCandidates(m);
    return vs == null || vs.isEmpty() || hasDollar(vs) ? null : selectReturnVariable(vs, prune(getReturnStatements(m)));
  }
  private static VariableDeclarationFragment selectReturnVariable(final List<VariableDeclarationFragment> vs, final List<ReturnStatement> rs) {
    return rs == null || rs.isEmpty() ? null : bestCandidate(vs, rs);
  }
  private static boolean hasDollar(final List<VariableDeclarationFragment> vs) {
    for (final VariableDeclaration v : vs)
      if (v.getName().getIdentifier().equals("$"))
        return true;
    return false;
  }
  private static List<ReturnStatement> prune(final List<ReturnStatement> $) {
    if ($ == null || $.isEmpty())
      return null;
    for (final Iterator<ReturnStatement> i = $.iterator(); i.hasNext();) {
      final ReturnStatement r = i.next();
      // Is enclosing method <code><b>void</b></code>?
      if (r.getExpression() == null)
        return null;
      if (Is.literal(r))
        i.remove();
    }
    return $;
  }
  private static VariableDeclarationFragment bestCandidate(final List<VariableDeclarationFragment> vs, final List<ReturnStatement> rs) {
    final int bestScore = bestScore(vs, rs);
    if (bestScore > 0)
      for (final VariableDeclarationFragment v : vs)
        if (bestScore == score(v, rs))
          return noRivals(v, vs, rs) ? v : null;
    return null;
  }
  private static boolean noRivals(final VariableDeclarationFragment candidate, final List<VariableDeclarationFragment> vs, final List<ReturnStatement> rs) {
    for (final VariableDeclarationFragment rival : vs)
      if (rival != candidate && score(rival, rs) >= score(candidate, rs))
        return false;
    return true;
  }
  private static int bestScore(final List<VariableDeclarationFragment> vs, final List<ReturnStatement> rs) {
    int $ = 0;
    for (final VariableDeclarationFragment v : vs)
      $ = Math.max($, score(v, rs));
    return $;
  }
  private static int score(final VariableDeclarationFragment v, final List<ReturnStatement> rs) {
    int $ = 0;
    for (final ReturnStatement r : rs)
      $ += Occurrences.BOTH_LEXICAL.of(v.getName()).in(r).size();
    return $;
  }
  @Override protected ASTVisitor collectOpportunities(final List<Range> $) {
    return new ASTVisitor() {
      @Override public boolean visit(final MethodDeclaration n) {
        final VariableDeclarationFragment v = selectReturnVariable(n);
        if (v != null)
          $.add(new Range(new AncestorSearch(ASTNode.METHOD_DECLARATION).of(v)));
        return true;
      }
    };
  }
}