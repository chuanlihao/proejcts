package counting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/** Test for {@link BaseConverter}. */
public class BaseConverterTest {

  private static final int VALID_INPUT = 123;
  private static final int INVALID_INPUT = 456;
  private static final String OUTPUT = "one-two-three";

  private BaseConverter converter;

  @Before
  public void setUp() {
    converter = new BaseConverter() {
      @Override
      public boolean isApplicable(int input) {
        return input == VALID_INPUT;
      }

      @Override
      protected void doApply(int validInput, StringBuilder outputCollector) {
        outputCollector.append(OUTPUT);
      }
    };
  }

  @Test
  public void testInvalidInput() {
    try {
      converter.apply(INVALID_INPUT);
      fail("Invalid input should cause an exceptions.");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testConversion() {
    assertEquals(OUTPUT, converter.apply(VALID_INPUT));

    StringBuilder outputCollector = new StringBuilder();
    converter.apply(VALID_INPUT, outputCollector);
    assertEquals(OUTPUT, outputCollector.toString());
  }
}
