package il.ac.technion.cs.ssdl.spartan.commandhandlers;

import il.ac.technion.cs.ssdl.spartan.refactoring.InlineSingleUseRefactoring;

/**
 * a BaseSpartanizationHandler configured to return the BaseRefactoring and
 * label relevant for Inline Single Use
 * 
 * @author Boris van Sosin <boris.van.sosin@gmail.com>
 * @author Yossi Gil <yossi.gil@gmail.com> (major refactoring 2013/07/11)
 * @since 2013/07/01
 */
public class InlineSingleUseHandler extends BaseSpartanizationHandler {
  /** Instantiates this class */
  public InlineSingleUseHandler() {
    super(new InlineSingleUseRefactoring());
  }
}