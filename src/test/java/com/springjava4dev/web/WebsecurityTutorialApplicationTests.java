package com.springjava4dev.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebsecurityTutorialApplication.class)
@WebAppConfiguration
public class WebsecurityTutorialApplicationTests {

	@Test
	public void contextLoads() {
	}

}
