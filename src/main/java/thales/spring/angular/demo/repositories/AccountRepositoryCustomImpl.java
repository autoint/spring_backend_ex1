package thales.spring.angular.demo.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import thales.spring.angular.demo.domain.Account;

public class AccountRepositoryCustomImpl implements AccountRepositoryCustom {

	private static final String SELECT_FROM_ACCOUNT_BY_ID_CLIENT =
			"SELECT ID, TYPE, BALANCE, RATE, DOCUMENT FROM ACCOUNT WHERE ID_CLIENT= %s;";

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorRepositoryCustomImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Account> getAccountByClientId(String idClient) {
		LOGGER.info(SELECT_FROM_ACCOUNT_BY_ID_CLIENT);

		List<Account> accounts = new ArrayList<Account>();

		try (Connection connexion = getDataSource().getConnection();
				Statement statement = connexion.createStatement();) {

			String query = String.format(SELECT_FROM_ACCOUNT_BY_ID_CLIENT, idClient);
			LOGGER.info(query);

			try (ResultSet resultat = statement.executeQuery(query);) {
				while (resultat.next()) {
					Account account = new Account();
					account.setIdAccount(resultat.getLong("ID"));
					account.setType(resultat.getString("TYPE"));
					account.setBalance(Double.valueOf(resultat.getString("BALANCE")));
					account.setDocument(resultat.getString("DOCUMENT"));
					account.setRate(Double.valueOf(resultat.getString("RATE")));
					accounts.add(account);
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Erreur lors de la connexion : " + e.getMessage());
		}

		LOGGER.info("Nombre de contrats {}", accounts.size());
		return accounts;
	}
}
