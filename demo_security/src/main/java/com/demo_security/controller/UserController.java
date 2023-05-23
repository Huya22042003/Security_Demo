package com.demo_security.controller;

import com.demo_security.entity.CreateUserRequest;
import com.demo_security.entity.LoginRequest;
import com.demo_security.entity.ResetUserRequest;
import com.demo_security.entity.ResponseObject;
import com.demo_security.entity.User;
import com.demo_security.repository.EmployeeRepository;
import com.demo_security.service.TokenService;
import com.demo_security.service.UserService;
import com.demo_security.token.RefreshTokenContants;
import com.demo_security.token.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/register")
    public ResponseObject addNewUser(@RequestBody CreateUserRequest userInfo){
        ResponseObject responseObject = new ResponseObject();
        User user = new User();
        user.setEmail("haha@gmail.com");
        user.setName(userInfo.getName());
        user.setPassword(userInfo.getPassword());
        user.setRoles("ADMIN");
        if (service.addUser(user)) {
            responseObject.setStatus(200);
            responseObject.setMessage("Đăng ký thành công");
        } else {
            responseObject.setStatus(400);
            responseObject.setMessage("Đăng ký thất bại");
        }
        return responseObject;
    }

    @GetMapping("/employee")
    public ResponseObject admin() {
        return new ResponseObject(200, null, repository.findAll()) ;
    }

    @GetMapping("/profile")
    public ResponseObject getProfile() {
        return new ResponseObject(200, null, service.getUserByName(SecurityContextHolder.getContext().getAuthentication().getName())) ;
    }

    @PostMapping("/login")
    public ResponseObject login(@RequestBody LoginRequest loginRequest) {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatus(200);
        try {
            // Xác thực thông tin đăng nhập
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Đăng nhập thành công, tạo JWT
            responseObject.setMessage("Đăng nhập thành công");
            responseObject.setData(tokenService.generateTokens(authentication));
            // Trả về token cho client
            return responseObject;
        } catch (AuthenticationException ex) {
            // Xử lý khi xác thực thất bại
            ex.printStackTrace();
            responseObject.setStatus(400);
            responseObject.setMessage("Đăng nhập thất bại");
            return responseObject;
        }
    }

    @PutMapping("/update-name")
    public ResponseObject resetUserName(@RequestBody ResetUserRequest resetUserRequest,
                                        HttpServletResponse response, HttpServletRequest request) {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatus(200);
        try {
            String accessToken = tokenProvider.resolveToken(request);
            // update lại dữ liệu
            User userNew = service.udpateUserName(resetUserRequest, accessToken);
            // xóa refresh token
            tokenService.invalidateToken();

            if (userNew != null) {
                // thay đổi thông tin trong security
                UsernamePasswordAuthenticationToken newUser = new UsernamePasswordAuthenticationToken(userNew.getName(),
                        userNew.getPassword());
                SecurityContextHolder.getContext().setAuthentication(newUser);
                responseObject.setMessage("Đăng nhập thành công");
                // gen lại access token và refresh token
                responseObject.setData(tokenService.generateTokens(SecurityContextHolder.getContext().getAuthentication()));
            }
            return responseObject;
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            responseObject.setStatus(400);
            responseObject.setMessage("Đăng nhập thất bại");
            return responseObject;
        }
    }

    @PostMapping("/refresh-token")
    public ResponseObject refreshToken() {
//        SecurityContextHolder.getContext().getAuthentication().getName();

        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatus(200);
//        System.out.println();
        if (tokenProvider.validateToken((String) cacheManager
                .getCache(RefreshTokenContants.REFRESH_TOKEN).
                        get(SecurityContextHolder.getContext().getAuthentication().getName()).get())) {
            responseObject.setMessage("Đổi access token thành công");
            String token = tokenProvider.generateToken(SecurityContextHolder.getContext().getAuthentication());
            responseObject.setData(token);
        } else {
            responseObject.setMessage("Refresh token hết hạn");
        }

        return responseObject;
    }

    @GetMapping("/logout")
    public ResponseObject logout() {
        tokenService.invalidateToken();
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatus(200);
        responseObject.setMessage("Đăng xuất thành công");
        return responseObject;
    }

}
