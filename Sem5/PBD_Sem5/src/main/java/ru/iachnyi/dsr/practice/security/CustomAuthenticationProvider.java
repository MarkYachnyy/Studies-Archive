package ru.iachnyi.dsr.practice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.iachnyi.dsr.practice.service.UserService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider{

	@Autowired
	private UserService userDetailsService;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		UserDetails fromDB = userDetailsService.loadUserByUsername(username);
		if(!encoder.matches((String) authentication.getCredentials(), fromDB.getPassword())) {
			throw new BadCredentialsException("das");
		}
		return new UsernamePasswordAuthenticationToken(fromDB.getUsername(), fromDB.getPassword(), fromDB.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}

}
