package mg.itu.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_password", nullable = false)
    private String userPassword;

    @Column(name = "user_birthday", nullable = false)
    private LocalDate userBirthday;

    @Column(name = "token_last_used_at")
    private LocalDateTime tokenLastUsedAt;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @Column
    private String token;

    @Column(name = "email_verification_code")
    private String emailVerificationCode;

    @Column(name = "verification_code_expires_at")
    private LocalDateTime verificationCodeExpiresAt;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "login_attempts")
    private Integer loginAttempts = 0;

    @Column(name = "last_login_attempt_at")
    private LocalDateTime lastLoginAttemptAt;

    @Column(name = "reset_attempts_token")
    private String resetAttemptsToken;

    @Column(name = "reset_attempts_token_expires_at")
    private LocalDateTime resetAttemptsTokenExpiresAt;

    @Column(name = "verification_attempts")
    private Integer verificationAttempts = 0;

    @Column(name = "last_verification_attempt_at")
    private LocalDateTime lastVerificationAttemptAt;

    @Column(name = "reset_verification_attempts_token")
    private String resetVerificationAttemptsToken;

    @Column(name = "reset_verification_attempts_token_expires_at")
    private LocalDateTime resetVerificationAttemptsTokenExpiresAt;

    @Column
    private String role;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public LocalDate getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(LocalDate userBirthday) {
        this.userBirthday = userBirthday;
    }

    public LocalDateTime getTokenLastUsedAt() {
        return tokenLastUsedAt;
    }

    public void setTokenLastUsedAt(LocalDateTime tokenLastUsedAt) {
        this.tokenLastUsedAt = tokenLastUsedAt;
    }

    public LocalDateTime getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmailVerificationCode() {
        return emailVerificationCode;
    }

    public void setEmailVerificationCode(String emailVerificationCode) {
        this.emailVerificationCode = emailVerificationCode;
    }

    public LocalDateTime getVerificationCodeExpiresAt() {
        return verificationCodeExpiresAt;
    }

    public void setVerificationCodeExpiresAt(LocalDateTime verificationCodeExpiresAt) {
        this.verificationCodeExpiresAt = verificationCodeExpiresAt;
    }

    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public Integer getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(Integer loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public LocalDateTime getLastLoginAttemptAt() {
        return lastLoginAttemptAt;
    }

    public void setLastLoginAttemptAt(LocalDateTime lastLoginAttemptAt) {
        this.lastLoginAttemptAt = lastLoginAttemptAt;
    }

    public String getResetAttemptsToken() {
        return resetAttemptsToken;
    }

    public void setResetAttemptsToken(String resetAttemptsToken) {
        this.resetAttemptsToken = resetAttemptsToken;
    }

    public LocalDateTime getResetAttemptsTokenExpiresAt() {
        return resetAttemptsTokenExpiresAt;
    }

    public void setResetAttemptsTokenExpiresAt(LocalDateTime resetAttemptsTokenExpiresAt) {
        this.resetAttemptsTokenExpiresAt = resetAttemptsTokenExpiresAt;
    }

    public Integer getVerificationAttempts() {
        return verificationAttempts;
    }

    public void setVerificationAttempts(Integer verificationAttempts) {
        this.verificationAttempts = verificationAttempts;
    }

    public LocalDateTime getLastVerificationAttemptAt() {
        return lastVerificationAttemptAt;
    }

    public void setLastVerificationAttemptAt(LocalDateTime lastVerificationAttemptAt) {
        this.lastVerificationAttemptAt = lastVerificationAttemptAt;
    }

    public String getResetVerificationAttemptsToken() {
        return resetVerificationAttemptsToken;
    }

    public void setResetVerificationAttemptsToken(String resetVerificationAttemptsToken) {
        this.resetVerificationAttemptsToken = resetVerificationAttemptsToken;
    }

    public LocalDateTime getResetVerificationAttemptsTokenExpiresAt() {
        return resetVerificationAttemptsTokenExpiresAt;
    }

    public void setResetVerificationAttemptsTokenExpiresAt(LocalDateTime resetVerificationAttemptsTokenExpiresAt) {
        this.resetVerificationAttemptsTokenExpiresAt = resetVerificationAttemptsTokenExpiresAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}