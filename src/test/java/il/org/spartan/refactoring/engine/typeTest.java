package il.org.spartan.refactoring.engine;

import static il.org.spartan.azzert.*;
import static il.org.spartan.refactoring.engine.type.*;
import static il.org.spartan.refactoring.engine.type.Odd.Types.*;
import static il.org.spartan.refactoring.engine.type.Primitive.Certain.*;
import static il.org.spartan.refactoring.engine.type.Primitive.Uncertain.*;

import org.junit.*;
import org.junit.runners.*;

import il.org.spartan.*;
import il.org.spartan.refactoring.engine.type.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //
@SuppressWarnings({ "javadoc", "static-method", "unused" }) //
public class typeTest {
  @Ignore public static class Pending {
    // class for Pending s that don't currently pass
  }

  // @Ignore ("type not implemented yet")
  public static class Working {
    private static final int _33 = 3;
    private static final int _32 = 3;
    private static final long _2L = 2l;
    private static final int _15 = 1;
    private static final int _14 = 1;
    private static final int _13 = 1;
    private static final int _12 = 1;
    private static final long _1L = 1L;
    private static final int _1 = 1;
    private static final int _3 = 3;
    /** Make sure the compiler cannot optimize this out */
    private final byte b = (byte) (hashCode() ^ 0xDEADdeaf);
    private final boolean b1 = b > (byte) hashCode();
    private final boolean b2 = (b & (byte) hashCode() << 3) < 0;
    private final char c1 = (char) (hashCode() ^ hashCode() << 7);
    private final char c2 = (char) (c1 << 13 ^ hashCode());
    private double d = c1 / c2;
    private int i = (int) d;
    private float f = (float) (0xCABAC0DAABBAL * d * i / b - (c1 ^ c2));
    private long l = (long) (++d * f--);
    private short s = (short) ((i ^ l) * (1L * c1 ^ c2 << 0xFF) / d);

    // basic tests for assignments
    @Test public void assingment1() {
      azzert.that(prudent(into.e("x = 2")), is(NUMERIC));
    }

    @Test public void assingment2() {
      azzert.that(prudent(into.e("x = \"a string\"")), is(STRING));
    }

    @Test public void assingment3() {
      azzert.that(prudent(into.e("x = true")), is(BOOLEAN));
    }

    @Test public void assingment4() {
      azzert.that(prudent(into.e("x = new Float()")), is(NUMERIC));
    }

    @Test public void assingment5() {
      azzert.that(prudent(into.e("x = new String()")), is(STRING));
    }

    @Test public void assingment6() {
      azzert.that(prudent(into.e("x = new Object()")), is(baptize("Object")));
    }

    @Test public void axiomAssignment1() {
      int x;
      azzert.that(Axiom.type(x = 2), is(INT));
    }

    @Test public void axiomAssignment2() {
      byte x;
      azzert.that(Axiom.type(x = 2), is(BYTE));
    }

    @Test public void axiomAssignment3() {
      char x;
      azzert.that(Axiom.type(x = 2), is(CHAR));
    }

    @Test public void axiomAssignment4() {
      short x;
      azzert.that(Axiom.type(x = 2), is(SHORT));
    }

    @Test public void axiomAssignment5() {
      long x;
      azzert.that(Axiom.type(x = 2), is(LONG));
    }

    @Test public void axiomAssignment6() {
      double x;
      azzert.that(Axiom.type(x = 2), is(DOUBLE));
    }

    @Test public void axiomAssignment7() {
      String x;
      azzert.that(Axiom.type(x = "abs"), is(STRING));
    }

    @Test public void axiomAssignment8() {
      double x;
      int y;
      azzert.that(Axiom.type(x = y = 2), is(DOUBLE));
    }

    @Test public void axiomAssignment9() {
      double x;
      int y;
      azzert.that(Axiom.type(y = (int) (x = 2)), is(INT));
    }

    @Test public void axiomBoolean1() {
      azzert.that(Axiom.type(true), is(BOOLEAN));
    }

    @Test public void axiomBoolean2() {
      azzert.that(Axiom.type(true || b1 && b2), is(BOOLEAN));
    }

    @Test public void axiomBoolean3() {
      azzert.that(Axiom.type(5 > 6 && 8 != 14), is(BOOLEAN));
    }

    @Test public void axiomByte() {
      azzert.that(Axiom.type((byte) 1), is(BYTE));
    }

