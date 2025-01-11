package org.example.gongiklifeclientbeauthservice.controller;

import org.example.gongiklifeclientbeauthservice.service.AuthService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  
}
