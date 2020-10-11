package solution;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;

import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.Pilot;
import baseclasses.Route;
import baseclasses.Schedule;
import baseclasses.SchedulerRunner;

/**
 * This class allows you to run the code in your classes yourself, for testing and development
 */
public class Main {

	public static void main(String[] args) 
	{	
		IAircraftDAO aircraft = new AircraftDAO();
		ICrewDAO crew = new CrewDAO();
		IRouteDAO route = new RouteDAO();
		IPassengerNumbersDAO passenger = new PassengerNumbersDAO();
        LocalDate from = LocalDate.parse("2020-07-01");
        LocalDate to = LocalDate.parse("2020-08-31");
        IScheduler scheduler = (IScheduler)new Scheduler();
		try 
		{
			aircraft.loadAircraftData(Paths.get("./data/aircraft.csv"));
	        crew.loadCrewData(Paths.get("./data/crew.json"));
	        route.loadRouteData(Paths.get("./data/routes.xml"));
	        passenger.loadPassengerNumbersData(Paths.get("./data/passengernumbers.db"));
	        SchedulerRunner sr = new SchedulerRunner(aircraft,crew,route,passenger,from,to,scheduler);
	        Schedule s =  sr.run();
	        System.out.println("Completed = "+s.getCompletedAllocations().size());
	        System.out.println("Remaining = "+s.getRemainingAllocations().size());
		}
		catch (DataLoadingException dle) 
		{
			System.err.println("Error loading crew data");
			dle.printStackTrace();
		}
	}

}
