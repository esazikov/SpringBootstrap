package springboot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springboot.model.Role;
import springboot.model.User;
import springboot.service.RoleService;
import springboot.service.UserService;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    public String index(Model model, Principal principal) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("principal", userService.getUserByName(principal.getName()));
        return "index";
    }

    @PostMapping("/newuser")
    public String addUser(@ModelAttribute("user") User user,
                          @RequestParam(value = "password") String password,
                          @RequestParam(value = "userRole", required = false) boolean userRole,
                          @RequestParam(value = "adminRole", required = false) boolean adminRole) {
        user.setRoles(getRoles(userRole,adminRole));
        user.setPassword(passwordEncoder.encode(password));
        userService.add(user);
        return "redirect:/admin";
    }

    @PostMapping("/{id}/edit")
    public String update(@ModelAttribute("user") User user,
                         @PathVariable("id") Long id,
                         @RequestParam(value = "password") String password,
                         @RequestParam(value = "userRole", required = false) boolean userRole,
                         @RequestParam(value = "adminRole", required = false) boolean adminRole) {
        String oldPassword = userService.getUserbyId(id).getPassword();
        user.setRoles(getRoles(userRole,adminRole));
        if (!password.equals(oldPassword)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        userService.edit(user);
        return "redirect:/admin";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        userService.deleteUserbyId(id);
        return "redirect:/admin";
    }

    private Set<Role> getRoles(boolean userRole, boolean adminRole) {
        Set<Role> roles = new HashSet<>();
        if (userRole) {
            roles.add(roleService.getRoleByName("ROLE_USER"));
        }
        if (adminRole) {
            roles.add(roleService.getRoleByName("ROLE_ADMIN"));
        }
        return roles;
    }

}
