package test;

import java.util.HashMap;
import java.util.Map;

interface Fetcher {
  String fetch(long appId);
}

class UsernameFetcher implements Fetcher {
  @Override
  public String fetch(long appId) {
    Application app = Api.getApplication(appId);
    if (app != null) {
      User user = Api.getUser(app.getUserId());
      if (user != null) {
        return user.getName();
      }
    }
    return null;
  }
}

class IdNumberLastFourDigitsFetcher implements Fetcher {
  @Override
  public String fetch(long appId) {
    Application app = Api.getApplication(appId);
    if (app != null) {
      User user = Api.getUser(app.getUserId());
      if (user != null) {
        String number = user.getIdNumber();
        if (number != null) {
          int length = number.length();
          return length <= 4 ? number : number.substring(length - 4);
        }
      }
    }
    return null;
  }
}

class UserMobileLastFourDigitsFetcher implements Fetcher {
  @Override
  public String fetch(long appId) {
    Application app = Api.getApplication(appId);
    if (app != null) {
      User user = Api.getUser(app.getUserId());
      if (user != null) {
        Contact contact = Api.getContact(user.getContactId());
        if (contact != null) {
          String number = contact.getNumber();
          if (number != null) {
            int length = number.length();
            return length <= 4 ? number : number.substring(length - 4);
          }
        }
      }
    }
    return null;
  }
}

public class TraditionFetchers {
  public static void main(String[] args) {
    Map<String, Fetcher> fetchers = new HashMap<>();
    fetchers.put("Username", new UsernameFetcher());
    fetchers.put("IdNumberLastFourDigits", new IdNumberLastFourDigitsFetcher());
    fetchers.put("UserMobileLastFourDigits", new UserMobileLastFourDigitsFetcher());

    long appId = 1234L;
    String key = "Username";
    System.out.println(fetchers.get(key).fetch(appId));
  }
}
