package solution;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;


/**
 * The AircraftDAO class is responsible for loading aircraft data from CSV files
 * and contains methods to help the system find aircraft when scheduling
 */
public class AircraftDAO implements IAircraftDAO 
{
	
	/**
	 * Loads the aircraft data from the specified file, adding them to the currently loaded aircraft
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
     *
	 * Initially, this contains some starter code to help you get started in reading the CSV file...
	 */
	private List<Aircraft> aircrafts = new ArrayList<Aircraft>();
	
	@Override
	public void loadAircraftData(Path p) throws DataLoadingException 
	{	
		try 
		{
			//open the file
			BufferedReader reader = Files.newBufferedReader(p);
			
			//read the file line by line
			String line = "";
			
			//skip the first line of the file - headers
			reader.readLine();
			
			while( (line = reader.readLine()) != null)
			{
				//each line has fields separated by commas, split into an array of fields
				String[] fields = line.split(",");
				Aircraft a = new Aircraft();
				a.setTailCode(fields[0]);
				a.setTypeCode(fields[1]);
				a.setManufacturer(Aircraft.Manufacturer.valueOf(fields[2].toUpperCase()));
				a.setModel(fields[3]);
				a.setSeats(Integer.parseInt(fields[4]));
				a.setCabinCrewRequired(Integer.parseInt(fields[5]));
				a.setStartingPosition(fields[6]);
				aircrafts.add(a);	
				//print a line explaining what we've found
			}
		}
		catch (Exception e) 
		{
			//Check for other exception. 
			//There was a problem reading the file
			throw new DataLoadingException(e);
		}

	}
	
	/**
	 * Returns a list of all the loaded Aircraft with at least the specified number of seats
	 * @param seats the number of seats required
	 * @return a List of all the loaded aircraft with at least this many seats
	 */
	@Override
	public List<Aircraft> findAircraftBySeats(int seats) 
	{
		ArrayList<Aircraft> seatAircrafts = new ArrayList<Aircraft>();
		for (int i = 0; i < aircrafts.size(); i++) 
		{
			if(seats <= aircrafts.get(i).getSeats())
			{
				seatAircrafts.add(aircrafts.get(i));
			}
		}
		return seatAircrafts;
	}

	/**
	 * Returns a list of all the loaded Aircraft that start at the specified airport code
	 * @param startingPosition the three letter airport code of the airport at which the desired aircraft start
	 * @return a List of all the loaded aircraft that start at the specified airport
	 */
	@Override
	public List<Aircraft> findAircraftByStartingPosition(String startingPosition) 
	{
		ArrayList<Aircraft> startPosAircrafts = new ArrayList<Aircraft>();
		for (int i = 0; i < aircrafts.size(); i++) 
		{
			if(startingPosition.equals(aircrafts.get(i).getStartingPosition()))
			{
				startPosAircrafts.add(aircrafts.get(i));
			}
		}
		return startPosAircrafts;
	}

	/**
	 * Returns the individual Aircraft with the specified tail code.
	 * @param tailCode the tail code for which to search
	 * @return the aircraft with that tail code, or null if not found
	 */
	@Override
	public Aircraft findAircraftByTailCode(String tailCode) 
	{
		for (int i = 0; i < aircrafts.size(); i++) 
		{
			if(tailCode.equalsIgnoreCase(aircrafts.get(i).getTailCode()))
			{
				return aircrafts.get(i);
			}
		}
		return null;
		
	}

	/**
	 * Returns a List of all the loaded Aircraft with the specified type code
	 * @param typeCode the type code of the aircraft you wish to find
	 * @return a List of all the loaded Aircraft with the specified type code
	 */
	@Override
	public List<Aircraft> findAircraftByType(String typeCode) 
	{
		ArrayList<Aircraft> typeAircrafts = new ArrayList<Aircraft>();
		for (int i = 0; i < aircrafts.size(); i++) 
		{
			if(typeCode.equals(aircrafts.get(i).getTypeCode()))
			{
				typeAircrafts.add(aircrafts.get(i));
			}
		}
		return typeAircrafts;
	}

	/**
	 * Returns a List of all the currently loaded aircraft
	 * @return a List of all the currently loaded aircraft
	 */
	@Override
	public List<Aircraft> getAllAircraft() 
	{
		ArrayList<Aircraft> newAircrafts = new ArrayList<Aircraft>();
		for (int i = 0; i < aircrafts.size(); i++) 
		{
			newAircrafts.add(aircrafts.get(i));
		}
		return newAircrafts;
	}

	/**
	 * Returns the number of aircraft currently loaded 
	 * @return the number of aircraft currently loaded
	 */
	@Override
	public int getNumberOfAircraft() 
	{
		return aircrafts.size();
	}

	/**
	 * Unloads all of the aircraft currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() 
	{
		aircrafts.clear();
	}

}
