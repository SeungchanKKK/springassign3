package com.sparta.springassign2.controller;

import com.sparta.springassign2.model.Memo;
import com.sparta.springassign2.model.Users;
import com.sparta.springassign2.repository.MemoRepository;
import com.sparta.springassign2.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class HomeController {
        private final UserController userController;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails, HttpSession httpSession) {
         String user= (String) httpSession.getAttribute("username");
        if(Optional.ofNullable(user).isPresent()) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("token",userController.jwt(userDetails));
        }
        else {model.addAttribute("username", "guest");
       }
        return "index";
    }
}