package com.richard.portfolio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final RuntimeInfo runtime;
    @Value("${profile.name}") private String name;
    @Value("${profile.title}") private String title;
    @Value("${profile.experience}") private String experience;
    @Value("${profile.bio}") private String bio;
    @Value("${profile.email:}") private String email;
    @Value("${profile.phone:}") private String phone;
    @Value("${profile.linkedin:}") private String linkedin;
    @Value("${profile.github:}") private String github;

    public HomeController(RuntimeInfo runtime) { this.runtime = runtime; }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("name", name);
        model.addAttribute("title", title);
        model.addAttribute("experience", experience);
        model.addAttribute("bio", bio);
        model.addAttribute("podName", runtime.podName());
        model.addAttribute("nodeName", runtime.nodeName());
        model.addAttribute("version", runtime.version());
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("linkedin", linkedin);
        model.addAttribute("github", github);
        model.addAttribute("hasContact", !email.isBlank() || !phone.isBlank()
                || !linkedin.isBlank() || !github.isBlank());
        return "index";
    }
}
