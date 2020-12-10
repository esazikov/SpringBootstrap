package springboot.service;

import springboot.model.User;

import java.util.List;

public interface UserService {
    void add(User user);
    List<User> getAllUsers();
    void deleteUserbyId(Long id);
    void edit(User user);
    User getUserByName(String name);

    User getUserbyId(Long id);
}
