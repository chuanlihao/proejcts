package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BaseConfig<S, T extends BaseConfig<?, ?>> {
  Map<String, String> properties = new HashMap<>();
  List<BaseConfig<?, ?>> subConfigs = new ArrayList<>();
  S upper;

  BaseConfig(S upper) {
    this.upper = upper;
  }

  @SuppressWarnings("unchecked")
  T setProperties(String key, String value) {
    properties.put(key, value);
    return (T) this;
  }

  S back() {
    return upper;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PROPERTIES: " + properties.toString() + "\n");
    for (BaseConfig<?, ?> config : subConfigs) {
      for (String s : config.toString().split("\n")) {
        builder.append("\t" + s + "\n");
      }
    }
    return builder.toString();
  }
}

class AA extends BaseConfig<Void, AA> {
  AA() {
    super(null);
  }

  BB newBb() {
    BB config = new BB(this);
    subConfigs.add(config);
    return config;
  }
}

class BB extends BaseConfig<AA, BB> {
  CC ccConfig = new CC(this);

  BB(AA upper) {
    super(upper);
    subConfigs.add(ccConfig);
  }

  CC getCc() {
    return ccConfig;
  }
}

class CC extends BaseConfig<BB, CC> {
  CC(BB upper) {
    super(upper);
  }
}

public class FluentApi {
  public static void main(String[] args) {
    AA test = new AA()
        .setProperties("aa-1", "11")
        .setProperties("aa-2", "22")
        .newBb()
            .setProperties("bb-1", "111")
            .setProperties("bb-2", "222")
            .getCc()
                .setProperties("cc-1", "1111")
                .setProperties("cc-2", "2222")
                .back()
            .back()
        .newBb()
            .setProperties("yy-1", "bbb")
            .getCc()
                .setProperties("alpha", "value")
                .back()
            .back();
    System.out.println(test);
  }
}
