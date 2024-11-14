package com.hnue.commerce.controller;

import com.hnue.commerce.dto.*;
import com.hnue.commerce.model.User;
import com.hnue.commerce.service.EmailService;
import com.hnue.commerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/account")
    public String account(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "account";
    }

    @GetMapping("/register")
    public String register(Model theModel){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        theModel.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/process-register")
    public String processRegister(@Valid @ModelAttribute("user") User theUser,
                           BindingResult result){
        if (result.hasErrors() || userService.isEmail(theUser.getEmail())){
            if (userService.isEmail(theUser.getEmail())){
                result.rejectValue("email", "error.user", "Email đã tồn tại");
            }
            return "register";
        }else{
            theUser.setRole("ROLE_user");
            userService.addUser(theUser);
            return "redirect:/account";
        }
    }

    @GetMapping("/pass")
    public String showChangePasswordForm(Model theModel){
        theModel.addAttribute("password", new ChangPasswordDTO());
        return "password";
    }

    @PostMapping("/pass")
    public String changePassword(@Valid @ModelAttribute("password") ChangPasswordDTO changPasswordDTO,
                                 BindingResult result, Model theModel){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(changPasswordDTO.getOldPassword(), userDetails.getPassword())) {
            result.rejectValue("oldPassword", "error.oldPassword", "Mật khẩu cũ không đúng");
        }else{
            if (changPasswordDTO.getOldPassword().equals(changPasswordDTO.getNewPassword())){
                result.rejectValue("oldPassword", "error.oldPassword", "Mật khẩu cũ và mật khẩu mới không được giống nhau");
            }
        }
        if (!changPasswordDTO.getNewPassword().equals(changPasswordDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu không giống nhau");
        }
        if (result.hasErrors()){
            return "password";
        }
        User user = userService.getUserByEmail(email);
        user.setPassword(passwordEncoder.encode(changPasswordDTO.getNewPassword()));
        userService.updateUser(user);
        return "redirect:/";
    }

    @GetMapping("/forgot")
    public String showForgotPassword(Model theModel){
        theModel.addAttribute("email", new SendEmail());
        return "email";
    }

    @PostMapping("/forgot")
    public String forgotPassword(@Valid @ModelAttribute("email") SendEmail sendEmail,
                                 BindingResult result, Model theModel, HttpSession session){
        if (sendEmail.getEmail() != null){
            if (!userService.isEmail(sendEmail.getEmail())){
                result.rejectValue("email", "error.email", "Không tồn tại email này");
            }
        }
        if (result.hasErrors()){
            return "email";
        }
        String otpCode = emailService.sendOtpEmail(sendEmail.getEmail());
        session.setAttribute("otp", new Otp(sendEmail.getEmail(), otpCode, LocalDateTime.now().plusMinutes(5)));
        return "otp";
    }

    @PostMapping("/otp")
    public String otp(@RequestParam(value = "otp", required = false) String otp, HttpSession session, Model model){
        Otp o = (Otp) session.getAttribute("otp");
        if (otp == null || otp.isBlank()) {
            model.addAttribute("error", "Mã OTP không để trống");
            return "otp";
        } else if (!otp.equals(o.getOtp())) {
            model.addAttribute("error", "Mã OTP không chính xác");
            return "otp";
        } else if (o.isExpired()) {
            model.addAttribute("error", "Mã OTP đã hết hạn");
            return "otp";
        }
        session.setAttribute("authenticated", true);
        return "redirect:/reset";
    }

    @GetMapping("/reset")
    public String showRestPassword(Model theModel, HttpSession session, @ModelAttribute("email") String email) {
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/forgot";
        }
        theModel.addAttribute("reset", new ResetPassword());
        theModel.addAttribute("email", email);
        return "reset";
    }

    @PostMapping("/reset")
    public String resetPassword(@Valid @ModelAttribute("reset") ResetPassword password,
                                BindingResult result, HttpSession session){
        if (!password.getNewPassword().equals(password.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu không giống nhau");
        }
        if (result.hasErrors()){
            return "reset";
        }
        Otp otp = (Otp) session.getAttribute("otp");
        session.removeAttribute("authenticated");
        session.removeAttribute("otp");
        User user = userService.getUserByEmail(otp.getEmail());
        user.setPassword(passwordEncoder.encode(password.getNewPassword()));
        userService.updateUser(user);
        return "redirect:/account";
    }

    @GetMapping("/access-denied")
    public String accessDenied(){
        return "redirect:/";
    }

    @GetMapping("/admin/users")
    public String userForAdmin(Model theModel){
        theModel.addAttribute("users", userService.getAllUsers());
        return "admin/user";
    }

    @GetMapping("/admin/users/create")
    public String createUserForAdmin(Model theModel){
        UserDTO userDTO = new UserDTO();
        theModel.addAttribute("userDTO", userDTO);
        theModel.addAttribute("actionUrl", "/admin/users/create");
        return "admin/user-form";
    }

    @PostMapping("/admin/users/create")
    public String processCreateUserForAdmin(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                                            BindingResult result, Model theModel){
        if (userDTO.getId() == 0 && (userDTO.getPassword() == null || userDTO.getPassword().isBlank())) {
            result.rejectValue("password", "error.password", "Không được để trống mật khẩu");
        }
        if (result.hasErrors()){
            theModel.addAttribute("userDTO", userDTO);
            theModel.addAttribute("actionUrl", "/admin/users/create");
            return "admin/user-form";
        }
        User user = new User(userDTO.getEmail(), userDTO.getPassword(),
                userDTO.getFullName(), userDTO.getRole(), userDTO.getAddress(), userDTO.getPhone(), new Date());
        userService.addUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/{id}")
    public String updateUserForAdmin(@PathVariable("id") int id, Model theModel){
        User user = userService.getUser(id);
        UserDTO userDTO = UserDTO.builder()
                .email(user.getEmail()).fullName(user.getFullName())
                .phone(user.getPhoneNumber()).role(user.getRole())
                .address(user.getAddress()).id(user.getId())
                .build();

        theModel.addAttribute("userDTO", userDTO);
        theModel.addAttribute("actionUrl", "/admin/users/"+id);

        return "admin/user-form";
    }

    @PostMapping("/admin/users/{id}")
    public String processUpdateUser(@PathVariable("id") int id, @Valid @ModelAttribute("userDTO") UserDTO userDTO,
                                    BindingResult result, Model theModel){
        if (userDTO.getId() == 0 && (userDTO.getPassword() == null || userDTO.getPassword().isBlank())) {
            result.rejectValue("password", "error.password", "Không được để trống mật khẩu");
        }
        if (result.hasErrors()){
            theModel.addAttribute("userDTO", userDTO);
            theModel.addAttribute("actionUrl", "/admin/users/"+id);
            return "admin/user-form";
        }
        User user = userService.getUser(id);
        user.setFullName(userDTO.getFullName());
        user.setRole(userDTO.getRole());
        user.setAddress(userDTO.getAddress());
        user.setPhoneNumber(userDTO.getPhone());
        if (!(userDTO.getPassword() == null || userDTO.getPassword().isBlank())){
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userService.updateUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable("id") int id){
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