    // s for the axiom methods
    @Test public void axiomChar1() {
      azzert.that(Axiom.type('a'), is(CHAR));
    }

    @Test public void axiomDouble() {
      azzert.that(Axiom.type(7.), is(DOUBLE));
    }

    @Test public void axiomExpression1() {
      azzert.that(Axiom.type(_3 / 2. + 7), is(DOUBLE));
    }

    @Test public void axiomExpression10() {
      azzert.that(Axiom.type(9f / 9), is(FLOAT));
    }

    @Test public void axiomExpression11() {
      azzert.that(Axiom.type((float) 1 / (int) 1L), is(FLOAT));
    }

    @Test public void axiomExpression12() {
      azzert.that(Axiom.type(_1 * (float) 1 / 1L), is(FLOAT));
    }

    @Test public void axiomExpression13() {
      azzert.that(Axiom.type((float) 1 / (short) 1), is(FLOAT));
    }

    @Test public void axiomExpression14() {
      azzert.that(Axiom.type(_14 * _1L + (float) _15), is(FLOAT));
    }

    @Test public void axiomExpression15() {
      azzert.that(Axiom.type((float) _12 + (char) _13), is(FLOAT));
    }

    @Test public void axiomExpression2() {
      azzert.that(Axiom.type(3 / _2L + 7), is(LONG));
    }

    @Test public void axiomExpression3() {
      azzert.that(Axiom.type(67 >> 2l), is(INT));
    }

    @Test public void axiomExpression4() {
      azzert.that(Axiom.type(67L >> 2l), is(LONG));
    }

    @Test public void axiomExpression5() {
      azzert.that(Axiom.type(2 * 3 / 4 + 1. - 5), is(DOUBLE));
    }

    @Test public void axiomExpression6() {
      azzert.that(Axiom.type((2 * _32 / 4 + 1. - 5) % 4), is(DOUBLE));
    }

    @Test public void axiomExpression7() {
      azzert.that(Axiom.type((2 * _33 / 4 + 1 - 5) % 4), is(INT));
    }

    @Test public void axiomExpression8() {
      azzert.that(Axiom.type((2 * 3 / 4 + 1L - 5) % 4), is(LONG));
    }

    @Test public void axiomExpression9() {
      azzert.that(Axiom.type(-1.0 / 2 * 3 / 4 * 5 * 6 / 7 / 8 / 9), is(DOUBLE));
    }

    @Test public void axiomFloat() {
      azzert.that(Axiom.type(7f), is(FLOAT));
    }

    @Test public void axiomInt1() {
      azzert.that(Axiom.type(7), is(INT));
    }

    @Test public void axiomInt2() {
      azzert.that(Axiom.type(4 + 'a'), is(INT));
    }

    @Test public void axiomLong() {
      azzert.that(Axiom.type(7l), is(LONG));
    }

    @Test public void axiomShort() {
      azzert.that(Axiom.type((short) 3), is(SHORT));
    }

    @Test public void axiomString1() {
      azzert.that(Axiom.type("string"), is(STRING));
    }

    @Test public void axiomString2() {
      azzert.that(Axiom.type("string" + 9.0), is(STRING));
    }

    @Test public void axiomString3() {
      azzert.that(Axiom.type("string" + 'd'), is(STRING));
    }

    @Test public void axiomString4() {
      azzert.that(Axiom.type(Integer.toString(15)), is(STRING));
    }

    // basic tests for pre/in/postfix expression
    @Test public void basicExpressions01() {
      azzert.that(prudent(into.e("2 + (2.0)*1L")), is(DOUBLE));
    }

    @Test public void basicExpressions02() {
      azzert.that(prudent(into.e("(int)(2 + (2.0)*1L)")), is(INT));
    }

    @Test public void basicExpressions03() {
      azzert.that(prudent(into.e("(int)(2 + (2.0)*1L)==9.0")), is(BOOLEAN));
    }

    @Test public void basicExpressions04() {
      azzert.that(prudent(into.e("9*3.0-f()")), is(DOUBLE));
    }

    @Test public void basicExpressions05() {
      azzert.that(prudent(into.e("g()+f()")), is(ALPHANUMERIC));
    }

    @Test public void basicExpressions06() {
      azzert.that(prudent(into.e("f(g()+h(),f(2))")), is(NOTHING));
    }

