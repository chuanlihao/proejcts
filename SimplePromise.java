import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

interface AsyncCall {
  void call(Object value, Consumer<Object> callback);
}

class Promise {
  private Object input;
  private List<AsyncCall> calls = new ArrayList<>();
  private Consumer<Object> consumer = (v) -> {};

  private Promise() {}

  static Promise startWith(Object input) {
    Promise promise = new Promise();
    promise.input = input;
    return promise;
  }

  Promise andSchedule(AsyncCall call) {
    calls.add(call);
    return this;
  }

  void consumeBy(Consumer<Object> consumer) {
    this.consumer = consumer;
    trigger(input, 0);
  }

  private void trigger(Object input, int index) {
    if (index == calls.size()) {
      consumer.accept(input);
    } else {
      calls.get(index).call(input, output -> trigger(output, index + 1));
    }
  }
}

public class SimplePromise {

  static AsyncCall testA = (value, callback) -> {
    System.out.println("trigger test A: " + value);
    new Thread(() -> {
      sleep(5);
      callback.accept("output A");
    }).start();
  };

  static AsyncCall testB = (value, callback) -> {
    System.out.println("trigger test B: " + value);
    new Thread(() -> {
      sleep(5);
      callback.accept("output B");
    }).start();
  };

  static AsyncCall testC = (value, callback) -> {
    System.out.println("trigger test C: " + value);
    new Thread(() -> {
      sleep(5);
      callback.accept("output C");
    }).start();
  };

  public static void main(String[] args) {
    simpleCall();
    callWithPromise();
    sleep(50);
  }

  static void simpleCall() {
    testA.call(
        "input A",
        outputA ->
            testB.call(
                outputA,
                outputB ->
                    testC.call(
                        outputB,
                        System.out::println)));
  }

  static void callWithPromise() {
    Promise
        .startWith("input A")
        .andSchedule(testA)
        .andSchedule(testB)
        .andSchedule(testC)
        .consumeBy(System.out::println);
  }

  static void sleep(int seconds) {
    try {
      TimeUnit.SECONDS.sleep(seconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
