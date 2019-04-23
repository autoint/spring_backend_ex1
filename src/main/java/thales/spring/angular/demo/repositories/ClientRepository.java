package thales.spring.angular.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import thales.spring.angular.demo.domain.Client;

@RepositoryRestResource
@CrossOrigin(origins = "http://localhost:4200")
public interface ClientRepository extends JpaRepository<Client, Long> {

	@Query("SELECT c FROM Client c WHERE c.advisor.idAdvisor = :idAdvisor")
	public List<Client> findByAdvisor(@Param("idAdvisor") Long idAdvisor);
	
	@Query("SELECT t.idClient FROM Client t") 
    public List<Long> findAllIds();
}
