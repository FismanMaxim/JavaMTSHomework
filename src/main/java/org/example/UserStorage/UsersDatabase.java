package org.example.UserStorage;

public class UsersDatabase {
  private static UsersMsisdnContainer msisdnBase;

  public static UsersMsisdnContainer getMsisdnBase() {
    return msisdnBase;
  }

  public static void setMsisdnBase(UsersMsisdnContainer msisdnBase) {
    UsersDatabase.msisdnBase = msisdnBase;
  }
}
