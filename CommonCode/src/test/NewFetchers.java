package test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class FuncFetcher<I, O> {
  private Function<I, O> func;

  private FuncFetcher(Function<I, O> func) {
    this.func = func;
  }

  static <I, O> FuncFetcher<I, O> from(Function<I, O> func) {
    return new FuncFetcher<I, O>(func);
  }

  O fetch(I input) {
    return func.apply(input);
  }

  <NO> FuncFetcher<I, NO> then(Function<O, NO> following) {
    return FuncFetcher.from(compose(func, following));
  }

  private static <A, B, C> Function<A, C> compose(
      Function<A, B> firstFunc, Function<B, C> thenFunc) {
    return input -> {
      B innerValue = firstFunc.apply(input);
      return innerValue == null ? null : thenFunc.apply(innerValue);
    };
  }
}

public class NewFetchers {
  public static void main(String[] args) {
    Function<String, String> lastFourDigitsFunc = value -> {
        int length = value.length();
        return length <= 4 ? value : value.substring(length - 4);
    };

    FuncFetcher<Long, User> userFetcher = FuncFetcher
        .from(Api::getApplication)
        .then(app -> Api.getUser(app.getUserId()));
    FuncFetcher<Long, String> usernameFetcher = userFetcher
        .then(User::getName);
    FuncFetcher<Long, String> idNumberLastFourDigitsFetcher = userFetcher
        .then(User::getIdNumber)
        .then(lastFourDigitsFunc);
    FuncFetcher<Long, String> userMobileLastFourDigitsFetcher = userFetcher
        .then(user -> Api.getContact(user.getContactId()))
        .then(Contact::getNumber)
        .then(lastFourDigitsFunc);

    Map<String, FuncFetcher<Long, String>> fetchers = new HashMap<>();
    fetchers.put("Username", usernameFetcher);
    fetchers.put("IdNumberLastFourDigits", idNumberLastFourDigitsFetcher);
    fetchers.put("UserMobileLastFourDigits", userMobileLastFourDigitsFetcher);

    long appId = 1234L;
    String key = "Username";
    System.out.println(fetchers.get(key).fetch(appId));
  }
}
