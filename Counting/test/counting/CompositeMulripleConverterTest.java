package counting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class CompositeMulripleConverterTest {

  @Mock MulripleConverter converter1;
  @Mock MulripleConverter converter2;
  @Mock MulripleConverter converter3;

  private CompositeMulripleConverter converterEmpty;
  private CompositeMulripleConverter converter;

  @Before
  public void setUp() {
    initMocks(this);

    converterEmpty = new CompositeMulripleConverter();
    converter = new CompositeMulripleConverter(
        converter1, converter2, converter3);
  }

  @Test
  public void testApplicability() {
    when(converter1.isApplicable(3)).thenReturn(true);
    when(converter1.isApplicable(15)).thenReturn(true);
    when(converter1.isApplicable(105)).thenReturn(true);
    when(converter3.isApplicable(7)).thenReturn(true);

    assertTrue(converter.isApplicable(3));
    assertTrue(converter.isApplicable(7));
    assertTrue(converter.isApplicable(15));
    assertTrue(converter.isApplicable(105));

    assertFalse(converterEmpty.isApplicable(123));
    assertFalse(converter.isApplicable(8));
  }

  @Test
  public void testConversion() {
    when(converter1.isApplicable(anyInt())).thenReturn(true);
    when(converter2.isApplicable(anyInt())).thenReturn(true, false);
    when(converter3.isApplicable(anyInt())).thenReturn(false);

    converter.apply(123);
    converter.apply(456);

    verify(converter1, times(2)).apply(anyInt(), any(StringBuilder.class));
    verify(converter2, times(1)).apply(anyInt(), any(StringBuilder.class));
  }
}
