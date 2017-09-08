package com.realdolmen.course.beans;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.realdolmen.course.domain.User;
import com.realdolmen.course.service.PersonServiceBean;
import com.realdolmen.course.service.UserServiceBean;

/**
 * FormClass for loginScreen
 * @author BSEBF08
 *
 */

@Named
@RequestScoped
public class LoginBean implements Serializable{
	@Inject
	private UserServiceBean userService;
	
	private boolean userNotFound = false;
	
	@Email @NotBlank
	private String email;
	
	@NotBlank @Size(max=200) 
	private String password;
	
	//Constructor
	public LoginBean() {
		super();
	}
	
	//Properties
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String search() {
		userNotFound = false;
		User user = userService.checkUserPassword(email, password);
		if (user != null) {
			System.out.println(user.getEmail() + " aangelogd!");
			return "index";
		} else {
			userNotFound = true;
			return "login";
		}
	}
	
}