    @Test public void basicExpressions07() {
      azzert.that(prudent(into.e("f()+null")), is(STRING));
    }

    @Test public void basicExpressions08() {
      azzert.that(prudent(into.e("2+f()")), is(ALPHANUMERIC));
    }

    @Test public void basicExpressions09() {
      azzert.that(prudent(into.e("2%f()")), is(INTEGRAL));
    }

    @Test public void basicExpressions10() {
      azzert.that(prudent(into.e("2<<f()")), is(INT));
    }

    @Test public void basicExpressions11() {
      azzert.that(prudent(into.e("f()<<2")), is(INTEGRAL));
    }

    @Test public void basicExpressions12() {
      azzert.that(prudent(into.e("f()||g()")), is(BOOLEAN));
    }

    @Test public void basicExpressions13() {
      azzert.that(prudent(into.e("x++")), is(NUMERIC));
    }

    @Test public void basicExpressions18() {
      azzert.that(prudent(into.e("((short)1)+((short)2)")), is(INT));
    }

    @Test public void basicExpressions19() {
      azzert.that(prudent(into.e("((byte)1)+((byte)2)")), is(INT));
    }

    @Test public void basicExpressions20() {
      azzert.that(prudent(into.e("1f + 1")), is(FLOAT));
    }

    @Test public void basicExpressions21() {
      azzert.that(prudent(into.e("1f + 1l")), is(FLOAT));
    }

    @Test public void basicExpressions22() {
      azzert.that(prudent(into.e("1F + 'a'")), is(FLOAT));
    }

    @Test public void basicExpressions23() {
      azzert.that(prudent(into.e("1f + 1.")), is(DOUBLE));
    }

    @Test public void basicExpressions24() {
      azzert.that(prudent(into.e("1f / f()")), is(NUMERIC));
    }

    @Test public void basicExpressions25() {
      azzert.that(prudent(into.e("1+2+3l")), is(LONG));
    }

    @Test public void basicExpressions26() {
      azzert.that(prudent(into.e("1+2f+3l-5-4d")), is(DOUBLE));
    }

    @Test public void basicExpressions27() {
      azzert.that(prudent(into.e("1+2f+3l+f()-5-4d")), is(DOUBLE));
    }

    @Test public void basicExpressions28() {
      azzert.that(prudent(into.e("1+2f+3l+f()")), is(ALPHANUMERIC));
    }

    @Test public void BitwiseOperationsSemantics01() {
      azzert.that(Axiom.type(c1 | c2), is(INT));
    }

    @Test public void BitwiseOperationsSemantics02() {
      azzert.that(Axiom.type(b & c1), is(INT));
    }

    @Test public void BitwiseOperationsSemantics03() {
      azzert.that(Axiom.type(b1 | b2), is(BOOLEAN));
    }

    @Test public void BitwiseOperationsSemantics04() {
      azzert.that(Axiom.type(b1 ^ b2), is(BOOLEAN));
    }

    // s for casting expression
    @Test public void cast01() {
      azzert.that(prudent(into.e("(List)f()")), is(baptize("List")));
    }

    @Test public void cast02() {
      azzert.that(prudent(into.e("(char)x")), is(CHAR));
    }

    @Test public void cast03() {
      azzert.that(prudent(into.e("(Character)x")), is(CHAR));
    }

    @Test public void cast04() {
      azzert.that(prudent(into.e("(int)x")), is(INT));
    }

    @Test public void cast05() {
      azzert.that(prudent(into.e("(Integer)x")), is(INT));
    }

    @Test public void cast06() {
      azzert.that(prudent(into.e("(long)x")), is(LONG));
    }

    @Test public void cast07() {
      azzert.that(prudent(into.e("(Long)x")), is(LONG));
    }

    @Test public void cast08() {
      azzert.that(prudent(into.e("(double)x")), is(DOUBLE));
    }

    @Test public void cast09() {
      azzert.that(prudent(into.e("(Double)x")), is(DOUBLE));
    }

    @Test public void cast10() {
      azzert.that(prudent(into.e("(boolean)x")), is(BOOLEAN));
    }

    @Test public void cast11() {
      azzert.that(prudent(into.e("(Boolean)x")), is(BOOLEAN));
    }

    @Test public void cast12() {
      azzert.that(prudent(into.e("(String)x")), is(STRING));
    }

    @Test public void cast13() {
      azzert.that(prudent(into.e("(byte)1")), is(BYTE));
    }

