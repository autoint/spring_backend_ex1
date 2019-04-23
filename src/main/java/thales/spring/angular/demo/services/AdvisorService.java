package thales.spring.angular.demo.services;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.domain.Advisor;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.repositories.AdvisorRepository;

@Service
public class AdvisorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorService.class);
	
	@Autowired
	private AdvisorRepository repository;
	@Autowired
	private ClientService serviceClient;

	public List<Advisor> findAll() {
		return repository.findAll();
	}

	public Optional<Advisor> findById(Long id) {
		return repository.findById(id);
	}

	public void deleteById(Long id) {
		LOGGER.info("deleteById");
		Optional<Advisor> advisor = findById(id);
		if (advisor.isPresent()) {
			Set<Client> clients = advisor.get().getClients();
			Iterator<Client> iterator = clients.iterator();
			while (iterator.hasNext()) {
				Client client = iterator.next();
				client.setAdvisor(null);

			}
			serviceClient.updateAll(clients);
			repository.deleteById(id);
		}
	}

	public Advisor update(Advisor advisor) {
		return repository.save(advisor);
	}
	
	public List<Advisor> updateAll(List<Advisor> advisors) {
		return repository.saveAll(advisors);
	}
	
	public List<Advisor> getAdvisorByClientId(String idAdvisor, String idClient) {
		return repository.getAdvisorByClientId(idAdvisor, idClient);
	}
	
	public List<Advisor> getAdvisorById(String idAdvisor) {
		List<Advisor> advisors = repository.getAdvisorById(idAdvisor);
		
		for (Advisor advisor : advisors) {
			LOGGER.info("recherche des clients pour l'advisor : {}",advisor.getIdAdvisor());
			List<Client> clients = serviceClient.findByAdvisor(advisor.getIdAdvisor());
			advisor.setClients(clients.stream().collect(Collectors.toSet()));			
		}
		return advisors;
	}
}
