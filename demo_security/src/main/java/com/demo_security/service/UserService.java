package com.demo_security.service;

import com.demo_security.entity.ResetUserRequest;
import com.demo_security.entity.User;
import com.demo_security.repository.UserInfoRepository;
import com.demo_security.token.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenProvider tokenProvider;

    public User getUserByName(String userName) {
        Optional<User> optionalUser = repository.findByName(userName);
        if (!optionalUser.isEmpty()) {
            return optionalUser.get();
        }
        return null;
    }

    public boolean addUser(User userInfo) {
        try {
            userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
            repository.save(userInfo);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public User udpateUserName(ResetUserRequest userName, String accessToken) {
        User userNew = new User();
        try {
            String userNameOld = tokenProvider.getUsernameFromToken(accessToken);
            Optional<User> userOptional = repository.findByName(userNameOld);
            if (!userOptional.isEmpty()) {
                userNew = userOptional.get();
                userNew.setName(userName.getUserName());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return repository.save(userNew);
    }

    public List<User> getList() {
        return repository.findAll();
    }
}
