package thales.spring.angular.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountEnum {
	COURANT("Compte Courant",0.),
	LIVRETA("Livret A",0.75),
	PEL("PEL",2.5),
	CEL("CEL",1.25),
	BOOST("Livret Boosté",4.5);
	
	public String name;
	public Double rate;
}
