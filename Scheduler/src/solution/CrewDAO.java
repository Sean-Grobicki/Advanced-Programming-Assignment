package solution;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.json.*;
import java.io.IOException;

import baseclasses.Aircraft;
import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.ICrewDAO;
import baseclasses.Pilot;

/**
 * The CrewDAO is responsible for loading data from JSON-based crew files 
 * It contains various methods to help the scheduler find the right pilots and cabin crew
 */
public class CrewDAO implements ICrewDAO 
{

	/**
	 * Loads the crew data from the specified file, adding them to the currently loaded crew
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	
	private ArrayList<Crew> crewMembers = new ArrayList<Crew>();
	
	@Override
	public void loadCrewData(Path p) throws DataLoadingException
	{	
		try 
		{
			BufferedReader br = Files.newBufferedReader(p);
			String json = "";
			String line = "";
			while((line = br.readLine()) != null)
			{
				json += line;
			}
			JSONObject crew = new JSONObject(json);
			JSONArray pilotsRoot = crew.getJSONArray("pilots");
			JSONArray cabinRoot = crew.getJSONArray("cabincrew");
			for(int i=0; i < pilotsRoot.length(); i++)
			{
				Pilot crewMember = new Pilot();
				JSONObject crewMemberJson =  pilotsRoot.getJSONObject(i);
				crewMember.setForename(crewMemberJson.getString("forename")); 
				crewMember.setSurname(crewMemberJson.getString("surname"));
				crewMember.setHomeBase(crewMemberJson.getString("homebase"));
				crewMember.setRank(Pilot.Rank.valueOf(crewMemberJson.getString("rank").toUpperCase()));
				JSONArray typeRatings = crewMemberJson.getJSONArray("typeRatings");
				for(int j = 0; j < typeRatings.length();j++) 
				{
					crewMember.setQualifiedFor(typeRatings.getString(j));
				}
				crewMembers.add(crewMember);
			}
			for(int i=0; i < cabinRoot.length(); i++)
			{
				CabinCrew crewMember = new CabinCrew();
				JSONObject crewMemberJson =  cabinRoot.getJSONObject(i);
				crewMember.setForename(crewMemberJson.getString("forename")); 
				crewMember.setSurname(crewMemberJson.getString("surname"));
				crewMember.setHomeBase(crewMemberJson.getString("homebase"));
				JSONArray typeRatings = crewMemberJson.getJSONArray("typeRatings");
				for(int j = 0; j < typeRatings.length();j++) 
				{
					crewMember.setQualifiedFor(typeRatings.getString(j));
				}
				crewMembers.add(crewMember);
			}
		}
		catch (Exception e) 
		{
			throw new DataLoadingException(e);
		}
	}
	
	
	
	/**
	 * Returns a list of all the cabin crew based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at the airport with the specified airport code
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBase(String airportCode) 
	{
		ArrayList<CabinCrew> homebaseCrew = new ArrayList<CabinCrew>();
		for(int i=0; i < crewMembers.size(); i++)
		{
			if(crewMembers.get(i) instanceof CabinCrew)
			{
				if(crewMembers.get(i).getHomeBase().equals(airportCode))
				{
					homebaseCrew.add((CabinCrew)crewMembers.get(i));
				}
			}
		}
		return homebaseCrew;
	}

	/**
	 * Returns a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find cabin crew for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBaseAndTypeRating(String typeCode, String airportCode) 
	{
		ArrayList<CabinCrew> homebaseCrew = new ArrayList<CabinCrew>();
		for(int i=0; i < crewMembers.size(); i++)
		{
			if(crewMembers.get(i) instanceof CabinCrew)
			{
				if(crewMembers.get(i).getHomeBase().equals(airportCode)&& crewMembers.get(i).isQualifiedFor(typeCode))
				{
					homebaseCrew.add((CabinCrew)crewMembers.get(i));
				}
			}
		}
		return homebaseCrew;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find cabin crew for
	 * @return a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<CabinCrew> findCabinCrewByTypeRating(String typeCode) 
	{
		ArrayList<CabinCrew> typeCrew = new ArrayList<CabinCrew>();
		for(int i=0; i < crewMembers.size(); i++)
		{
			if(crewMembers.get(i) instanceof CabinCrew)
			{
				if(crewMembers.get(i).isQualifiedFor(typeCode))
				{
					typeCrew.add((CabinCrew)crewMembers.get(i));
				}
			}
		}
		return typeCrew;
	}

	/**
	 * Returns a list of all the pilots based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at the airport with the specified airport code
	 */
	@Override
	public List<Pilot> findPilotsByHomeBase(String airportCode) 
	{
		ArrayList<Pilot> homebasePilot = new ArrayList<Pilot>();
		for(int i=0; i < crewMembers.size(); i++)
		{
			if(crewMembers.get(i) instanceof Pilot)
			{
				if(crewMembers.get(i).getHomeBase().equals(airportCode))
				{
					homebasePilot.add((Pilot)crewMembers.get(i));
				}
			}
		}
		return homebasePilot;
	}

