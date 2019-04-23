package thales.spring.angular.demo.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name="CUSTOMER")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode()
@XmlRootElement
public class Customer {
	
	@Id 
    private Long idCustomer;
    private @NonNull String customerName;
    private @NonNull String customerAddress;
    
    @Override
	public String toString() {
	    try {
	        return new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
	    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

}
