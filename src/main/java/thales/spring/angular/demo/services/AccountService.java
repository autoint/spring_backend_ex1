package thales.spring.angular.demo.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.repositories.AccountRepository;

@Service
public class AccountService {
private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
	
	@Autowired
	private AccountRepository repository;

	public List<Account> findAll() {
		return repository.findAll();
	}

	public Optional<Account> findById(Long id) {
		return repository.findById(id);
	}
	
	public boolean isExist(Long id) {
		return findById(id).isPresent();
	}

	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	public Account update(Account account) {
		return repository.save(account);
	}
	
	public List<Account> updateAll(List<Account> accounts) {
		return repository.saveAll(accounts);
	}
	
	public List<Account> getAccountByClientId(String idClient) {
		return repository.getAccountByClientId(idClient);		
	}
}
