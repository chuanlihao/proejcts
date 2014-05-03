package counting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/** Test for {@link MulripleConverter}. */
public class MulripleConverterTest {

  private MulripleConverter converter3;
  private MulripleConverter converter11;

  @Before
  public void setUp() {
    converter3 = new MulripleConverter(3, "output");
    converter11 = new MulripleConverter(11, "output");
  }

  @Test
  public void testInvalidConstruction() {
    try {
      new MulripleConverter(0, "invalid");
      fail("Zero should not be used as divisior.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testApplicability() {
    assertTrue(converter3.isApplicable(3));
    assertTrue(converter3.isApplicable(6));
    assertTrue(converter11.isApplicable(11));
    assertTrue(converter11.isApplicable(33));

    assertFalse(converter3.isApplicable(4));
    assertFalse(converter3.isApplicable(8));
    assertFalse(converter11.isApplicable(42));
    assertFalse(converter11.isApplicable(87));
  }
}
