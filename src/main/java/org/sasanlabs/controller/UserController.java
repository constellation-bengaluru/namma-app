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
import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> login(@RequestBody UserBean user, HttpSession session) {
        if (userService.authenticate(user)) {
            // Store only username in session, not the password
            UserBean sessionUser = new UserBean();
            sessionUser.setUsername(user.getUsername());
            sessionUser.setAuthenticated(true);
            
            session.setAttribute("user", sessionUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("username", user.getUsername());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Invalid credentials");
            
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
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
    public ResponseEntity<?> logout(HttpSession session, SessionStatus status) {
        session.removeAttribute("user");
        status.setComplete();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Logout successful");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint to check if a user is authenticated.
     * 
     * @param session the HTTP session
     * @return a response indicating if the user is authenticated
     */
    @GetMapping("/isAuthenticated")
    public ResponseEntity<?> isAuthenticated(HttpSession session) {
        UserBean user = (UserBean) session.getAttribute("user");
        boolean authenticated = (user != null && user.isAuthenticated());
        
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", authenticated);
        
        if (authenticated) {
            response.put("username", user.getUsername());
        }
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}