/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse223.resto.model;
import java.io.Serializable;
import java.util.*;
import java.sql.Date;

// line 40 "../../../../../RestoAppPersistence.ump"
// line 104 "../../../../../RestoApp.ump"
public class Event implements Serializable
{

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static int nextEventId = 1;

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Event Attributes
  private String nameOfEvent;
  private String description;
  private Date startDate;
  private Date endDate;

  //Autounique Attributes
  private int eventId;

  //Event Associations
  private RestoApp restoApp;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Event(String aNameOfEvent, String aDescription, Date aStartDate, Date aEndDate, RestoApp aRestoApp)
  {
    nameOfEvent = aNameOfEvent;
    description = aDescription;
    startDate = aStartDate;
    endDate = aEndDate;
    eventId = nextEventId++;
    boolean didAddRestoApp = setRestoApp(aRestoApp);
    if (!didAddRestoApp)
    {
      throw new RuntimeException("Unable to create event due to restoApp");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setNameOfEvent(String aNameOfEvent)
  {
    boolean wasSet = false;
    nameOfEvent = aNameOfEvent;
    wasSet = true;
    return wasSet;
  }

  public boolean setDescription(String aDescription)
  {
    boolean wasSet = false;
    description = aDescription;
    wasSet = true;
    return wasSet;
  }

  public boolean setStartDate(Date aStartDate)
  {
    boolean wasSet = false;
    startDate = aStartDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setEndDate(Date aEndDate)
  {
    boolean wasSet = false;
    endDate = aEndDate;
    wasSet = true;
    return wasSet;
  }

  public String getNameOfEvent()
  {
    return nameOfEvent;
  }

  public String getDescription()
  {
    return description;
  }

  public Date getStartDate()
  {
    return startDate;
  }

  public Date getEndDate()
  {
    return endDate;
  }

  public int getEventId()
  {
    return eventId;
  }

  public RestoApp getRestoApp()
  {
    return restoApp;
  }

  public boolean setRestoApp(RestoApp aRestoApp)
  {
    boolean wasSet = false;
    if (aRestoApp == null)
    {
      return wasSet;
    }

    RestoApp existingRestoApp = restoApp;
    restoApp = aRestoApp;
    if (existingRestoApp != null && !existingRestoApp.equals(aRestoApp))
    {
      existingRestoApp.removeEvent(this);
    }
    restoApp.addEvent(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    RestoApp placeholderRestoApp = restoApp;
    this.restoApp = null;
    if(placeholderRestoApp != null)
    {
      placeholderRestoApp.removeEvent(this);
    }
  }

  // line 48 "../../../../../RestoAppPersistence.ump"
   public static  void reinitializeEventID(List<Event> events){
    int nextEventNumber = 0;
		for(Event event : events){
			if(event.getEventId() > nextEventNumber){
				nextEventNumber = event.getEventId();
			}
		}
		nextEventNumber++;
  }


  public String toString()
  {
    return super.toString() + "["+
            "eventId" + ":" + getEventId()+ "," +
            "nameOfEvent" + ":" + getNameOfEvent()+ "," +
            "description" + ":" + getDescription()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "startDate" + "=" + (getStartDate() != null ? !getStartDate().equals(this)  ? getStartDate().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "endDate" + "=" + (getEndDate() != null ? !getEndDate().equals(this)  ? getEndDate().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "restoApp = "+(getRestoApp()!=null?Integer.toHexString(System.identityHashCode(getRestoApp())):"null");
  }  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 45 "../../../../../RestoAppPersistence.ump"
  private static final long serialVersionUID = 123123123123123L ;

  
}