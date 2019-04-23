package thales.spring.angular.demo.repositories;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import thales.spring.angular.demo.domain.Advisor;
import thales.spring.angular.demo.domain.Client;

public class AdvisorRepositoryCustomImpl implements AdvisorRepositoryCustom {

	private static final String SELECT_FROM_ADVISOR_AND_CLIENT_BY_IDS =
			"SELECT ID,FIRSTNAME, LASTNAME, OFFICE, ID as cID,FIRSTNAME as cFIRSTNAME,LASTNAME as cLASTNAME, ADDRESS as cADDRESS"
			+ " FROM ADVISOR as a, CLIENT as c"
			+ " WHERE a.ID = c.ID_ADVISOR and a.ID = %s and c.idClient= %s ;";
	
	private static final String SELECT_FROM_ADVISOR_BY_ID =
			"SELECT ID,FIRSTNAME, LASTNAME, OFFICE FROM ADVISOR WHERE ID = %s ;";


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
	public List<Advisor> getAdvisorByClientId(String idAdvisor, String idClient) {
		LOGGER.info(SELECT_FROM_ADVISOR_AND_CLIENT_BY_IDS);

		List<Advisor> advisors = new ArrayList<Advisor>();

		try (Connection connexion = getDataSource().getConnection();
				Statement statement = connexion.createStatement();) {

			String query = String.format(SELECT_FROM_ADVISOR_AND_CLIENT_BY_IDS, idAdvisor, idClient);
			LOGGER.info(query);

			Map<Long, Advisor> advisorsMap = new HashMap<Long, Advisor>();
			try (ResultSet resultat = statement.executeQuery(query);) {
				while (resultat.next()) {
					Long aID = resultat.getLong("aID");
					LOGGER.debug("ADVISOR TROUVE : " + aID);
					if (!advisorsMap.containsKey(aID)) {
						Advisor advisor = extractAdvisor(resultat);
						advisorsMap.put(aID, advisor);
						advisors.add(advisor);
						LOGGER.debug("\nAJOUT DU ADVISOR DANS LA MAP : " + aID);
					}
					Client client = new Client();
					client.setIdClient(resultat.getLong("cID"));
					client.setFirstName(resultat.getString("cFIRSTNAME"));
					client.setLastName(resultat.getString("cLASTNAME"));
					client.setAddress(resultat.getString("cADDRESS"));
					advisorsMap.get(aID).getClients().add(client);
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Erreur lors de la connexion : " + e.getMessage());
		}

		LOGGER.info("Nombre de conseillers {}", advisors.size());
		return advisors;
	}

	private Advisor extractAdvisor(ResultSet resultat) throws SQLException {
		Advisor advisor = new Advisor();
		advisor.setIdAdvisor(resultat.getLong("ID"));
		advisor.setFirstName(resultat.getString("FIRSTNAME"));
		advisor.setLastName(resultat.getString("LASTNAME"));
		advisor.setOffice(resultat.getString("OFFICE"));
		return advisor;
	}

	@Override
	public List<Advisor> getAdvisorById(String idAdvisor) {
		LOGGER.info(SELECT_FROM_ADVISOR_BY_ID);

		List<Advisor> advisors = new ArrayList<Advisor>();

		try (Connection connexion = getDataSource().getConnection();
				Statement statement = connexion.createStatement();) {

			String query = String.format(SELECT_FROM_ADVISOR_BY_ID, idAdvisor);
			LOGGER.info(query);

			try (ResultSet resultat = statement.executeQuery(query);) {
				while (resultat.next()) {					
					advisors.add(extractAdvisor(resultat));
				}
			}
		} catch (SQLException e) {
			LOGGER.error("Erreur lors de la connexion : " + e.getMessage());
		}

		LOGGER.info("Nombre de conseillers {}", advisors.size());
		return advisors;
	}
}
