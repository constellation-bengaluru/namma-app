package org.sasanlabs.controller;

import org.sasanlabs.beans.UserBean;
import org.sasanlabs.service.vulnerability.auth.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpSession;

/**
 * Controller for handling user authentication.
 * 
 * @author copilot
 */
@RestController
@SessionAttributes("user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint for user login.
     * 
     * @param user the user credentials
     * @param session the HTTP session
     * @return a response indicating success or failure
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserBean user, HttpSession session) {
        if (userService.authenticate(user)) {
            session.setAttribute("user", user);
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Endpoint for user logout.
     * 
     * @param session the HTTP session
     * @param status the session status
     * @return a response indicating success
     */
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session, SessionStatus status) {
        session.removeAttribute("user");
        status.setComplete();
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }

    /**
     * Endpoint to check if a user is authenticated.
     * 
     * @param session the HTTP session
     * @return a response indicating if the user is authenticated
     */
    @GetMapping("/isAuthenticated")
    public ResponseEntity<Boolean> isAuthenticated(HttpSession session) {
        UserBean user = (UserBean) session.getAttribute("user");
        return new ResponseEntity<>(user != null && user.isAuthenticated(), HttpStatus.OK);
    }
}