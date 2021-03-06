package il.org.spartan.refactoring.java;

import static il.org.spartan.azzert.*;
import static il.org.spartan.refactoring.engine.into.*;

import org.junit.*;

import il.org.spartan.*;

@SuppressWarnings("static-method") public class TermsReogranizerTest {
  @Test public void test00() {
    azzert.that(TermsReorganizer.simplify(i("a-b")), iz("a-b"));
  }

  @Test public void test01() {
    azzert.that(TermsReorganizer.simplify(i("a+b+c")), iz("a + b + c"));
  }

  @Test public void test02() {
    azzert.that(TermsReorganizer.simplify(i("a-c")), iz("a-c"));
  }

  @Test public void test03() {
    azzert.that(TermsReorganizer.simplify(i("a + (b+c)")), iz("a + b+c"));
  }

  @Test public void test04() {
    azzert.that(TermsReorganizer.simplify(i("a + (b-c)")), iz("a + b-c"));
  }

  @Test public void test05() {
    azzert.that(TermsReorganizer.simplify(i("a + b -c + d -e + f")), iz("a + b + d + f -c -e"));
  }

  @Test public void test06() {
    azzert.that(TermsReorganizer.simplify(i("a + (b + c + d)")), iz("a + b + c + d"));
  }

  @Test public void test07() {
    azzert.that(TermsReorganizer.simplify(i("a - (c -d)")), iz("a + d -c"));
  }

  @Test public void test08() {
    azzert.that(TermsReorganizer.simplify(i("a + -a")), iz("a - a"));
  }

  @Test public void test09() {
    azzert.that(TermsReorganizer.simplify(i("+a + +a")), iz("a + a"));
  }

  @Test public void test10() {
    azzert.that(TermsReorganizer.simplify(i("a - -b")), iz("a + b"));
  }

  @Test public void test11() {
    azzert.that(TermsReorganizer.simplify(i("a + - - (b + c)")), iz("a + b + c"));
  }

  @Test public void test12() {
    azzert.that(TermsReorganizer.simplify(i("a + + - - - (b -c)")), iz("a +c -b"));
  }

  @Test public void test13() {
    azzert.that(TermsReorganizer.simplify(i("-a + + - - - (b -c)")), iz("c -a -b"));
  }

  @Test public void test14() {
    azzert.that(TermsReorganizer.simplify(i("-a + + - - - (b -(-c))")), iz(" -a -b-c"));
  }

  @Test public void test15() {
    azzert.that(TermsReorganizer.simplify(i("-a-b")), iz("-a-b"));
  }

  @Test public void test16() {
    azzert.that(TermsReorganizer.simplify(i("x-a-b")), iz("x-a-b"));
  }

  @Test public void test17() {
    azzert.that(TermsReorganizer.simplify(i("x-a-b + y")), iz("x + y-a-b"));
  }

  @Test public void test18() {
    azzert.that(TermsReorganizer.simplify(i("(-(x-a))-+ + (y -b)")), iz("a + b-x-y"));
  }

  @Test public void test19() {
    azzert.that(TermsReorganizer.simplify(i("a-b*c - (x - - - (d*e))")), //
        iz("a + d*e - b*c -x"));
  }

  @Test public void test20() {
    azzert.that(TermsReorganizer.simplify(i("a-b*c")), iz("a - b*c"));
  }
}