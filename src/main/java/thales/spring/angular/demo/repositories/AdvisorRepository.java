package thales.spring.angular.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import thales.spring.angular.demo.domain.Advisor;

@RepositoryRestResource
@CrossOrigin(origins = "http://localhost:4200")
public interface AdvisorRepository extends JpaRepository<Advisor, Long>, AdvisorRepositoryCustom {
	
	public List<Advisor> getAdvisorByClientId(String idAdvisor,String idClient);
	
	public List<Advisor> getAdvisorById(String idAdvisor);
	
	@Query("SELECT a.idAdvisor FROM Advisor a") 
    public List<Long> findAllIds();
}
