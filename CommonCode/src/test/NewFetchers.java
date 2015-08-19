package test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class Utils {
  public static <A, B, C> Function<A, C> compose(
      Function<A, B> firstFunc, Function<B, C> secondFunc) {
    return input -> {
      B innerValue = firstFunc.apply(input);
      return innerValue == null ? null : secondFunc.apply(innerValue);
    };
  }
}

class ComposableFunc<I, O> implements Function<I, O> {
  private final Function<I, O> func;

  private ComposableFunc(Function<I, O> func) {
    this.func = func;
  }

  public static <I, O> ComposableFunc<I, O> startWith(Function<I, O> func) {
    return new ComposableFunc<I, O>(func);
  }

  public <R> ComposableFunc<I, R> then(Function<O, R> followingFunc) {
    return new ComposableFunc<I, R>(Utils.compose(func, followingFunc));
  }

  @Override
  public O apply(I input) {
    return func.apply(input);
  }
}

public class NewFetchers {
  public static void main(String[] args) {
    Function<String, String> lastFourDigitsFunc = value -> {
        int length = value.length();
        return length <= 4 ? value : value.substring(length - 4);
    };

    ComposableFunc<Long, User> userFetcher = ComposableFunc
        .startWith(Api::getApplication)
        .then(app -> Api.getUser(app.getUserId()));
    Function<Long, String> usernameFetcher = userFetcher
        .then(User::getName);
    Function<Long, String> idNumberLastFourDigitsFetcher = userFetcher
        .then(User::getIdNumber)
        .then(lastFourDigitsFunc);
    Function<Long, String> userMobileLastFourDigitsFetcher = userFetcher
        .then(user -> Api.getContact(user.getContactId()))
        .then(Contact::getNumber)
        .then(lastFourDigitsFunc);

    Map<String, Function<Long, String>> fetchers = new HashMap<>();
    fetchers.put("Username", usernameFetcher);
    fetchers.put("IdNumberLastFourDigits", idNumberLastFourDigitsFetcher);
    fetchers.put("UserMobileLastFourDigits", userMobileLastFourDigitsFetcher);

    long appId = 1234L;
    String key = "Username";
    System.out.println(fetchers.get(key).apply(appId));
  }
}
