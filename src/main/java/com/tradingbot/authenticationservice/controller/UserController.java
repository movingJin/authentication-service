package com.tradingbot.authenticationservice.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tradingbot.authenticationservice.dto.UserDto;
import com.tradingbot.authenticationservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tradingbot.authenticationservice.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/")
public class UserController {

    private final Environment env;
    private final UserService userService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service on PORT %s"
                , env.getProperty("local.server.port"));
    }

    @RequestMapping(value ="/users", method = RequestMethod.POST)
    public ResponseEntity<String> createUser(@RequestBody UserDto requestUser) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(requestUser, UserDto.class);
        User createdUser = userService.createUser(userDto);
        if(createdUser != null){
            return new ResponseEntity<>("success", HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("email is already exists", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<UserDto> getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        String token = bearerToken.replace("Bearer ", "");
        DecodedJWT decodeToken = JWT.decode(token);
        String userId = decodeToken.getSubject();

        UserDto responseUser = userService.getUserByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

    @PostMapping("/user")
    public void updateUser(@RequestBody UserDto requestUser) {

        userService.updateUser(requestUser);
    }

    @GetMapping("/validation")
    public String checkValidationToken() {
        return "Status:OK";
    }
}
