package VTTPmini.mini_project.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class User {
    //8 characters+ 1upper and lower case + 1 digit, 1 special, no whitespace
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @NotBlank (message="Please enter your email")
    @Email (message="Please enter a valid email")
    private String email;
    @NotBlank (message="Please enter your password")
    @Pattern (regexp =PASSWORD_PATTERN, message = "Password must be at least 8 characters long, contain both upper and lowercase letters, include at least one digit and one special character.")
    private String password;
    private String password2;

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public String getPassword2() {return password2;}
    public void setPassword2(String password2) {this.password2 = password2;}
    
    




}
