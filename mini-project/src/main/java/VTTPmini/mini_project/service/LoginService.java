package VTTPmini.mini_project.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import VTTPmini.mini_project.repository.LoginRepository;

@Service
public class LoginService {
    
    @Autowired
    private LoginRepository loginRepo;

    public Set<String> getAllEmails(String key){
        return loginRepo.getAllEmails(key);
    }

    public String getPassword(String key, String email){
        return loginRepo.getPassword(key, email);
    }

    public void saveCredentials(String email, String password){
        loginRepo.saveCredentials(email, password);
    }


}
