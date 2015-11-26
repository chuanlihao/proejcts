package test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@FunctionalInterface
interface MonadFunction<I, O> {

  O apply(I input);

  default <R> MonadFunction<I, R> then(MonadFunction<O, R> following) {
    return input -> {
      O middleResult = apply(input);
      return middleResult == null ? null : following.apply(middleResult);
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


public class CommonCodeSample {

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
        applicationFetcher.then(app -> Api.getUser(app.getUserId()));
    MonadFunction<Long, String> usernameFetcher =
        userFetcher.then(User::getName);
    MonadFunction<Long, String> idNumberLastFourDigitsFetcher =
        userFetcher.then(User::getIdNumber).then(lastFourDigitsFunc);
    MonadFunction<Long, String> userMobileLastFourDigitsFetcher =
        userFetcher
            .then(user -> Api.getContact(user.getContactId()))
            .then(Contact::getNumber)
            .then(lastFourDigitsFunc);
    MonadFunction<Long, String> userMobileLastFourDigitsFetcher2 =
        userFetcher
            .then(User::getContactId)
            .then(Api::getContact)
            .then(Contact::getNumber)
            .then(lastFourDigitsFunc);

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
