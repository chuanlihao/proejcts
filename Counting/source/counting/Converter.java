package counting;

/**
 * Converter which converts an integer to its {@link String} representation.
 */
public interface Converter {

  /** Whether this converter is applicable to the given {@code input}. */
  boolean isApplicable(int input);

  /**
   * Applies this converter to given {@code input}, appends its {@link String}
   * representation to {@code outputCollector}.
   *
   * @throws {@link IllegalArgumentException} if this converter is not applicable
   */
  void apply(int input, StringBuilder outputCollector);

  /**
   * Applies this converter to given {@code input}.
   *
   * @return its {@link String} representation
   * @throws {@link IllegalArgumentException} if this converter is not applicable
   */
  String apply(int input);
}