	/**
	 * Returns a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find pilots for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<Pilot> findPilotsByHomeBaseAndTypeRating(String typeCode, String airportCode) 
	{
		ArrayList<Pilot> homebasePilot = new ArrayList<Pilot>();
		for(int i=0; i < crewMembers.size(); i++)
		{
			if(crewMembers.get(i) instanceof Pilot)
			{
				if(crewMembers.get(i).getHomeBase().equals(airportCode) && crewMembers.get(i).isQualifiedFor(typeCode))
				{
					homebasePilot.add((Pilot)crewMembers.get(i));
				}
			}
		}
		return homebasePilot;
	}

	/**
	 * Returns a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find pilots for
	 * @return a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<Pilot> findPilotsByTypeRating(String typeCode) 
	{
		ArrayList<Pilot> typePilot = new ArrayList<Pilot>();
		for(int i=0; i < crewMembers.size(); i++)
		{
			if(crewMembers.get(i) instanceof Pilot)
			{
				if(crewMembers.get(i).isQualifiedFor(typeCode))
				{
					typePilot.add((Pilot)crewMembers.get(i));
				}
			}
		}
		return typePilot;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded
	 * @return a list of all the cabin crew currently loaded
	 */
	@Override
	public List<CabinCrew> getAllCabinCrew() 
	{
		ArrayList<CabinCrew> crew = new ArrayList<CabinCrew>();
		for(int i=0; i < crewMembers.size(); i++)
		{
			if(crewMembers.get(i) instanceof CabinCrew)
			{
				crew.add((CabinCrew)crewMembers.get(i));
			}
		}
		return crew;
	}

	/**
	 * Returns a list of all the crew, regardless of type
	 * @return a list of all the crew, regardless of type
	 */
	@Override
	public List<Crew> getAllCrew() 
	{
		return crewMembers;
	}

	/**
	 * Returns a list of all the pilots currently loaded
	 * @return a list of all the pilots currently loaded
	 */
	@Override
	public List<Pilot> getAllPilots() 
	{
		ArrayList<Pilot> pilots = new ArrayList<Pilot>();
		for(int i = 0; i < crewMembers.size(); i++)
		{
			if(crewMembers.get(i) instanceof Pilot)
			{
				pilots.add((Pilot)crewMembers.get(i));
			}
		}
		return pilots;
	}

	@Override
	public int getNumberOfCabinCrew() 
	{
		int crewNumber = 0;
		for(int i = 0; i < crewMembers.size(); i++)
		{
			if(crewMembers.get(i) instanceof CabinCrew)
			{
				crewNumber++;
			}
		}
		return crewNumber;
	}

	/**
	 * Returns the number of pilots currently loaded
	 * @return the number of pilots currently loaded
	 */
	@Override
	public int getNumberOfPilots() 
	{
		int pilotNumber = 0;
		for(int i=0; i < crewMembers.size(); i++)
		{
			if(crewMembers.get(i) instanceof Pilot)
			{
				pilotNumber++;
			}
		}
		return pilotNumber;
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() 
	{
		crewMembers.clear();
	}

}
