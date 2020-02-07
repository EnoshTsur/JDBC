package dao;

import entities.User;
import exceptions.AlreadyExistsException;
import exceptions.NotExistsException;

import java.util.List;

public interface UserDAO {

    User getById(long id) throws NotExistsException;

    List<User> getAll();

    List<User> getByFullName(String firstName, String lastName);

    User createUser(User user) throws AlreadyExistsException;
}
