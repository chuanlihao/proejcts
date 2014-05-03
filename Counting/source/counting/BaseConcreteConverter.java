package counting;

/**
 * Base concrete converter.
 *
 * <p>Its behavior depends on a concrete check/condition.  Once the check passed,
 * it returns the given representation as the output.</p>
 */
public abstract class BaseConcreteConverter extends BaseConverter {

  protected int tester;
  protected String representation;

  /**
   * Constructs a {@link BaseConcreteConverter}.
   *
   * @param tester the concrete checking depends on this parameter
   * @param representation the output of this converter if the checking passed
   */
  protected BaseConcreteConverter(int tester, String representation) {
    this.tester = tester;
    this.representation = representation;
  }

  @Override
  protected void doApply(int validInput, StringBuilder outputCollector) {
    outputCollector.append(representation);
  }
}
