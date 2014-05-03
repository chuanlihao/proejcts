package counting;

/**
 * Converter which checks whether a number contains the given digit literally.
 */
public class LiteralCheckingConverter extends BaseConcreteConverter {

  /**
   * Constructs a {@link LiteralCheckingConverter}.
   *
   * @param tester the literal digit to be tested against
   * @param representation the output of this converter if the checking passed
   */
  public LiteralCheckingConverter(int tester, String representation) {
    super(tester, representation);

    if (!(0 <= tester && tester <= 9)) {
      throw new IllegalArgumentException(
          "Literal checking must be against a single digit.");
    }
  }

  @Override
  public boolean isApplicable(int input) {
    if (input < 0) {
      input = -input;
    }

    /*
     * Checks whether input (integer without leading zeroes) contains given digit.
     * Condition "0 contains 0" will also pass.
     */
    do {
      if (getLeastSignificantDigit(input) == tester) {
        return true;
      }
      input = removeLastSignificantDigit(input);
    } while (input > 0);

    return false;
  }

  private static int getLeastSignificantDigit(int number) {
    return number % 10;
  }

  private static int removeLastSignificantDigit(int number) {
    return number / 10;
  }
}
