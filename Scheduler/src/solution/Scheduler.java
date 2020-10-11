package solution;
import java.time.LocalDate;
import java.util.ArrayList;
import baseclasses.Aircraft;
import baseclasses.CabinCrew;
import baseclasses.DoubleBookedException;
import baseclasses.FlightInfo;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.InvalidAllocationException;
import baseclasses.Pilot;
import baseclasses.Schedule;
import baseclasses.SchedulerRunner;

public class Scheduler implements IScheduler 
{
	private FlightInfo flight1;
	private Schedule s;

	private Aircraft getBestPlane(ArrayList<Aircraft> planes, int passNo)
	{
		Aircraft bestPlane = null;
		for(Aircraft a : planes) 				
		{
			if(bestPlane == null || (a.getSeats()-passNo) < (bestPlane.getSeats() - passNo))
			{
				if(!s.hasConflict(a, flight1))
				{
					bestPlane = a;
				}
			}
		}
		return bestPlane;
	}	

	private Pilot getBestPilot(ArrayList<Pilot> pilots)
	{
		for(Pilot p : pilots)
		{
			if(p.getRank().equals(Pilot.Rank.CAPTAIN) && !s.hasConflict(p,flight1)) 
			{
				return p;
			}
		}
		return null;
	}

	private Pilot getBestFirstOfficer(ArrayList<Pilot> pilots)
	{
		for(Pilot p : pilots)
		{
			if(p.getRank().equals(Pilot.Rank.FIRST_OFFICER) && !s.hasConflict(p,flight1))
			{
				return p;
			}
		}
		return null;
	}

	private ArrayList<CabinCrew> getBestCabinCrew(ArrayList<CabinCrew> crews,int count)
	{
		ArrayList<CabinCrew> bestCabinCrew = new ArrayList<CabinCrew>();
		for(CabinCrew c : crews)
		{
			if(!s.hasConflict(c,flight1))
			{
				bestCabinCrew.add(c);
				count--;
			}
			if(count == 0)
			{
				return bestCabinCrew;
			}
		}
		return bestCabinCrew;
	}
	@Override
	public Schedule generateSchedule(IAircraftDAO planes, ICrewDAO crew, IRouteDAO routes, IPassengerNumbersDAO passengers,
			LocalDate start, LocalDate end) 
	{
		s = new Schedule(routes,start,end);
		int restart = 0;
		while(!s.isCompleted())
		{
			flight1 = s.getRemainingAllocations().get(restart);

			//DECIDES AIRCRAFT

			int passNo = passengers.getPassengerNumbersFor(flight1.getFlight().getFlightNumber(),flight1.getDepartureDateTime().toLocalDate());
			ArrayList<Aircraft> startingPos = (ArrayList<Aircraft>)planes.findAircraftByStartingPosition(flight1.getFlight().getDepartureAirportCode());
			Aircraft bestPlane = getBestPlane(startingPos,passNo);
			if(bestPlane == null)
			{
				bestPlane =  getBestPlane((ArrayList<Aircraft>)planes.getAllAircraft(),passNo);
			}

			//DECIDES PILOTS

			ArrayList<Pilot> pilot = (ArrayList<Pilot>)crew.findPilotsByHomeBaseAndTypeRating(bestPlane.getTypeCode(),flight1.getFlight().getDepartureAirportCode()) ;
			Pilot captain =  getBestPilot(pilot);
			Pilot firstOfficer = getBestFirstOfficer(pilot);
			ArrayList<Pilot> allPilots = (ArrayList<Pilot>) crew.getAllPilots();
			if(captain == null)
			{
				captain = getBestPilot((ArrayList<Pilot>) crew.findPilotsByTypeRating(bestPlane.getTypeCode()));
				if(captain == null)
				{
					captain = getBestPilot(allPilots);
				}
			}
			if(firstOfficer == null)
			{
				firstOfficer = getBestFirstOfficer((ArrayList<Pilot>) crew.findPilotsByTypeRating(bestPlane.getTypeCode()));
				if(firstOfficer == null)
				{
					firstOfficer = getBestFirstOfficer(allPilots);
				}
			}

			//DECIDES CABIN CREW

			int count = bestPlane.getCabinCrewRequired();
			ArrayList<CabinCrew> bestCabinCrew = (ArrayList<CabinCrew>)crew.findCabinCrewByHomeBaseAndTypeRating(bestPlane.getTypeCode(),flight1.getFlight().getDepartureAirportCode());
			ArrayList<CabinCrew> allCabinCrew = (ArrayList<CabinCrew>)crew.getAllCabinCrew();
			bestCabinCrew = getBestCabinCrew(bestCabinCrew,count);
			if(bestCabinCrew.size() < count)
			{
				bestCabinCrew.addAll(getBestCabinCrew((ArrayList<CabinCrew>)crew.findCabinCrewByTypeRating(bestPlane.getTypeCode()),count - bestCabinCrew.size()));
				if(bestCabinCrew.size() < count)
				{
					bestCabinCrew.addAll(getBestCabinCrew(allCabinCrew,count - bestCabinCrew.size()));
				}
			}

			//ALLOCATES EVRYTHING

			try
			{
				s.allocateAircraftTo(bestPlane, flight1);
				s.allocateCaptainTo(captain, flight1);
				s.allocateFirstOfficerTo(firstOfficer, flight1);
				for(CabinCrew c : bestCabinCrew)
				{
					s.allocateCabinCrewTo(c, flight1);
				}
				s.completeAllocationFor(flight1);
			}
			catch(DoubleBookedException | InvalidAllocationException dbe)
			{
				s.unAllocate(flight1);				
				restart ++;
			}
			if(restart > s.getRemainingAllocations().size()-1)
			{
				restart = 0;
			}
		}
		return s;
	}

	@Override
	public void setSchedulerRunner(SchedulerRunner arg0) 
	{
	}

	@Override
	public void stop() 
	{
		// TODO Auto-generated method stub

	}
}
