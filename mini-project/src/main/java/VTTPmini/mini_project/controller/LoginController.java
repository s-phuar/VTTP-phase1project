package VTTPmini.mini_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import VTTPmini.mini_project.model.User;
import VTTPmini.mini_project.service.LoginService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class LoginController {
    
    @Autowired
    private LoginService loginSvc;    

    @GetMapping("/error")
    public String errorPage(HttpSession sess, Model model){
        sess.invalidate();
        return "error";
    }


    @GetMapping("/")
    public String landingPage(HttpSession sess, Model model){
        sess.invalidate();
        model.addAttribute("user", new User());
        return "index";
    }

    @GetMapping("/login")
    public String loginUser(HttpSession sess, Model model){
        sess.invalidate();
        model.addAttribute("user", new User());
        return "index";
    }
    @GetMapping("/logout")
    public String logoutUser(HttpSession sess, Model model){
        return "redirect:/login";
    }

    @GetMapping("/createaccount")
    public String createUser(HttpSession sess, Model model){
        sess.invalidate();
        model.addAttribute("user", new User());
        return "create-account";
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("user") User user, BindingResult binding, HttpSession sess, Model model){

        //if email is not in Database
        if(!loginSvc.getAllEmails("credentials").contains(user.getEmail())){
            // System.out.println("user ID does not exist");//debugging
            FieldError err = new FieldError("user", "email", "This ID does not exist, please create an account");
            binding.addError(err);
            model.addAttribute("user", user);
            return "index";
        }
        //if password does not match DB's password
        if(!loginSvc.getPassword("credentials", user.getEmail()).equals(user.getPassword())){ 
            // System.out.println("password is incorrect");//debugging
            FieldError err = new FieldError("user", "password", "The password is incorrect");
            binding.addError(err);
            model.addAttribute("user", user);
            return "index";
        }

        sess.setAttribute("user", user);
        return "redirect:/stocks";
    }

    @PostMapping("/createaccount")
    public String createUser(@Valid @ModelAttribute("user") User user, BindingResult binding, Model model){
        //validation errors in model
        if(binding.hasErrors()){
            model.addAttribute("user", user);
            return "create-account";
        }
        //user ID already exists in DB
        if(loginSvc.getAllEmails("credentials").contains(user.getEmail())){
            // System.out.println("the ID is already in use");//debugging
            FieldError err = new FieldError("user", "email", "This ID is already in use");
            binding.addError(err);
            model.addAttribute("user", user);
            return "create-account";
        }
        //passwords not matching
        if(!user.getPassword().equals(user.getPassword2())){
            // System.out.println("passwords does not match");//debugging
            FieldError err = new FieldError("user", "password2", "The passwords do not match");
            binding.addError(err);
            model.addAttribute("user", user);
            return "create-account";
        }

        //save new user credentials
        loginSvc.saveCredentials(user.getEmail(), user.getPassword());

        return "redirect:/";

    }




}
