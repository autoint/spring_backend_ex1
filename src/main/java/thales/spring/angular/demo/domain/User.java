package thales.spring.angular.demo.domain;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import thales.spring.angular.demo.util.CryptoConverter2;

@Entity
@Table(name="USER")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode
public class User {

	static CryptoConverter2 instance = CryptoConverter2.getInstance();
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUser;
	@NonNull
	private String email;
	@NonNull
	@JsonIgnore
	private String password;
	@NonNull
	@JsonIgnore
	private String token;
        
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_client")
    @JsonIgnore
    private Client client;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @JsonIgnore
    private Collection<Role> roles;
		
	public void setPassword(String value) {
		this.password = instance.convertToDatabaseColumn(value);
	}
	
    @Override
	public String toString() {
	    try {
	        return new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
	    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

    /**
     * Method which return user's client informations
     * @return String client information json format
     */
    public String clientInformations() {
        return this.client.toString();
    }
    
    public Boolean admin() throws InstantiationException, IllegalAccessException {
        for (Role role : this.roles) {
            if (role.getId() == 2) {
                return true;
            }
        }
        return false;
    }
}
