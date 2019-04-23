package thales.spring.angular.demo.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.domain.Message;
import thales.spring.angular.demo.repositories.MessageRepository;

@Service
public class MessageService {
private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
	
	@Autowired
	private MessageRepository repository;

	public List<Message> findAll() {
		return repository.findAll();
	}

	public Optional<Message> findById(Long id) {
		return repository.findById(id);
	}

	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	public Message update(Message message) {
		return repository.save(message);
	}

	public List<Message> updateAll(List<Message> messages) {
		return repository.saveAll(messages);
	}

	public List<Message> findByRead(Boolean read) {
		return repository.findByRead(read);
	}
}
