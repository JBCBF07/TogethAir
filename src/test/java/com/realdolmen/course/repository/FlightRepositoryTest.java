package com.realdolmen.course.repository;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.*;

import com.realdolmen.course.domain.*;
import org.junit.Before;
import org.junit.Test;

import com.realdolmen.course.AbstractPersistenceTest;
import com.realdolmen.course.enums.BudgetClass;
import com.realdolmen.course.utils.DateUtils;

public class FlightRepositoryTest extends AbstractPersistenceTest{
    private static final long TEST_FLIGHT_ID = 2L;

	private FlightRepository flightRepository;
	
	@Before
	public void initializeRepository() {
		flightRepository = new FlightRepository();
		flightRepository.em = em;
	}

	/**
	 * Save a Flight, with prices and availableSeats. Afterwards, retrieve this Flight,
	 * and check if the values are correct.
	 */
	@Test
    public void shouldSaveAndRetrieveAFlight() {
		Map<BudgetClass, Integer> budgetClassIntegerMap = new HashMap<>();
		Map<BudgetClass, Price> budgetClassPriceMap = new HashMap<>();
		Map<Integer, BigDecimal> volumeDiscounts = new HashMap();
		Flight flight = new Flight("AH47", DateUtils.createDate("2016-01-01 00:00"),
				DateUtils.createDate("2016-01-01 00:00"), budgetClassIntegerMap, budgetClassPriceMap, volumeDiscounts,
				new Company("Lufthansa", "Test"),
				new Airport(), new Airport());
		Price price = new Price();
	    price.setBase(BigDecimal.valueOf(126.32));
	    price.setProfitPercentage(BigDecimal.valueOf(6));
	    flight.setPricePerBudgetClass(BudgetClass.ECONOMY, price);
	    price = new Price();
	    price.setBase(BigDecimal.valueOf(126.32));
	    price.setFixBonus(BigDecimal.valueOf(55));
	    flight.setPricePerBudgetClass(BudgetClass.BUSINESS, price);
	    flight.setAvailableSeatsPerBudgetClass(BudgetClass.ECONOMY, 25);
	    flight.setAvailableSeatsPerBudgetClass(BudgetClass.BUSINESS, 5);
		flight = flightRepository.save(flight);
		Flight savedFlight = flightRepository.findById(flight.getId());
		
		assertNotNull("FlightId is not supposed to be null after saving", savedFlight.getId());
		assertTrue(BigDecimal.valueOf(126.32 * 1.06) 
				.compareTo(savedFlight.getPrices().get(BudgetClass.ECONOMY).calculatePrice()) == 0);
		assertTrue(BigDecimal.valueOf(126.32 + 55) 
				.compareTo(savedFlight.getPrices().get(BudgetClass.BUSINESS).calculatePrice()) == 0);
		assertEquals((Integer)5, savedFlight.getAvailableSeats().get(BudgetClass.BUSINESS));
		assertEquals((Integer)25, savedFlight.getAvailableSeats().get(BudgetClass.ECONOMY));
    }	
	@Test
	public void updateAFlight() {
		Flight flight = flightRepository.findById(TEST_FLIGHT_ID);
		flight.setAvailableSeatsPerBudgetClass(BudgetClass.ECONOMY, 13);
		flight.setArrivalTime(DateUtils.createDate("2014-01-01 00:00"));
		Price price = flight.getPrices().get(BudgetClass.BUSINESS);
		price.setBase(BigDecimal.valueOf(100));
		price.setFixBonus(BigDecimal.valueOf(356));
		flight.setPricePerBudgetClass(BudgetClass.BUSINESS, price);
		flight = flightRepository.save(flight);
		Flight flightUpdated = flightRepository.findById(TEST_FLIGHT_ID);
		assertEquals((Integer)13, flightUpdated.getAvailableSeats().get(BudgetClass.ECONOMY));
		assertTrue(BigDecimal.valueOf(100 + 356) 
				.compareTo(flightUpdated.getPrices().get(BudgetClass.BUSINESS).calculatePrice()) == 0);
		assertEquals((Long)2L, flightUpdated.getId());
	}
	@Test
	public void shouldReturnAllFlights() {
		List<Flight> flights = flightRepository.findAll();
		assertNotNull(flights);
		assertEquals(10, flights.size());
	}
	@Test
	public void shouldReturnAFlight() {
		Flight flight = flightRepository.findById(TEST_FLIGHT_ID);
		assertNotNull(flight);
		assertNotNull(flight.getId());
		assertEquals("AB17", flight.getName());
	}
	@Test
	public void shouldGetAvailableSeatsInEconomyClass() {
		Flight flight = flightRepository.findById(2L);
		Integer availableEconomy = flight.getAvailableSeats().get(BudgetClass.ECONOMY);
		assertEquals((Integer)30, availableEconomy);
	}
	@Test
	public void shouldGetAvailableSeatsInFirstClass() {
		Flight flight = flightRepository.findById(2L);
		flight.bookSeats(BudgetClass.FIRST_CLASS, 15); //originally 20, becomes 5
		flight = flightRepository.save(flight);
		em.flush();
		Flight flight2 = flightRepository.findById(2L);
		Integer availableFirstClass = flight2.getAvailableSeats().get(BudgetClass.FIRST_CLASS);
		assertEquals((Integer)5, availableFirstClass);
	}
	
