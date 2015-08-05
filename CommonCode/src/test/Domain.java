package test;

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
