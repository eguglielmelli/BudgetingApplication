package com.eguglielmelli.service;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.text.WordUtils;
import com.eguglielmelli.models.User;
import com.eguglielmelli.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(User newUser) {
        if(newUser == null) throw new IllegalArgumentException("User cannot be null");

        if(newUser.getName() == null || newUser.getName().isEmpty() || newUser.getEmail() == null || newUser.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Name and email cannot be null.");
        }

        if(userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new IllegalArgumentException("An account is already associated with this email.");
        }

        newUser.setName(WordUtils.capitalizeFully(newUser.getName()));

        String encryptedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encryptedPassword);

        return userRepository.save(newUser);
    }

    public String authenticateUser(User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User email and password cannot be null.");
        }

        User foundUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        if (foundUser != null && passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            return generateJwtToken(foundUser);
        }
        return null;
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    private String generateJwtToken(User user) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        long expMillis = nowMillis + 3600000;
        Date exp = new Date(expMillis);


        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());


        return Jwts.builder()
                .setSubject(user.getEmail())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }


}
