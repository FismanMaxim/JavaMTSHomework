package org.example.UserStorage;

import org.example.CustomExceptions.UserNotFoundException;
import org.example.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UsersMsisdnContainer implements UserRepository {

  private final ConcurrentMap<String, User> map;

  public UsersMsisdnContainer() {
    map = new ConcurrentHashMap<>();
  }
  public UsersMsisdnContainer(Map<String, User> map) {
    this.map = new ConcurrentHashMap<>(map) {
    };
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
