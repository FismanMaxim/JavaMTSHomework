package org.example.UserStorage;

import org.example.CustomExceptions.UserNotFoundException;
import org.example.User;

import java.util.HashMap;
import java.util.Map;

public class UsersMsisdnContainer implements UserRepository {

  private final Map<String, User> map;

  public UsersMsisdnContainer() {
    map = new HashMap<>();
  }
  public UsersMsisdnContainer(Map<String, User> map) {
    this.map = map;
  }

  public User findUserByMsisdn(String msisdn) {
    if (map.containsKey(msisdn))
      return map.get(msisdn);
    else
      throw new UserNotFoundException();
  }

  public void updateUserByMsisdn(String msisdn, User user) {
    map.put(msisdn, user);
  }
}
