package counting;

/**
 * Composite converter, which contains a list of sub {@link MulripleConverter}s.
 *
 * <p>This composite converter is applicable to an input if and only if at least
 * one of its sub-converters is applicable to the input.</p>
 */
public class CompositeMulripleConverter extends BaseConverter {

  private MulripleConverter[] subConverters;

  /**
   * Constructs a {@link CompositeMulripleConverter} from a list of
   * sub {@link MulripleConverter}s.
   */
  public CompositeMulripleConverter(MulripleConverter... subConverters) {
    this.subConverters = subConverters;
  }

  @Override
  public boolean isApplicable(int input) {
    // applicable if one of the sub-converters is applicable
    for (MulripleConverter subConverter : subConverters) {
      if (subConverter.isApplicable(input)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void doApply(int validInput, StringBuilder outputCollector) {
    for (MulripleConverter subConverter : subConverters) {
      /*
       * Here, the composite converter is applicable to validInput, which means
       * at least one of the sub converters is applicable to validInput.  It's
       * still possible that some sub converters are not applicable, so we need
       * to check whether current sub-converter is applicable here.
       */
      if (subConverter.isApplicable(validInput)) {
        subConverter.apply(validInput, outputCollector);
      }
    }
  }
}