    @Test public void cast14() {
      azzert.that(prudent(into.e("(Byte)1")), is(BYTE));
    }

    @Test public void cast15() {
      azzert.that(prudent(into.e("(short)1")), is(SHORT));
    }

    @Test public void cast16() {
      azzert.that(prudent(into.e("(Short)1")), is(SHORT));
    }

    @Test public void cast17() {
      azzert.that(prudent(into.e("(float)1")), is(FLOAT));
    }

    @Test public void cast18() {
      azzert.that(prudent(into.e("(Float)1")), is(FLOAT));
    }

    @Test public void cast19() {
      azzert.that(prudent(into.e("(float)1d")), is(FLOAT));
    }

    // s for conditionals
    @Test public void conditional01() {
      azzert.that(prudent(into.e("f() ? 3 : 7")), is(INT));
    }

    @Test public void conditional02() {
      azzert.that(prudent(into.e("f() ? 3L : 7")), is(LONG));
    }

    @Test public void conditional03() {
      azzert.that(prudent(into.e("f() ? 3L : 7.")), is(DOUBLE));
    }

    @Test public void conditional04() {
      azzert.that(prudent(into.e("f() ? 3L : 7.")), is(DOUBLE));
    }

    @Test public void conditional05() {
      azzert.that(prudent(into.e("f() ? 'a' : 7.")), is(DOUBLE));
    }

    @Test public void conditional06() {
      azzert.that(prudent(into.e("f() ? 'a' : 'b'")), is(CHAR));
    }

    @Test public void conditional07() {
      azzert.that(prudent(into.e("f() ? \"abc\" : \"def\"")), is(STRING));
    }

    @Test public void conditional08() {
      azzert.that(prudent(into.e("f() ? true : false")), is(BOOLEAN));
    }

    @Test public void conditional09() {
      azzert.that(prudent(into.e("f() ? f() : false")), is(BOOLEAN));
    }

    @Test public void conditional10() {
      azzert.that(prudent(into.e("f() ? f() : 2")), is(NUMERIC));
    }

    @Test public void conditional11() {
      azzert.that(prudent(into.e("f() ? f() : 2l")), is(NUMERIC));
    }

    @Test public void conditional12() {
      azzert.that(prudent(into.e("f() ? 2. : g()")), is(DOUBLE));
    }

    @Test public void conditional13() {
      azzert.that(prudent(into.e("f() ? 2 : 2%f()")), is(INTEGRAL));
    }

    @Test public void conditional14() {
      azzert.that(prudent(into.e("f() ? x : 'a'")), is(NUMERIC));
    }

    @Test public void conditional15() {
      azzert.that(prudent(into.e("f() ? x : g()")), is(NOTHING));
    }

    @Test public void conditional16() {
      azzert.that(prudent(into.e("f() ? \"a\" : h()")), is(STRING));
    }

    // tests for constructors
    @Test public void constructors01() {
      azzert.that(prudent(into.e("new List<Integer>()")), is(baptize("List<Integer>")));
      azzert.assertNotEquals(prudent(into.e("new List<Integer>()")), baptize("List"));
    }

    @Test public void constructors02() {
      azzert.that(prudent(into.e("new Object()")), is(baptize("Object")));
    }

    @Test public void constructors03() {
      azzert.that(prudent(into.e("new String(\"hello\")")), is(STRING));
    }

    @Test public void constructors04() {
      azzert.that(prudent(into.e("new Byte()")), is(BYTE));
    }

    @Test public void constructors05() {
      azzert.that(prudent(into.e("new Double()")), is(DOUBLE));
    }

    @Test public void InDecreamentSemantics01() {
      azzert.that(Axiom.type(i++), is(INT));
    }

    @Test public void InDecreamentSemantics02() {
      azzert.that(Axiom.type(l--), is(LONG));
    }

    @Test public void inDecreamentSemantics03() {
      azzert.that(Axiom.type(++s), is(SHORT));
    }

    @Test public void InDecreamentSemantics04() {
      azzert.that(Axiom.type(d++), is(DOUBLE));
    }

    @Test public void InDecreamentSemantics05() {
      azzert.that(Axiom.type(--f), is(FLOAT));
    }

    @Test public void InDecreamentSemantics06() {
      byte x = 0;
      azzert.that(Axiom.type(--x), is(BYTE));
    }

