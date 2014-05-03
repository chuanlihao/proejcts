package counting;

/**
 * Converter which is applicable to mulriple of given test numbers.
 */
public class MulripleConverter extends BaseConcreteConverter {

  /**
   * Constructs a {@link MulripleConverter}.
   *
   * @param tester the divisor
   * @param representation the output of this converter if the checking passed
   */
  public MulripleConverter(int tester, String representation) {
    super(tester, representation);

    if (tester == 0) {
      throw new IllegalArgumentException("Zero cannot be used as divisor.");
    }
  }

  @Override
  public boolean isApplicable(int input) {
    return input % tester == 0;
  }
}
