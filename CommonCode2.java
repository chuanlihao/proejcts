package test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
interface MonadFunction<I, O> extends Function<I, O> {

  @Override
  default <V> MonadFunction<I, V> andThen(Function<? super O, ? extends V> after) {
    return input -> {
      O middleResult = apply(input);
      return middleResult == null ? null : after.apply(middleResult);
    };
  }
}

class Application {
  long getUserId() { return 0L; }
}

class User {
  String getName() { return null; }
  String getIdNumber() { return null; }
  long getContactId() { return 0L; }
}

class Contact {
  String getNumber() { return null; }
}

class Api {
  static Application getApplication(long id) { return null; }
  static User getUser(long id) { return null; }
  static Contact getContact(long id) { return null; }
}


public class CommonCode2 {

  @SafeVarargs
  static <T> T wrap(T initValue, Consumer<T> ... consumers) {
    for (Consumer<T> consumer : consumers) {
      consumer.accept(initValue);
    }
    return initValue;
  }

  public static void main(String[] args) {
    MonadFunction<String, String> lastFourDigitsFunc = value -> {
      int length = value.length();
      return length <= 4 ? value : value.substring(length - 4);
    };

    MonadFunction<Long, Application> applicationFetcher = Api::getApplication;
    MonadFunction<Long, User> userFetcher =
        applicationFetcher.andThen(app -> Api.getUser(app.getUserId()));
    MonadFunction<Long, String> usernameFetcher =
        userFetcher.andThen(User::getName);
    MonadFunction<Long, String> idNumberLastFourDigitsFetcher =
        userFetcher.andThen(User::getIdNumber).andThen(lastFourDigitsFunc);
    MonadFunction<Long, String> userMobileLastFourDigitsFetcher =
        userFetcher
            .andThen(user -> Api.getContact(user.getContactId()))
            .andThen(Contact::getNumber)
            .andThen(lastFourDigitsFunc);
    MonadFunction<Long, String> userMobileLastFourDigitsFetcher2 =
        userFetcher
            .andThen(User::getContactId)
            .andThen(Api::getContact)
            .andThen(Contact::getNumber)
            .andThen(lastFourDigitsFunc);

    Map<String, MonadFunction<Long, String>> fetchers = wrap(
        new HashMap<>(),
        x -> x.put("Username", usernameFetcher),
        x -> x.put("IdNumberLastFourDigitsFetcher", idNumberLastFourDigitsFetcher),
        x -> x.put("UserMobileLastFourDigitsFetcher", userMobileLastFourDigitsFetcher),
        x -> x.put("UserMobileLastFourDigitsFetcher2", userMobileLastFourDigitsFetcher2));

    long appId = 1234L;
    String key = "Username";
    System.out.println(fetchers.get(key).apply(appId));
  }
}
