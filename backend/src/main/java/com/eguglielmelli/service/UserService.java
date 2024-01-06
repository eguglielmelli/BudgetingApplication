package com.eguglielmelli.service;
import com.eguglielmelli.models.Account;
import com.eguglielmelli.models.Category;
import com.eguglielmelli.repositories.AccountRepository;
import com.eguglielmelli.repositories.CategoryRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.text.WordUtils;
import com.eguglielmelli.models.User;
import com.eguglielmelli.repositories.UserRepository;
import org.apache.commons.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.validator.routines.EmailValidator;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Autowired
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder, AccountRepository accountRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public User createUser(User newUser) {
        if(newUser == null) throw new IllegalArgumentException("User cannot be null");

        if(newUser.getName() == null || newUser.getName().isEmpty() || newUser.getEmail() == null || newUser.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Name and email cannot be null.");
        }
        if(!EmailValidator.getInstance().isValid(newUser.getEmail())) {
            throw new IllegalArgumentException("Please enter a valid email.");
        }

        if(userRepository.findByEmailAndIsDeletedFalse(newUser.getEmail()).isPresent()) {
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

        User foundUser = userRepository.findByEmailAndIsDeletedFalse(user.getEmail()).orElse(null);
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

        user.setDeleted(true);
        userRepository.save(user);
    }

    private String generateJwtToken(User user) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        long expMillis = nowMillis + 3600000; // 1 hour token validity
        Date exp = new Date(expMillis);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("userId", user.getUserId()); // Include userId in the token claims

        return Jwts.builder()
                .setClaims(claims) // Set the claims
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public List<Account> getAllAccountsForUser(Long userId) {
        return accountRepository.findByUserUserId(userId);

    }

    public List<Category> getAllCategoriesForUser(Long userId) {
        return categoryRepository.findByUserUserId(userId);
    }


}
