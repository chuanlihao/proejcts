package counting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class CountingSimulatorTest {

  @Mock CompositeMulripleConverter compositeMulripleConverter;
  @Mock LiteralCheckingConverter literalCheckingConverter;

  private CountingSimulator simulator;

  @Before
  public void setUp() {
    initMocks(this);

    simulator = new CountingSimulator();
    simulator.setConvertersForTesting(
        compositeMulripleConverter, literalCheckingConverter);
  }

  @Test
  public void testNumberValidation() {
    assertTrue(CountingSimulator.areValidNumbers(1, 2, 3));
    assertTrue(CountingSimulator.areValidNumbers(3, 5, 7));
    assertTrue(CountingSimulator.areValidNumbers(9, 5, 1));
    assertTrue(CountingSimulator.areValidNumbers(2, 4, 8));

    assertFalse(CountingSimulator.areValidNumbers(0, 1, 2));
    assertFalse(CountingSimulator.areValidNumbers(8, 9, 10));
    assertFalse(CountingSimulator.areValidNumbers(1, 1, 2));
    assertFalse(CountingSimulator.areValidNumbers(1, 2, 2));
    assertFalse(CountingSimulator.areValidNumbers(1, 2, 1));
    assertFalse(CountingSimulator.areValidNumbers(1, 1, 1));
  }

  @Test
  public void testCountOffForNormalNumber() {
    when(literalCheckingConverter.isApplicable(17)).thenReturn(false);
    when(compositeMulripleConverter.isApplicable(17)).thenReturn(false);

    assertEquals("17", simulator.countOffFor(17));
  }

  @Test
  public void testCountOffForLiteralSpecialNumber() {
    String literalSpecialString = "literal-special";

    when(literalCheckingConverter.isApplicable(17)).thenReturn(true);
    when(literalCheckingConverter.apply(17)).thenReturn(literalSpecialString);

    assertEquals(literalSpecialString, simulator.countOffFor(17));
  }

  @Test
  public void testCountOffForMulriple() {
    String mulripleString = "mulriple";

    when(literalCheckingConverter.isApplicable(15)).thenReturn(false);
    when(compositeMulripleConverter.isApplicable(15)).thenReturn(true);
    when(compositeMulripleConverter.apply(15)).thenReturn(mulripleString);

    assertEquals(mulripleString, simulator.countOffFor(15));
  }
}