	@Test
	public void updateVolumeDiscounts() {
		Flight flight = flightRepository.findById(TEST_FLIGHT_ID);
		assertTrue(BigDecimal.valueOf(10).compareTo(
				flight.getVolumeDiscounts().get(5)) == 0);
		flight.addVolumeDiscount(5, BigDecimal.valueOf(17.25));
		flight = flightRepository.save(flight);
		assertEquals(0.0, BigDecimal.valueOf(17.25).compareTo(flight.getVolumeDiscounts()
				.get(5)), 0.001);
		
	}

	@Test
	public void getVolumeDiscountsPerFlight() {
		Flight flight = flightRepository.findById(TEST_FLIGHT_ID);
		assertEquals(3, flight.getVolumeDiscounts().size());
	}
	@Test
	public void searchForAvailableFlights(){
		Calendar cal = Calendar.getInstance();
		cal.set(2017, Calendar.SEPTEMBER, 18);
		List<Flight> flights = flightRepository.searchForAvailableFlights(
				"bru",
				"new",
				1,
				BudgetClass.FIRST_CLASS,
				cal.getTime()
		);
		assertNotNull(flights);
		assertEquals(1, flights.size());
	}

	@Test
	public void shouldCheckAvailableSeats(){
		Flight flight = flightRepository.findById(1L);
		assertEquals(true, flightRepository.checkIfSeatsAvailable(20, flight, BudgetClass.ECONOMY));
		assertEquals(true, flightRepository.checkIfSeatsAvailable(2, flight, BudgetClass.ECONOMY));
		assertEquals(false, flightRepository.checkIfSeatsAvailable(21, flight, BudgetClass.ECONOMY));
	}

	@Test
	public void shouldReserveSeats(){
		Flight flight = flightRepository.findById(1L);
		flightRepository.reserveSeats(10, flight, BudgetClass.ECONOMY);
		assertEquals(Optional.of(10), Optional.of(flightRepository.findById(1L).getAvailableSeats().get(BudgetClass.ECONOMY)));
	}

	@Test
	public void shouldRevokeSeats(){
		Flight flight = flightRepository.findById(1L);
		flightRepository.revokeSeats(8, flight, BudgetClass.ECONOMY);
		assertEquals(Optional.of(28), Optional.of(flightRepository.findById(1L).getAvailableSeats().get(BudgetClass.ECONOMY)));
	}
	
	
}
