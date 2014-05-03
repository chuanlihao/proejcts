package counting;

/**
 * Base converter, which implements common functionality for {@link Converter#apply}s.
 */
public abstract class BaseConverter implements Converter {

  @Override
  public void apply(int input, StringBuilder outputCollector) {
    if (!isApplicable(input)) {
      throw new IllegalArgumentException(
          String.format("Not applicable for input: %d.", input));
    }

    doApply(input, outputCollector);
  }

  @Override
  public String apply(int input) {
    StringBuilder outputCollector = new StringBuilder();
    apply(input, outputCollector);
    return outputCollector.toString();
  }

  /**
   * Applies this converter to given {@code validInput}, appends its {@link String}
   * representation to given {@code outputCollector}.
   */
  protected abstract void doApply(int validInput, StringBuilder outputCollector);
}
