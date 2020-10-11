package solution;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import baseclasses.DataLoadingException;
import baseclasses.IPassengerNumbersDAO;

/**
 * The PassengerNumbersDAO is responsible for loading an SQLite database
 * containing forecasts of passenger numbers for flights on dates
 */
public class PassengerNumbersDAO implements IPassengerNumbersDAO 
{


	/**
	 * Returns the number of passenger number entries in the cache
	 * @return the number of passenger number entries in the cache
	 */
	ArrayList<Bookings> bookings= new ArrayList<Bookings>();
	
	@Override
	public int getNumberOfEntries() 
	{
		return bookings.size();
	}

	/**
	 * Returns the predicted number of passengers for a given flight on a given date, or -1 if no data available
	 * @param flightNumber The flight number of the flight to check for
	 * @param date the date of the flight to check for
	 * @return the predicted number of passengers, or -1 if no data available
	 */
	@Override
	public int getPassengerNumbersFor(int flightNumber, LocalDate date) 
	{
		for(int i = 0;i < bookings.size();i++)
		{
			if(bookings.get(i).getFlightNumber() == flightNumber && bookings.get(i).getDate().equals(date))
			{
				return bookings.get(i).getPassengers();
			}
			
		}
		return -1;
	}

	/**
	 * Loads the passenger numbers data from the specified SQLite database into a cache for future calls to getPassengerNumbersFor()
	 * Multiple calls to this method are additive, but flight numbers/dates previously cached will be overwritten
	 * The cache can be reset by calling reset() 
	 * @param p The path of the SQLite database to load data from
	 * @throws DataLoadingException If there is a problem loading from the database
	 */
	@Override
	
	public void loadPassengerNumbersData(Path p) throws DataLoadingException 
	{
		Connection c = null;
		try
		{
			c = DriverManager.getConnection("jdbc:sqlite:" +p.toString());
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM PassengerNumbers");
			while(rs.next())
			{
				Bookings temp = new Bookings(LocalDate.parse(rs.getString("Date")),rs.getInt("FlightNumber"),rs.getInt("Passengers"));
				if(!bookings.contains(temp))
				{
					bookings.add(temp);
				}
					
			}
		}
		catch(Exception e)
		{
			throw new DataLoadingException(e);
		}
	}

	/**
	 * Removes all data from the DAO, ready to start again if needed
	 */
	@Override
	public void reset() 
	{
		bookings.clear();
	}

}

class Bookings
{
	private LocalDate date;
	private int flightNumber;
	private int passengers;
	
	Bookings(LocalDate date,int flightNum,int passNo)
	{
		this.setDate(date);
		setFlightNumber(flightNum);
		setPassengers(passNo);
	}

	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * @return the flightNumber
	 */
	public int getFlightNumber() {
		return flightNumber;
	}

	/**
	 * @param flightNumber the flightNumber to set
	 */
	public void setFlightNumber(int flightNumber) {
		this.flightNumber = flightNumber;
	}

	/**
	 * @return the passengers
	 */
	public int getPassengers() {
		return passengers;
	}

	/**
	 * @param passengers the passengers to set
	 */
	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}
	
	

}
