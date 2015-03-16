package js;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

interface FinishedHandler {
  void execute(String result);
}

interface Activity {
  void setFinishedHandler(FinishedHandler handler);
  void trigger();
}

interface ActivityCreator {
  Activity create(String result);
}

abstract class BaseActivity implements Activity {

  static final FinishedHandler emptyHandler = result -> {};
  FinishedHandler handler;
  FinishedHandler lazyHandler = result -> handler.execute(result);

  @Override
  public void setFinishedHandler(FinishedHandler handler) {
    this.handler = handler;
  }
}

abstract class CompositeActivity extends BaseActivity {
  Activity first;
  Activity second;

  CompositeActivity(Activity first, Activity second) {
    this.first = first;
    this.second = second;
  }
}

class SequentialActivity extends CompositeActivity {

  SequentialActivity(Activity first, Activity second) {
    super(first, second);

    first.setFinishedHandler(result -> second.trigger());
    second.setFinishedHandler(lazyHandler);
  }

  @Override
  public void trigger() {
    first.trigger();
  }
}

class AnyActivity extends CompositeActivity {

  AnyActivity(Activity first, Activity second) {
    super(first, second);

    first.setFinishedHandler(result -> {
      second.setFinishedHandler(emptyHandler);
      handler.execute(result);
    });

    second.setFinishedHandler(result -> {
      first.setFinishedHandler(emptyHandler);
      handler.execute(result);
    });
  }

  @Override
  public void trigger() {
    first.trigger();
    second.trigger();
  }
}

class ParallelActivity extends CompositeActivity {

  ParallelActivity(Activity first, Activity second) {
    super(first, second);

    FinishedHandler parallelHandler = new FinishedHandler() {
      int count = 0;
      @Override
      public void execute(String result) {
        if (++count == 2) {
          handler.execute("ParallelResult");
        }
      }
    };

    first.setFinishedHandler(parallelHandler);
    second.setFinishedHandler(parallelHandler);
  }

  @Override
  public void trigger() {
    first.trigger();
    second.trigger();
  }
}

class DynamicActivity extends BaseActivity {

  Activity base;
  ActivityCreator creator;

  DynamicActivity(Activity base, ActivityCreator creator) {
    this.base = base;
    this.creator = creator;

    base.setFinishedHandler(result -> {
      Activity rest = creator.create(result);
      rest.setFinishedHandler(handler);
      rest.trigger();
    });
  }

  @Override
  public void trigger() {
    base.trigger();
  }
}


interface Predicate {
  boolean apply(String result);
}

class Predicates {
  static Predicate alwaysTrue() {
    return result -> true;
  }

  static Predicate resultMatch(String expected) {
    return expected::equals;
  }

  static Predicate count(int n) {
    return new Predicate() {
      int count = n;

      @Override
      public boolean apply(String result) {
        return count-- > 0;
      }
    };
  }
}

class Activities {

  static Activity createDummy() {
    return new BaseActivity() {
      @Override
      public void trigger() {
        handler.execute("DummyActivityResult");
      }
    };
  }

  static Activity createSequential(Activity... subActivities) {
    return createComposite(subActivities, 0, SequentialActivity::new);
  }

  static Activity createParallel(Activity... subActivities) {
    return createComposite(subActivities, 0, ParallelActivity::new);
  }

  static Activity createAny(Activity... subActivities) {
    return createComposite(subActivities, 0, AnyActivity::new);
  }

  static Activity createDynamic(Activity base, ActivityCreator creator) {
    return new DynamicActivity(base, creator);
  }

  static Activity createConditional(Activity base, Predicate predicate, Activity then) {
    return createConditional(base, predicate, then, createDummy());
  }

  static Activity createConditional(
      Activity base, Predicate predicate, Activity then, Activity fallback) {
    LinkedHashMap<Predicate, Activity> entries = new LinkedHashMap<>();
    entries.put(predicate, then);
    return createConditional(base, entries, fallback);
  }

  static Activity createConditional(
      Activity base, LinkedHashMap<Predicate, Activity> entries, Activity fallback) {
    return createDynamic(base, result -> {
      for (Entry<Predicate, Activity> entry : entries.entrySet()) {
        if (entry.getKey().apply(result)) {
          return entry.getValue();
        }
      }
      return fallback;
    });
  }

  static Activity createLoop(Activity baseActivity, Predicate predicate, Activity bodyActivity) {
    return createDynamic(
        baseActivity,
        baseResult -> predicate.apply(baseResult)
            ? createSequential(bodyActivity, createLoop(baseActivity, predicate, bodyActivity))
            : createDummy());
  }

  interface CompositeCreator {
    Activity create(Activity first, Activity second);
  }

  private static Activity createComposite(
      Activity[] activities, int index, CompositeCreator creator) {
    return index == activities.length -1
        ? activities[index]
        : creator.create(activities[index], createComposite(activities, index + 1, creator));
  }
}


interface EventWaiter {
  void receive(String result);
}

class JobActivity extends BaseActivity implements EventWaiter {
  String name;

  JobActivity(String name) {
    this.name = name;
  }

  @Override
  public void trigger() {
    EventProcessor.bookWaiter(name, this);
    System.out.println("TRIGGER " + name);
  }

  @Override
  public void receive(String result) {
    handler.execute(result);
  }
}

class DomainActivities {

  static Activity createJob(String name) {
    return new JobActivity(name);
  }
}

class EventProcessor {

  static Map<String, EventWaiter> waiters = new HashMap<>();

  static void bookWaiter(String jobName, EventWaiter waiter) {
    waiters.put(jobName, waiter);
  }

  static void run() {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      try {
        String[] line = reader.readLine().split(" ");
        if (line[0].equals("START")) {
          createActivity().trigger();
        } else {
          String result = line.length == 1 ? "EMPTY" : line[1];
          waiters.remove(line[0]).receive(result);
        }
      } catch (RuntimeException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  static Activity createActivity() {
    Activity activity = DomainActivities.createJob("A");
    activity.setFinishedHandler(System.out::println);
    return activity;
  }
}

public class Workflow {
  public static void main(String[] args) {
    System.out.println("Starting ...");
    EventProcessor.run();
  }
}
