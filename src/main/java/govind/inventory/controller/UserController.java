package govind.inventory.controller;

import govind.inventory.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	@Autowired
	private UserDao userDao;

	@GetMapping("/user")
	public Object getUsers() {
		return userDao.findAll();
	}
}