    @Test public void InDecreamentSemantics07() {
      char x = 0;
      azzert.that(Axiom.type(--x), is(CHAR));
    }

    // tests for recognition of literals
    @Test public void literal01() {
      azzert.that(prudent(into.e("3")), is(INT));
    }

    @Test public void literal02() {
      azzert.that(prudent(into.e("3l")), is(LONG));
    }

    @Test public void literal03() {
      azzert.that(prudent(into.e("3L")), is(LONG));
    }

    @Test public void literal04() {
      azzert.that(prudent(into.e("3d")), is(DOUBLE));
    }

    @Test public void literal05() {
      azzert.that(prudent(into.e("3D")), is(DOUBLE));
    }

    @Test public void literal06() {
      azzert.that(prudent(into.e("3.0d")), is(DOUBLE));
    }

    @Test public void literal07() {
      azzert.that(prudent(into.e("3.02D")), is(DOUBLE));
    }

    @Test public void Literal08() {
      azzert.that(prudent(into.e("3.098")), is(DOUBLE));
    }

    @Test public void literal09() {
      azzert.that(prudent(into.e("3f")), is(FLOAT));
    }

    @Test public void literal10() {
      azzert.that(prudent(into.e("3.f")), is(FLOAT));
    }

    @Test public void literal11() {
      azzert.that(prudent(into.e("3.0f")), is(FLOAT));
    }

    @Test public void literals12() {
      azzert.that(prudent(into.e("null")), is(NULL));
    }

    @Test public void literals13() {
      azzert.that(prudent(into.e("(((null)))")), is(NULL));
    }

    @Test public void literals14() {
      azzert.that(prudent(into.e("\"a string\"")), is(STRING));
    }

    @Test public void literals15() {
      azzert.that(prudent(into.e("'a'")), is(CHAR));
    }

    @Test public void literals16() {
      azzert.that(prudent(into.e("true")), is(BOOLEAN));
    }

    @Test public void makeSureIUnderstandSemanticsOfShift() {
      azzert.that(Axiom.type((short) 1 << 1L), is(INT));
    }

    // s for method calls. currently only toString()
    @Test public void methods1() {
      azzert.that(prudent(into.e("a.toString()")), is(STRING));
    }

    @Test public void methods2() {
      azzert.that(prudent(into.e("a.fo()")), is(NOTHING));
    }

    @Test public void methods3() {
      azzert.that(prudent(into.e("toString()")), is(STRING));
    }

    @Test public void methods4() {
      azzert.that(prudent(into.e("toString(x,y)")), is(NOTHING));
    }

    @Test public void OnaryPlusMinusSemantics01() {
      azzert.that(Axiom.type(+i), is(INT));
    }

    @Test public void OnaryPlusMinusSemantics02() {
      azzert.that(Axiom.type(-l), is(LONG));
    }

    @Test public void OnaryPlusMinusSemantics03() {
      final short x = 0;
      azzert.that(Axiom.type(+x), is(INT));
    }

    @Test public void OnaryPlusMinusSemantics04() {
      azzert.that(Axiom.type(+d), is(DOUBLE));
    }

    @Test public void OnaryPlusMinusSemantics05() {
      azzert.that(Axiom.type(-f), is(FLOAT));
    }

    @Test public void OnaryPlusMinusSemantics06() {
      azzert.that(Axiom.type(+b), is(INT));
    }

    @Test public void OnaryPlusMinusSemantics07() {
      azzert.that(Axiom.type(-b), is(INT));
    }

    @Test public void OnaryPlusMinusSemantics08() {
      final char x = 0;
      azzert.that(Axiom.type(-x), is(INT));
    }

