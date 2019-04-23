package thales.spring.angular.demo.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import thales.spring.angular.demo.domain.Message;

@RepositoryRestResource
@CrossOrigin(origins = "http://localhost:4200")
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRead(Boolean read);
}
