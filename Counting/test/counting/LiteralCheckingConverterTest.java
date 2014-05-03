package counting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/** Test for {@link LiteralCheckingConverter}. */
public class LiteralCheckingConverterTest {

  private LiteralCheckingConverter converter3;
  private LiteralCheckingConverter converter0;

  @Before
  public void setUp() {
    converter3 = new LiteralCheckingConverter(3, "output");
    converter0 = new LiteralCheckingConverter(0, "output");
  }

  @Test
  public void testInvalidConstruction() {
    try {
      new LiteralCheckingConverter(-1, "invalid");
      fail("Should fail for negative number.");
    } catch (IllegalArgumentException e) {
      // expected
    }

    try {
      new LiteralCheckingConverter(10, "invalid");
      fail("Should fail for number great than 9.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testApplicability() {
    assertTrue(converter3.isApplicable(3));
    assertTrue(converter3.isApplicable(13));
    assertTrue(converter3.isApplicable(31));
    assertTrue(converter3.isApplicable(-123));
    assertTrue(converter3.isApplicable(123321));
    assertTrue(converter0.isApplicable(0));
    assertTrue(converter0.isApplicable(120012));
    assertTrue(converter0.isApplicable(-10));

    assertFalse(converter3.isApplicable(7));
    assertFalse(converter3.isApplicable(-56));
    assertFalse(converter0.isApplicable(7));
    assertFalse(converter0.isApplicable(-56));
  }
}
