package thales.spring.angular.demo.repositories;

import java.util.List;

import thales.spring.angular.demo.domain.Account;

public interface AccountRepositoryCustom {
	public List<Account> getAccountByClientId(String idClient);
}
