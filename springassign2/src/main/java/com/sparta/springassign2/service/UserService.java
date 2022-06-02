package com.sparta.springassign2.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.sparta.springassign2.dto.SignupRequestDto;
import com.sparta.springassign2.model.Users;
import com.sparta.springassign2.repository.UserRepository;
import com.sparta.springassign2.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String SECRETKEY = "sad@!$SDFASFge@!%";

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        // 회원 ID 중복 확인
        Optional<Users> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            return "중복된 사용자 ID 가 존재합니다.";
        }

        if(!Pattern.matches("^([a-zA-Z0-9]{3,9})$", username)){
            return "최소 3자 이상,9자 이하 알파벳 대소문자(a~z, A~Z), 숫자(0~9)로 해야합니다";
        }

        //비밀번호 확인절차
        String ps= requestDto.getPassword();
        String psC=requestDto.getPasswordCheck();

        if(ps.length()<4){
            return "비밀번호는 4글자이상이어야합니다";
        }

        if(ps.equals(username)){
            return "아이디와같으면안됩니다";
        }

        if(!ps.equals(psC)){
            return "비밀번호가 일치하지않습니다";
        }

// 패스워드 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());

// 사용자 ROLE 확인


        Users user = new Users(username,password);
        userRepository.save(user);
        return "회원가입완료!";
    }

    public String jwt(UserDetailsImpl userDetails) {

        String token = null;
        try {
            token = JWT.create()
                    .withIssuer("sparta")
                    .withClaim("USER_NAME", userDetails.getUsername())
                    // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim("EXPIRED_DATE", new Date(System.currentTimeMillis() + 1 * 60 * 60 * 24 * 3 * 1000))
                    .sign(Algorithm.HMAC256(SECRETKEY));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;
    }

    public String decodeUsername(String token) {
        DecodedJWT decodedJWT = isValidToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효한 토큰이 아닙니다."));

        Date expiredDate = decodedJWT
                .getClaim("EXPIRED_DATE")
                .asDate();

        Date now = new Date();
        if (expiredDate.before(now)) {
            throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
        }

        String username = decodedJWT
                .getClaim("USER_NAME")
                .asString();

        return username;
    }

    private Optional<DecodedJWT> isValidToken(String token) {
        DecodedJWT jwt = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRETKEY);
            JWTVerifier verifier = JWT
                    .require(algorithm)
                    .build();

            jwt = verifier.verify(token);
        } catch (Exception e) {

        }

        return Optional.ofNullable(jwt);
    }

}

