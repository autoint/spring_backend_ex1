package thales.spring.angular.demo.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.repositories.ClientRepository;

@Service
public class ClientService {
	@Autowired
	private ClientRepository repository;
        
	public List<Client> findAll() {
		return repository.findAll();
	}

	public Optional<Client> findById(Long id) {
		return repository.findById(id);
	}
	
	public boolean isExist(Long id) {
		return findById(id).isPresent();
	}

	public void deleteById(Long id) {
		repository.deleteById(id);		
	}
	
	public Client update(Client client) {
		return repository.save(client);		
	}
	
	public List<Client> updateAll(Set<Client> clients) {
		return repository.saveAll(clients);		
	}
	
	public List<Client> findByAdvisor(Long idAdvisor) {
		return repository.findByAdvisor(idAdvisor);
	}
}
