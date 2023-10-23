package org.example.UserStorage;

import org.example.User;

public interface UserRepository {
  /**
   * Tries to find {@link User} with given msisdn
   * @param msisdn - msisdn with which a user to searched
   * @return user with given msisdn
   * @throws org.example.CustomExceptions.UserNotFoundException if user with given msisdn not found
   */
  User findUserByMsisdn(String msisdn);

  void updateUserByMsisdn(String msisdn, User user);
}
