package com.iampfac.demo.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iampfac.demo.data.jpa.JpaProxyUserRepository;
import com.iampfac.demo.data.jpa.UserJpaRepository;

@Service
public class DemoJpaService {
	
	@Autowired
	UserJpaRepository userJpaRepository;
	
	@Autowired
	JpaProxyUserRepository proxyUserJpaRepository;

	public UserJpaRepository getUserJpaRepository() {
		return userJpaRepository;
	}

	public JpaProxyUserRepository getProxyUserJpaRepository() {
		return proxyUserJpaRepository;
	}

	
	
}
