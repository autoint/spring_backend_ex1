package thales.spring.angular.demo.services;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.domain.AccountEnum;
import thales.spring.angular.demo.domain.Advisor;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.domain.Role;
import thales.spring.angular.demo.domain.RoleEnum;
import thales.spring.angular.demo.domain.User;
import thales.spring.angular.demo.dto.UserDto;
import thales.spring.angular.demo.exception.UserAlreadyExistException;
import thales.spring.angular.demo.repositories.AccountRepository;
import thales.spring.angular.demo.repositories.AdvisorRepository;
import thales.spring.angular.demo.repositories.RoleRepository;
import thales.spring.angular.demo.repositories.UserRepository;
import thales.spring.angular.demo.util.CryptoConverter2;

@Service
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository repoRole;

	@Autowired
	private AccountRepository repoAccount;

	@Autowired
	private AdvisorRepository repoAdvisor;

	@Autowired
	private ClientService serviceClient;

	public List<User> findAll() {
		return repository.findAll();
	}

	public Optional<User> findById(Long id) {
		return repository.findById(id);
	}

	public User findByEmail(String email) {
		return repository.findByEmail(email);
	}

	public User findByToken(String token) {
		return repository.findByToken(token);
	}

	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	public User update(User user) {
		LOGGER.info(user.toString());
		serviceClient.update(user.getClient());
		addMinimumRoles(user);
		return repository.save(user);
	}

	private void addMinimumRoles(User user) {
		addMinimumRoles(user, null);
	}

	private void addMinimumRoles(User user, UserDto userDto) {
		Role findByRole = repoRole.findByRole(RoleEnum.USER);
		if (userDto != null) {
			Integer roleId = userDto.getRole();
			if (roleId != null) {
				Optional<Role> role = repoRole.findById(Long.valueOf(roleId));
				if (role.isPresent()) {
					findByRole = role.get();
				}
			}
		}
		if (user.getRoles() == null || user.getRoles().isEmpty()) {
			user.setRoles(Arrays.asList(findByRole));
		}
	}

	private void addToken(User user) {
		CryptoConverter2 converter = CryptoConverter2.getInstance();
		String tokenClear = user.getEmail() + "-ForJo";
		String token = converter.convertToDatabaseColumn(tokenClear);
		user.setToken(token);
	}

	public List<User> updateAll(Set<User> users) {
		return repository.saveAll(users);
	}

	public User registerNewUserAccount(final UserDto accountDto) {
		if (emailExists(accountDto.getEmail())) {
			throw new UserAlreadyExistException("There is an account with that email adress: " + accountDto.getEmail());
		}

		Set<Account> accounts = createRandomAccounts();

		Advisor advisor = getRandomAdvisor();

		final User user = new User();
		user.setEmail(accountDto.getEmail());
		user.setPassword(accountDto.getPassword());
		addMinimumRoles(user, accountDto);
		addToken(user);

		final Client client = new Client();
		accounts.forEach(System.out::println);
		accounts.forEach(account -> account.setClient(client));

		client.setFirstName(accountDto.getFirstName());
		client.setLastName(accountDto.getLastName());
		client.setAddress(accountDto.getAddress());
		client.setAccounts(accounts);
		client.setAdvisor(advisor);

		LOGGER.info("DETAIL D'UN CLIENT\n" + client.toString());
		user.setClient(client);
		User userRegistered = update(user);
		repoAccount.saveAll(accounts);
		LOGGER.info("DETAIL DU USER REGISTERED\n" + userRegistered.toString());
		return userRegistered;
	}

	private Set<Account> createRandomAccounts() {
		Set<Account> accounts = new HashSet<Account>();
		SecureRandom rand = new SecureRandom();
		String[] ribs = { "rib1", "rib2", "rib3", "rib4", "rib5", "rib6" };

		for (AccountEnum type : AccountEnum.values()) {
			StringBuffer sb = new StringBuffer(String.valueOf(rand.nextInt(9) + 1));
			for (int i = 0; i < 3; i++) {
				sb.append(rand.nextInt(10));
			}

			Account a = new Account();
			a.setIdAccount(Long.valueOf(sb.toString()));
			if (type.equals(AccountEnum.COURANT)) {
				a.setBalance((rand.nextInt(300000) - rand.nextInt(100000)) / 100);
			} else {
				a.setBalance(rand.nextInt(500000) / 100);
			}
			a.setDocument(ribs[rand.nextInt(ribs.length)] + ".pdf");
			a.setRate(type.getRate());
			a.setType(type.getName());
			accounts.add(a);
		}
		return accounts;
	}

	private List<String> initPDFLists() {
		List<String> pdflist = new ArrayList<String>();

		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		try {
			Resource[] resources = resolver.getResources("/download/*.pdf");
			Arrays.stream(resources).map(Resource::getFilename).forEach(pdflist::add);
		} catch (IOException e) {
			LOGGER.info("initPDFLists\n");
			e.printStackTrace();
		}

		return pdflist;
	}

	private Advisor getRandomAdvisor() {
		List<Long> advisorIds = repoAdvisor.findAllIds();
		return repoAdvisor.findById(advisorIds.get(new SecureRandom().nextInt(advisorIds.size()))).get();
	}

	private boolean emailExists(final String email) {
		return repository.findByEmail(email) != null;
	}
}