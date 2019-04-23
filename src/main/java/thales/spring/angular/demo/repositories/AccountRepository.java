package thales.spring.angular.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import thales.spring.angular.demo.domain.Account;

@RepositoryRestResource
@CrossOrigin(origins = "http://localhost:4200")
public interface AccountRepository extends JpaRepository<Account, Long> , AccountRepositoryCustom {
	public List<Account> getAccountByClientId(String idClient);
	
	@Query("SELECT a.idAccount FROM Account a") 
    public List<Long> findAllIds();
}
