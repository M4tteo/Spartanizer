package il.org.spartan.refactoring.wring;

import static il.org.spartan.Utils.*;
import static il.org.spartan.refactoring.ast.step.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import il.org.spartan.refactoring.ast.*;
import il.org.spartan.refactoring.engine.*;

/** convert
 *
 * <pre>
 * if (a) {
 *   return x;
 * }
 * </pre>
 *
 * into
 *
 * <pre>
 * return x;
 * </pre>
 *
 * provided that this
 *
 * <pre>
 * <b>if</b>
 * </pre>
 *
 * statement is the last statement in a method.
 * @author Yossi Gil
 * @author Daniel Mittelman
 *
 *         <pre>
 * <mittelmania [at] gmail.com>
 *         </pre>
 *
 * @since 2015-09-09 */
public final class IfLastInMethodThenEndingWithEmptyReturn extends Wring<IfStatement> implements Kind.Canonicalization {
  @Override String description(@SuppressWarnings("unused") final IfStatement __) {
    return "Remove redundant return statement in 'then' branch of if statement that terminates this method";
  }

  @Override Rewrite make(final IfStatement s, final ExclusionManager exclude) {
    final Block b = az.block(s.getParent());
    if (b == null || !(b.getParent() instanceof MethodDeclaration) || !lastIn(s, statements(b)))
      return null;
    final ReturnStatement deleteMe = az.returnStatement(hop.lastStatement(step.then(s)));
    if (deleteMe == null || deleteMe.getExpression() != null)
      return null;
    if (exclude != null)
      exclude.equals(s);
    return new Rewrite(description(s), s) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        r.replace(deleteMe, s.getAST().newEmptyStatement(), g);
      }
    };
  }
}
