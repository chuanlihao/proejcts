package test;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

interface FinishedHandler {
  void finish(String result);
}

interface Activity {
  Activity withFinishedHandler(FinishedHandler handler);
  void trigger();
}

interface ActivityCreator {
  Activity create(String initialParam);
}

abstract class BaseActivity implements Activity {
  FinishedHandler handler;

  @Override
  public BaseActivity withFinishedHandler(FinishedHandler handler) {
    this.handler = handler;
    return this;
  }
}

class DynamicActivity extends BaseActivity {
  Activity baseActivity;
  ActivityCreator creator;

  public DynamicActivity(Activity baseActivity, ActivityCreator creator) {
    this.baseActivity = baseActivity;
    this.creator = creator;
  }

  @Override
  public void trigger() {
    baseActivity
        .withFinishedHandler(
            baseResult -> creator.create(baseResult).withFinishedHandler(handler).trigger())
        .trigger();;
  }
}

class SequentialActivity extends DynamicActivity {
  public SequentialActivity(Activity first, Activity second) {
    super(first, result -> second);
  }
}

class ParallelActivity extends BaseActivity {
  Activity first;
  Activity second;
  int finished = 0;

  public ParallelActivity(Activity first, Activity second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public void trigger() {
    FinishedHandler tempHandler = result -> {
      if (++finished == 2) {
        handler.finish("PARALLEL ACTIVITY");
      }
    };
    first.withFinishedHandler(tempHandler).trigger();
    second.withFinishedHandler(tempHandler).trigger();
  }
}

class EmptyActivity extends BaseActivity {
  @Override
  public void trigger() {
    handler.finish("EMPTY");
  }
}

abstract class JobActivity extends BaseActivity {
  String name;

  public JobActivity(String name) {
    this.name = name;
  }
}

class SimpleActivity extends JobActivity {
  public SimpleActivity(String name) {
    super(name);
  }

  @Override
  public void trigger() {
    System.out.println("DOING SIMPLE JOB: " + name);
    handler.finish("SIMPLE ACTIVITY");
  }
}

interface FinishEventListener {
  void finish(String result);
}

class ComplexActivity extends JobActivity implements FinishEventListener {
  public ComplexActivity(String name) {
    super(name);
  }

  @Override
  public void trigger() {
    System.out.println("STARTING COMPLEX JOB: " + name);
  }

  @Override
  public void finish(String result) {
    System.out.println("FINISHED COMPLEX JOB: " + name);
    handler.finish(result);
  }
}

interface Predicate {
  boolean check(String result);
}

public class Workflow2 {

  static class EmptyActivityWithResult extends BaseActivity {
    String result;

    public EmptyActivityWithResult(String result) {
      this.result = result;
    }

    @Override
    public void trigger() {
      System.out.println("DOING EMPTY ACTIVITY WITH RESULT: " + result);
      handler.finish(result);
    }
  }

  static interface Composer {
    Activity compose(Activity first, Activity second);
  }

  static Activity createConditionalActivity(
      Activity baseActivity, Iterator<Entry<Predicate, Activity>> conditions) {
    if (!conditions.hasNext()) {
      return new EmptyActivity();
    }

    Entry<Predicate, Activity> first = conditions.next();
    return new DynamicActivity(
        baseActivity,
        result ->
          first.getKey().check(result)
              ? first.getValue()
              : createConditionalActivity(new EmptyActivityWithResult(result), conditions));
  }

  static Activity createSequentialActivities(Activity... activities) {
    return composeActivities(SequentialActivity::new, activities, 0);
  }

  static Activity createParallelActivity(Activity... activities) {
    return composeActivities(ParallelActivity::new, activities, 0);
  }

  static Activity composeActivities(Composer composer, Activity[] activities, int startIndex) {
    return startIndex == activities.length
        ? new EmptyActivity()
        : composer.compose(
            activities[startIndex], composeActivities(composer, activities, startIndex + 1));
  }

  public static void main(String[] args) {
    LinkedHashMap<Predicate, Activity> conditions = new LinkedHashMap<>();
    conditions.put(r -> r.equals("A"), new SimpleActivity("MATCH A"));
    conditions.put(r -> r.equals("B"), new SimpleActivity("MATCH B"));
    conditions.put(r -> r.equals("C"), new SimpleActivity("MATCH C"));

    createConditionalActivity(new EmptyActivityWithResult("D"), conditions.entrySet().iterator())
        .withFinishedHandler(System.out::println)
        .trigger();

    ComplexActivity a = new ComplexActivity("A");
    ComplexActivity b = new ComplexActivity("B");
    SimpleActivity c = new SimpleActivity("C");
    createSequentialActivities(createParallelActivity(a, b), c)
        .withFinishedHandler(System.out::println).trigger();
    b.finish("X");
    a.finish("Y");

    new SequentialActivity(new SimpleActivity("Job A"), new SimpleActivity("Job B"))
        .withFinishedHandler(System.out::println)
        .trigger();

    new DynamicActivity(new SimpleActivity("Job A"), result -> new SimpleActivity("Job B"))
        .withFinishedHandler(System.out::println)
        .trigger();
  }
}
