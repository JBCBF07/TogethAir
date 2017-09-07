package com.realdolmen.course.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.realdolmen.course.enums.BudgetClass;

/**
 * These are the flights provided by the companies. The companies will provide this data, 
 * including a baseprice (in Price) and VolumeDiscounts.
 * TogethAir sells them with profit. 
 * 
 * @author BSEBF08
 *
 */
@Entity
public class Flight implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = true, length = 15)
	@NotBlank @Size(max = 15)
	private String name;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date departureTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date arrivalTime;
	
	@ElementCollection
	@CollectionTable(name = "availableSeatsPerBudgetClass")
	@MapKeyColumn(name = "budgetClass")
	@Column(name  = "available")
	private Map<BudgetClass, Integer> availableSeats = new HashMap<>();
	
	@ElementCollection
	@CollectionTable (name = "pricePerBudgetClass")
	@MapKeyColumn(name = "budgetClass")
	//@Column(name = "prices_id")
	private Map<BudgetClass, Price> prices = new HashMap<>();
	
	@ElementCollection
	@CollectionTable(name = "discountPerVolume")
	private Set<VolumeDiscount> volumeDiscounts = new TreeSet();
	
	@ManyToOne (fetch = FetchType.LAZY)
	private Company company;
	
	@Version
	private Integer version;
	
	//Constructors
	public Flight() {
	}

	public Flight(String name, Date departureTime, Date arrivalTime, Company company) {
		super();
		this.name = name;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.company = company;
	}

	//Properties
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public Map<BudgetClass, Integer> getAvailableSeats() {
		return Collections.unmodifiableMap(availableSeats);
	}

	public Integer getVersion() {
		return version;
	}


	/**
	 * To set the available seats per BudgetClass. If availableSeats is already present, this method 
	 * will overwrite this value!
	 * @param budgetClass
	 * @param count
	 */
	public void setAvailableSeatsPerBudgetClass(BudgetClass budgetClass, int count) {
		this.availableSeats.put(budgetClass, count) ;
	}
	
	/**
	 * To book a number of seats in a certain budgetClass. This is an enumeration of possible
	 * tickets (BudgetClass.ECONOMY, ...)
	 * @param budgetClass
	 * @param count
	 */
	public void bookSeats(BudgetClass budgetClass, int count) {
		this.availableSeats.put(budgetClass, availableSeats.get(budgetClass) - count);
	}
	/**
	 * To set the price per BudgetClass (cfr. BudgetClass.ECONOMY). If the price is already present
	 * for this budgetclass, this method will overwrite this value!
	 * @param budgetClass
	 * @param price
	 */
	public void setPricePerBudgetClass(BudgetClass budgetClass, Price price) {
		this.prices.put(budgetClass, price);
	}


	public Map<BudgetClass,Price> getPrices() {
		return prices;
	}
}