    @Test public void OnaryPlusMinusSemantics09() {
      azzert.that(Axiom.type(-c1), is(INT));
    }
    // tests using old version of prudent that is now removed
    // @Test public void under01() {
    // azzert.that(prudent(into.e("+2"), INT), is(INT));
    // }
    //
    // @Test public void under02() {
    // azzert.that(prudent(into.e("~2"), ALPHANUMERIC), is(INTEGRAL));
    // }
    //
    // @Test public void under03() {
    // azzert.that(prudent(into.e("++x"), DOUBLE), is(DOUBLE));
    // }
    //
    // @Test public void under04() {
    // azzert.that(prudent(into.e("!x"), NOTHING), is(BOOLEAN));
    // }
    //
    // @Test public void under05() {
    // azzert.that(prudent(into.e("~'x'"), CHAR), is(INT));
    // }
    //
    // @Test public void under06() {
    // azzert.that(prudent(into.e("x+y"), NOTHING, NOTHING), is(ALPHANUMERIC));
    // }
    //
    // @Test public void under07() {
    // azzert.that(prudent(into.e("x+y"), INT, DOUBLE), is(DOUBLE));
    // }
    //
    // @Test public void under08() {
    // azzert.that(prudent(into.e("x+y"), INT, INT), is(INT));
    // }
    //
    // @Test public void under09() {
    // azzert.that(prudent(into.e("x+y"), STRING, STRING), is(STRING));
    // }
    //
    // @Test public void under10() {
    // azzert.that(prudent(into.e("x+y"), STRING, NULL), is(STRING));
    // }
    //
    // @Test public void under11() {
    // azzert.that(prudent(into.e("x+y"), NUMERIC, NULL), is(STRING));
    // }
    //
    // @Test public void under12() {
    // azzert.that(prudent(into.e("x+y"), LONG, INT), is(LONG));
    // }
    //
    // @Test public void under13() {
    // azzert.that(prudent(into.e("x+y"), LONG, INTEGRAL), is(LONG));
    // }
    //
    // @Test public void under14() {
    // azzert.that(prudent(into.e("x+y"), LONG, NUMERIC), is(NUMERIC));
    // }
    //
    // @Test public void under15() {
    // azzert.that(prudent(into.e("x+y"), INT, INTEGRAL), is(INTEGRAL));
    // }
    //
    // @Test public void under16() {
    // azzert.that(prudent(into.e("x&y"), INT, INT), is(INT));
    // }
    //
    // @Test public void under17() {
    // azzert.that(prudent(into.e("x|y"), INT, LONG), is(LONG));
    // }
    //
    // @Test public void under18() {
    // azzert.that(prudent(into.e("x<<y"), INTEGRAL, LONG), is(INTEGRAL));
    // }
    //
    // @Test public void under19() {
    // azzert.that(prudent(into.e("x%y"), NUMERIC, NOTHING), is(INTEGRAL));
    // }
    //
    // @Test public void under20() {
    // azzert.that(prudent(into.e("x>>y"), LONG, INTEGRAL), is(LONG));
    // }
    //
    // @Test public void under21() {
    // azzert.that(prudent(into.e("x^y"), NOTHING, INTEGRAL), is(INTEGRAL));
    // }
    //
    // @Test public void under22() {
    // azzert.that(prudent(into.e("x>y"), INT, INTEGRAL), is(BOOLEAN));
    // }
    //
    // @Test public void under23() {
    // azzert.that(prudent(into.e("x==y"), NOTHING, INTEGRAL), is(BOOLEAN));
    // }
    //
    // @Test public void under24() {
    // azzert.that(prudent(into.e("x!=y"), NUMERIC, NULL), is(BOOLEAN));
    // }
    //
    // @Test public void under25() {
    // azzert.that(prudent(into.e("x&&y"), BOOLEAN, BOOLEAN), is(BOOLEAN));
    // }
    //
    // @Test public void under26() {
    // azzert.that(prudent(into.e("x*y"), DOUBLE, NUMERIC), is(DOUBLE));
    // }
    //
    // @Test public void under27() {
    // azzert.that(prudent(into.e("x/y"), DOUBLE, INTEGRAL), is(DOUBLE));
    // }
    //
    // @Test public void under28() {
    // azzert.that(prudent(into.e("x-y"), INTEGRAL, LONG), is(LONG));
    // }
    //
    // @Test public void under29() {
    // azzert.that(prudent(into.e("x+y"), CHAR, CHAR), is(INT));
    // }
    //
    // @Test public void under30() {
    // azzert.that(prudent(into.e("x-y"), CHAR, INT), is(INT));
    // }
    //
    // @Test public void under31() {
    // azzert.that(prudent(into.e("x^y"), BOOLEAN, BOOLEAN), is(BOOLEAN));
    // }
    //
    // @Test public void under32() {
    // azzert.that(prudent(into.e("x+y"), INT, ALPHANUMERIC), is(ALPHANUMERIC));
    // }
    //
    // @Test public void under33() {
    // azzert.that(prudent(into.e("x+y"), INTEGRAL, NOTHING), is(ALPHANUMERIC));
    // }
  }
}
