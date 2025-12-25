package com.quora.quora.controller;

import com.quora.quora.dto.RegisterDTO;
import com.quora.quora.model.User;
import com.quora.quora.repository.UserRepository;

import jakarta.servlet.http.HttpSession;   // <-- add this import
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String root() {
        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new RegisterDTO());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("user") RegisterDTO registerDTO,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }

        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.user", "An account with this email already exists");
            return "register";
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
    
        userRepository.save(user);

        return "redirect:/login?registered";
    }

    // 🔴 ADD THIS METHOD
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        // remove everything stored in session
        session.invalidate();
        // after logout, go to login page
        return "redirect:/login?logout";
    }
}
