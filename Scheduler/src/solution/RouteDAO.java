package solution;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.*;

import java.time.DayOfWeek;
import java.time.Duration;

import org.w3c.dom.*;
import org.xml.sax.*;

import baseclasses.DataLoadingException;
import baseclasses.IRouteDAO;
import baseclasses.Route;

/**
 * The RouteDAO parses XML files of route information, each route specifying
 * where the airline flies from, to, and on which day of the week
 */
public class RouteDAO implements IRouteDAO 
{
	
	ArrayList<Route> routes = new ArrayList<Route>();

	/**
	 * Finds all flights that depart on the specified day of the week
	 * @param dayOfWeek A three letter day of the week, e.g. "Tue"
	 * @return A list of all routes that depart on this day
	 */
	@Override
	public List<Route> findRoutesByDayOfWeek(String dayOfWeek) 
	{
		ArrayList<Route> dayRoutes = new ArrayList<Route>();
		for(Route r : routes)
		{
			if(r.getDayOfWeek().equals(dayOfWeek))
			{
				dayRoutes.add(r);
			}
		}
		return dayRoutes;
	}

	/**
	 * Finds all of the flights that depart from a specific airport on a specific day of the week
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @param dayOfWeek the three letter day of the week code to searh for, e.g. "Tue"
	 * @return A list of all routes from that airport on that day
	 */
	@Override
	public List<Route> findRoutesByDepartureAirportAndDay(String airportCode, String dayOfWeek) 
	{
		ArrayList<Route> dayAirportRoutes = new ArrayList<Route>();
		for(Route r : routes)
		{
			if(r.getDayOfWeek().equals(dayOfWeek) && r.getDepartureAirportCode().equals(airportCode))
			{
				dayAirportRoutes.add(r);
			}
		}
		return dayAirportRoutes;
		
	}

	/**
	 * Finds all of the flights that depart from a specific airport
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @return A list of all of the routes departing the specified airport
	 */
	@Override
	public List<Route> findRoutesDepartingAirport(String airportCode) 
	{
		ArrayList<Route> airportRoutes = new ArrayList<Route>();
		for(Route r : routes)
		{
			if(r.getDepartureAirportCode().equals(airportCode))
			{
				airportRoutes.add(r);
			}
		}
		return airportRoutes;
	}

	/**
	 * Finds all of the flights that depart on the specified date
	 * @param date the date to search for
	 * @return A list of all routes that depart on this date
	 */
	@Override
	public List<Route> findRoutesbyDate(LocalDate date) 
	{
		ArrayList<Route> dates = new ArrayList<Route>();
		for(Route r : routes)
		{
			if(date.getDayOfWeek().toString().contains(r.getDayOfWeek().toUpperCase()))
			{
				dates.add(r);
			}
		}
		return dates;
	}

	/**
	 * Returns The full list of all currently loaded routes
	 * @return The full list of all currently loaded routes
	 */
	@Override
	public List<Route> getAllRoutes() 
	{
		ArrayList<Route> newRoutes = new ArrayList<Route>();
		for (int i = 0; i < routes.size(); i++) 
		{
			newRoutes.add(routes.get(i));
		}
		
		return newRoutes;
	}

	/**
	 * Returns The number of routes currently loaded
	 * @return The number of routes currently loaded
	 */
	@Override
	public int getNumberOfRoutes() 
	{
		// TODO Auto-generated method stub
		return routes.size();
	}

	/**
	 * Loads the route data from the specified file, adding them to the currently loaded routes
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadRouteData(Path arg0) throws DataLoadingException 
	{
		ArrayList<String> days = new ArrayList<String>(Arrays.asList("Mon","Tue","Wed","Thu","Fri","Sat","Sun"));
		try
		{
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document d = db.parse(Files.newInputStream(arg0));
			Element root = d.getDocumentElement();
			NodeList titles = root.getElementsByTagName("Route");// try changing to Routes to get nicer tree.
			for(int i = 0; i < titles.getLength(); i++)
			{
				Route newRoute = new Route();
				newRoute.setFlightNumber(Integer.parseInt(titles.item(i).getChildNodes().item(1).getTextContent()));
				String dow = titles.item(i).getChildNodes().item(3).getTextContent();
				if(days.contains(dow))
				{
					newRoute.setDayOfWeek(dow);
				}
				else
				{
					throw new DataLoadingException();
				}
				newRoute.setDepartureTime(LocalTime.parse(titles.item(i).getChildNodes().item(5).getTextContent()));
				newRoute.setDepartureAirport(titles.item(i).getChildNodes().item(7).getTextContent());
				newRoute.setDepartureAirportCode(titles.item(i).getChildNodes().item(9).getTextContent());
				newRoute.setArrivalTime(LocalTime.parse(titles.item(i).getChildNodes().item(11).getTextContent()));
				newRoute.setArrivalAirport(titles.item(i).getChildNodes().item(13).getTextContent());
				newRoute.setArrivalAirportCode(titles.item(i).getChildNodes().item(15).getTextContent());
				newRoute.setDuration(Duration.parse(titles.item(i).getChildNodes().item(17).getTextContent()));
				routes.add(newRoute);
			}
		}
		catch(Exception e)
		{
			throw new DataLoadingException(e);
		}
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() 
	{
		routes.clear();
	}

}
