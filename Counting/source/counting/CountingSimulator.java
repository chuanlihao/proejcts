package counting;

import java.util.Scanner;

/**
 * Counting simulation system.
 *
 * <p>Typical usage:
 * {@code new CountingSimulator().inputNumbers().prepareConverters().countOff();}.</p>
 */
public class CountingSimulator {

  private static final String FIZZ = "Fizz";
  private static final String BUZZ = "Buzz";
  private static final String WHIZZ = "Whizz";
  private static final int START_NUMBER = 1;
  private static final int END_NUMBER = 100;

  private CompositeMulripleConverter compositeMulripleConverter;
  private LiteralCheckingConverter literalCheckingConverter;

  private int number1;
  private int number2;
  private int number3;

  public CountingSimulator inputNumbers() {
    Scanner inputScanner = new Scanner(System.in);
    try {
      number1 = inputScanner.nextInt();
      number2 = inputScanner.nextInt();
      number3 = inputScanner.nextInt();
    } finally {
      inputScanner.close();
    }

    if (!areValidNumbers(number1, number2, number3)) {
      throw new IllegalStateException(
          "Invalid input.  You should enter 3 distinct numbers between 1 and 9.");
    }

    return this;
  }

  /**
   * Checks whether {@code x}, {code y} and {code z} are distinct numbers
   * between 1 and 9.  This function is visible for testing.
   */
  static boolean areValidNumbers(int x, int y, int z) {
    return isBetweenOneAndNine(x)
        && isBetweenOneAndNine(y)
        && isBetweenOneAndNine(z)
        && areDistinctNumbers(x, y, z);
  }

  private static boolean isBetweenOneAndNine(int x) {
    return 1 <= x && x <= 9;
  }

  private static boolean areDistinctNumbers(int x, int y, int z) {
    return (x != y) && (x != z) && (y != z);
  }

  public CountingSimulator prepareConverters() {
    compositeMulripleConverter = new CompositeMulripleConverter(
        new MulripleConverter(number1, FIZZ),
        new MulripleConverter(number2, BUZZ),
        new MulripleConverter(number3, WHIZZ));
    literalCheckingConverter = new LiteralCheckingConverter(number1, FIZZ);

    return this;
  }

  /** For testing only. */
  void setConvertersForTesting(
      CompositeMulripleConverter compositeMulripleConverter,
      LiteralCheckingConverter literalCheckingConverter) {
    this.compositeMulripleConverter = compositeMulripleConverter;
    this.literalCheckingConverter = literalCheckingConverter;
  }

  public CountingSimulator countOff() {
    for (int number = START_NUMBER; number <= END_NUMBER; number++) {
      // Note: May use a StringBuilder to collect outputs for efficiency.
      System.out.println(countOffFor(number));
    }

    return this;
  }

  /** Visible for testing. */
  String countOffFor(int number) {
    if (literalCheckingConverter.isApplicable(number)) {
      return literalCheckingConverter.apply(number);
    } else if (compositeMulripleConverter.isApplicable(number)) {
      return compositeMulripleConverter.apply(number);
    } else {
      return String.valueOf(number);
    }
  }

  /** Driver function. */
  public static void main(String[] args) {
    new CountingSimulator()
        .inputNumbers()
        .prepareConverters()
        .countOff();
  }
}
