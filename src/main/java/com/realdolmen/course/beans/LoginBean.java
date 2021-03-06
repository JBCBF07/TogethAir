package com.realdolmen.course.beans;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.realdolmen.course.domain.User;
import com.realdolmen.course.service.UserServiceBean;

/**
 * FormClass for loginScreen
 * @author BSEBF08
 *
 */

@Named
@RequestScoped
public class LoginBean implements Serializable{
	@EJB
	private UserServiceBean userService;

	@Inject
	private SearchFlightsBean searchFlightsBean;

	@Inject
	private BookingBean bookingBean;

	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Inject
	private LoggedInBean loggedInBean;
	
	private boolean userNotFound = false;
	
	@Email (message = "{req.email}")
	@NotBlank (message = "{req.email}")
	private String email;
	
	@NotBlank (message = "{req.password}")
	@Size(max = 200, message = "{siz.password}")
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
	
	public boolean isUserNotFound() {
		return userNotFound;
	}

	public void setUserNotFound(boolean userNotFound) {
		this.userNotFound = userNotFound;
	}

	/**
	 * Log the user in if the user is found and the password is correct
	 * @return
	 */
	public String loginUser() {
		userNotFound = false;
		if (userService.isUserPasswordCorrect(email, password)) {
			user = userService.findByEmail(email);
			loggedInBean.setUser(user);
			if (bookingBean.getBooking() != null){
				return "booking";
			}
			if (bookingBean.getPassengers() != null && bookingBean.getPassengers().size() > 0){
				return "inputPassengers";
			} else {
				return "index";
			}
		} else {
			userNotFound = true;
			return "login";
		}
	}

	/**
	 * Navigate to userregistration.xhtml
	 * @return
	 */
	public String newUser() {
		return "userregistration";
	}
}
