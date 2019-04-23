package thales.spring.angular.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import thales.spring.angular.demo.domain.Customer;

@RepositoryRestResource
@CrossOrigin(origins = "http://localhost:4200")
public interface CustomerRepository extends JpaRepository<Customer, Long> , CustomerRepositoryCustom {
}
