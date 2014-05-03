package counting;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/** Test for {@link BaseConcreteConverter}. */
public class BaseConcreteConverterTest {

  private static final int INPUT = 123;
  private static final String OUTPUT = "test-output";

  private BaseConcreteConverter converter;

  @Before
  public void setUp() {
    converter = new BaseConcreteConverter(INPUT, OUTPUT) {
      @Override
      public boolean isApplicable(int input) {
        // Simply returns true here, since the logic is not used for this regard.
        return true;
      }
    };
  }

  @Test
  public void testConversion() {
    assertEquals(OUTPUT, converter.apply(INPUT));
  }
}
