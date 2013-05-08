/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package playground.acmarmol.matsim2030.microcensus2010;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.replanning.PlanStrategyModule;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.population.PersonImpl;
import org.matsim.core.utils.geometry.CoordImpl;
import org.matsim.households.Households;
import org.matsim.utils.objectattributes.ObjectAttributes;


/**
* 
* Parses the zielpersonen.dat file from MZ2010, creates matsim persons and adds them to the matsim population.
* Also fills the population attributes with the microcensus information.
* 
* @see org.matsim.utils.objectattributes 
*
* @author acmarmol
* 
*/
	
public class MZ2010ZielPersonParser {
	
//////////////////////////////////////////////////////////////////////
//member variables
//////////////////////////////////////////////////////////////////////

	private Households households;
	private ObjectAttributes householdAttributes;
	private Population population;
	private ObjectAttributes populationAttributes;
	

//////////////////////////////////////////////////////////////////////
//constructors
//////////////////////////////////////////////////////////////////////

	public MZ2010ZielPersonParser(Population population, ObjectAttributes populationAttributes,  Households households, ObjectAttributes householdAttributes) {
	super();
	this.households = households;
	this.householdAttributes = householdAttributes;
	this.population = population;
	this.populationAttributes = populationAttributes;

}	


//////////////////////////////////////////////////////////////////////
//private methods
//////////////////////////////////////////////////////////////////////
	
	public void parse(String zielpersonenFile) throws Exception{
		
		FileReader fr = new FileReader(zielpersonenFile);
		BufferedReader br = new BufferedReader(fr);
		String curr_line = br.readLine(); // Skip header
				
		while ((curr_line = br.readLine()) != null) {
			
		String[] entries = curr_line.split("\t",-1);
		
		//household number & person number
		String hhnr = entries[0].trim();
		String zielpnr = entries[1].trim();
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.HOUSEHOLD_NUMBER, hhnr);
		
		//person weight 
		String person_weight = entries[2];
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.PERSON_WEIGHT, person_weight);
		
		//person age 
		String age = entries[188];
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.AGE, age);
		
		//person gender
		String gender = entries[190];
		if(gender.equals("1")){gender = MZConstants.MALE;}
		else if(gender.equals("2")){gender = MZConstants.FEMALE;}
		else Gbl.errorMsg("This should never happen!  Gender: " + gender+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.GENDER, gender);
		
		//day of week
		String dow = entries[10];
		if(dow.equals("1")){dow = MZConstants.MONDAY;}
		else if(dow.equals("2")){dow = MZConstants.TUESDAY;}
		else if(dow.equals("3")){dow = MZConstants.WEDNESDAY;}
		else if(dow.equals("4")){dow = MZConstants.THURSDAY;}
		else if(dow.equals("5")){dow = MZConstants.FRIDAY;}
		else if(dow.equals("6")){dow = MZConstants.SATURDAY;}
		else if(dow.equals("7")){dow = MZConstants.SUNDAY;}
				else Gbl.errorMsg("This should never happen!  Day of week: " + dow + " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.DAY_OF_WEEK, dow);
		
		
		//employment status
		boolean employed = true;		
		String employment_status = entries[177];
		
		if(!employment_status.equals(" ")){
			if(Integer.parseInt(employment_status)>4){employed = false;}
		}
		else{
			employed = false;
		}
		
		//////////////////////////////////////////////////////////////////////
				
		if(employment_status.equals("1")){employment_status = MZConstants.INDEPENDENT;}
		else if(employment_status.equals("2")){employment_status = MZConstants.MITARBEITENDES;}
		else if(employment_status.equals("3")){employment_status = MZConstants.EMPLOYEE;}
		else if(employment_status.equals("4")){employment_status = MZConstants.TRAINEE	;}
		else if(employment_status.equals("5")){employment_status = MZConstants.UNEMPLOYED;}
		else if(employment_status.equals("6")){employment_status = MZConstants.AUSBILDUNG;}	
		else if(employment_status.equals("7")){employment_status = MZConstants.RETIRED;}
		else if(employment_status.equals("8")){employment_status = MZConstants.DISABLED;}
		else if(employment_status.equals("9")){employment_status = MZConstants.HOUSEWIFE_HOUSEHUSBAND;}
		else if(employment_status.equals("10")){employment_status = MZConstants.OTHER_INACTIVE;}
		else if(employment_status.equals(" ")){employment_status = MZConstants.UNSPECIFIED;}
		else Gbl.errorMsg("This should ne ver happen! Employment Status: " + employment_status + " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.EMPLOYMENT_STATUS, employment_status);
		
		//level of employment
		String level_employment = entries[179];
		if(level_employment.equals("1")){level_employment = "90-100%";}
		else if(level_employment.equals("2")){level_employment = "70-89%";}
		else if(level_employment.equals("3")){level_employment = "50-69%";}
		else if(level_employment.equals("4")){level_employment = "less than 50%";}
		else if(level_employment.equals("99")){level_employment = "part-time unspecified";}
		else if(level_employment.equals("999")){level_employment = MZConstants.UNEMPLOYED;}
		else if(level_employment.equals(" ")){level_employment = MZConstants.UNSPECIFIED;}
		else Gbl.errorMsg("This should never happen! Level of Employment: " + level_employment + " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.LEVEL_EMPLOYMENT, level_employment);
		
		//other activity 1:
		String other_activity = entries[35];
		if(other_activity.equals("0")){other_activity = MZConstants.NO_OTHER;}
		else if(other_activity.equals("21")){other_activity = MZConstants.REGISTERED_UNEMPLOYED;}
		else if(other_activity.equals("22")){other_activity = MZConstants.LOOKING_JOB;}
		else if(other_activity.equals("32")){other_activity = MZConstants.AUSBILDUNG;}
		else if(other_activity.equals("33")){other_activity = MZConstants.HOUSEWIFE_HOUSEHUSBAND;}
		else if(other_activity.equals("34")){other_activity = MZConstants.RETIRED;}
		else if(other_activity.equals("35")){other_activity = MZConstants.DISABLED;}
		else if(other_activity.equals("36")){other_activity = MZConstants.OTHER;}
		else if(other_activity.equals("-99")){other_activity = MZConstants.UNEMPLOYED;}
		else{ Gbl.errorMsg("No other activity know for : " + other_activity);}
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.OTHER_ACTIVITY1, other_activity);
		
		
		//other activity 1:
		other_activity = entries[36];
		if(other_activity.equals("0")){other_activity = MZConstants.NO_OTHER;}
		else if(other_activity.equals("21")){other_activity = MZConstants.REGISTERED_UNEMPLOYED;}
		else if(other_activity.equals("22")){other_activity = MZConstants.LOOKING_JOB;}
		else if(other_activity.equals("32")){other_activity = MZConstants.AUSBILDUNG;}
		else if(other_activity.equals("33")){other_activity = MZConstants.HOUSEWIFE_HOUSEHUSBAND;}
		else if(other_activity.equals("34")){other_activity = MZConstants.RETIRED;}
		else if(other_activity.equals("35")){other_activity = MZConstants.DISABLED;}
		else if(other_activity.equals("36")){other_activity = MZConstants.OTHER;}
		else if(other_activity.equals("-99")){other_activity = MZConstants.UNEMPLOYED_NO_OTHER;}
		else{ Gbl.errorMsg("No other activity know for : " + other_activity);}
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.OTHER_ACTIVITY2, other_activity);
		
		//other activity 3:
		other_activity = entries[37];
		if(other_activity.equals("0")){other_activity = MZConstants.NO_OTHER;}
		else if(other_activity.equals("21")){other_activity = MZConstants.REGISTERED_UNEMPLOYED;}
		else if(other_activity.equals("22")){other_activity = MZConstants.LOOKING_JOB;}
		else if(other_activity.equals("32")){other_activity = MZConstants.AUSBILDUNG;}
		else if(other_activity.equals("33")){other_activity = MZConstants.HOUSEWIFE_HOUSEHUSBAND;}
		else if(other_activity.equals("34")){other_activity = MZConstants.RETIRED;}
		else if(other_activity.equals("35")){other_activity = MZConstants.DISABLED;}
		else if(other_activity.equals("36")){other_activity = MZConstants.OTHER;}
		else if(other_activity.equals("-99")){other_activity = MZConstants.UNEMPLOYED_NO_OTHER;}
		else{ Gbl.errorMsg("No other activity know for : " + other_activity);}
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.OTHER_ACTIVITY3, other_activity);
		
		
		
		
		// work location coordinate (round to 1/10 of hectare) - WGS84 (124,125) & CH1903 (126,127)
		if(employed){
		Coord work_location = new CoordImpl(entries[126].trim(),entries[127].trim());
		//work_location.setX(Math.round(work_location.getX()/10.0)*10);
		//work_location.setY(Math.round(work_location.getY()/10.0)*10);
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.WORK_LOCATION_COORD, work_location);
		} //else?

		
		//total nr wege inland
		String t_wege = entries[202];
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.TOTAL_TRIPS_INLAND, t_wege);
		
		//total wege time
		String wege_time = entries[203];
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.TOTAL_TRIPS_DURATION, wege_time);
		
		//total wege distance
		String wege_dist = entries[196];
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.TOTAL_TRIPS_DISTANCE, wege_dist);
		
		
		
		
		//car driving license
		String licence = entries[193];
		if(licence.equals("1")){
			licence = MZConstants.YES;
		}else{
			licence = MZConstants.NO;
		}
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.DRIVING_LICENCE, licence);
		
		
		//car availability
		String car_av = entries[63];
		if(car_av.equals("1")){car_av = MZConstants.ALWAYS;}
		else if(car_av.equals("2")){car_av =MZConstants.ARRANGEMENT;}
		else if(car_av.equals("3")){car_av = MZConstants.NEVER;}
		else if(car_av.equals("-99")){car_av = "???";}// -review
		else if(car_av.equals("-98")){car_av = MZConstants.NO_ANSWER;}
		else if(car_av.equals("-97")){car_av = MZConstants.UNSPECIFIED;}
		else Gbl.errorMsg("This should never happen!  Car availability: " + car_av+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.CAR_AVAILABILITY, car_av);
		
		//motorcycle availability
		String mcycle_av = entries[62];
		if(mcycle_av.equals("1")){mcycle_av = MZConstants.ALWAYS;}
		else if(mcycle_av.equals("2")){mcycle_av = MZConstants.ARRANGEMENT;}
		else if(mcycle_av.equals("3")){mcycle_av = MZConstants.NEVER;}
		else if(mcycle_av.equals("-99")){mcycle_av = "???";}// -review
		else if(mcycle_av.equals("-98")){mcycle_av = MZConstants.NO_ANSWER;}
		else if(mcycle_av.equals("-97")){mcycle_av = MZConstants.UNSPECIFIED;}
		else Gbl.errorMsg("This should never happen!  Motorcycle availability: " + mcycle_av+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.MOTORCYCLE_AVAILABILITY, mcycle_av);
		
		//small motorcycle availability
		String smcycle_av = entries[61];
		if(smcycle_av.equals("1")){smcycle_av = MZConstants.ALWAYS;}
		else if(smcycle_av.equals("2")){smcycle_av = MZConstants.ARRANGEMENT;}
		else if(smcycle_av.equals("3")){smcycle_av = MZConstants.NEVER;}
		else if(smcycle_av.equals("-99")){smcycle_av = "age less than 16";}
		else if(smcycle_av.equals("-98")){smcycle_av = MZConstants.NO_ANSWER;}
		else if(smcycle_av.equals("-97")){smcycle_av = MZConstants.UNSPECIFIED;}
		else Gbl.errorMsg("This should never happen!  Small motorcycle availability: " + smcycle_av+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.SMALL_MOTORCYCLE_AVAILABILITY, smcycle_av);
		
		
		//Mofa availability
		String mofa_av = entries[60];
		if(mofa_av.equals("1")){mofa_av = MZConstants.ALWAYS;}
		else if(mofa_av.equals("2")){mofa_av = MZConstants.ARRANGEMENT;}
		else if(mofa_av.equals("3")){mofa_av = MZConstants.NEVER;}
		else if(mofa_av.equals("-99")){mofa_av = "age less than 14";}
		else if(mofa_av.equals("-98")){mofa_av = MZConstants.NO_ANSWER;}
		else if(mofa_av.equals("-97")){mofa_av = MZConstants.UNSPECIFIED;}
		else Gbl.errorMsg("This should never happen!  Mofa availability: " + mofa_av+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.MOFA_AVAILABILITY, mofa_av);
		
		//Bicycle availability
		String bike_av = entries[59];
		if(bike_av.equals("1")){bike_av = MZConstants.ALWAYS;}
		else if(bike_av.equals("2")){bike_av = MZConstants.ARRANGEMENT;}
		else if(bike_av.equals("3")){bike_av = MZConstants.NEVER;}
		else if(bike_av.equals("-99")){bike_av = MZConstants.UNSPECIFIED;}// -review
		else if(bike_av.equals("-98")){bike_av = MZConstants.NO_ANSWER;}
		else if(bike_av.equals("-97")){bike_av = MZConstants.UNSPECIFIED;}
		else Gbl.errorMsg("This should never happen!  Bike availability: " + bike_av+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.BICYCLE_AVAILABILITY, bike_av);
		
		//car-sharing membership
		String sharing = entries[56];
		if(sharing .equals("1")){sharing  = MZConstants.YES;}
		else if(sharing.equals("2")){sharing  = MZConstants.NO;}
		else if(sharing.equals("-99")){sharing = "???";}// -review
		else if(sharing.equals("-98")){sharing = MZConstants.NO_ANSWER;}
		else if(sharing.equals("-97")){sharing = MZConstants.NOT_KNOWN;}	
		else Gbl.errorMsg("This should never happen!  Car sharing membership: " + sharing + " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.CAR_SHARING_MEMBERSHIP, sharing);
		
		//HalbTax
		String halbtax = entries[48];
		if(halbtax.equals("1")){halbtax = MZConstants.YES;}
		else if(halbtax.equals("2")){halbtax = MZConstants.NO;}
		else if(halbtax.equals("-98")){halbtax = MZConstants.NO_ANSWER;}
		else if(halbtax.equals("-97")){halbtax = MZConstants.NOT_KNOWN;}
		else Gbl.errorMsg("This should never happen!  Halbtax: " + halbtax+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.ABBO_HT, halbtax);
		
		//GA first class
		String gaFirstClass = entries[49];
		if(gaFirstClass.equals("1")){gaFirstClass = MZConstants.YES;} 
		else if(gaFirstClass.equals("2")){gaFirstClass = MZConstants.NO;}
		else if(gaFirstClass.equals("-98")){gaFirstClass = MZConstants.NO_ANSWER;}
		else if(gaFirstClass.equals("-97")){gaFirstClass = MZConstants.NOT_KNOWN;}
		else Gbl.errorMsg("This should never happen!  GA First Class: " + gaFirstClass+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.ABBO_GA1, gaFirstClass);
		
		//GA second class
		String gaSecondClass = entries[50];
		if(gaSecondClass.equals("1")){gaSecondClass = MZConstants.YES;}
		else if(gaSecondClass.equals("2")){gaSecondClass = MZConstants.NO;}
		else if(gaSecondClass.equals("-98")){gaSecondClass = MZConstants.NO_ANSWER;}
		else if(gaSecondClass.equals("-97")){gaSecondClass = MZConstants.NOT_KNOWN;}
		else Gbl.errorMsg("This should never happen!  GA Second Class: " + gaSecondClass+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.ABBO_GA2, gaSecondClass);
		
		
		//verbund abonnement
		String verbund = entries[51];
		if(verbund.equals("1")){verbund = MZConstants.YES;}
		else if(verbund.equals("2")){verbund = MZConstants.NO;}
		else if(verbund.equals("-98")){verbund = MZConstants.NO_ANSWER;}
		else if(verbund.equals("-97")){verbund = MZConstants.NOT_KNOWN;}
		else Gbl.errorMsg("This should never happen!  Verbund abonnement: " + verbund+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.ABBO_VERBUND, verbund);
		
		//strecken abonnement
		String strecken = entries[52];
		if(strecken.equals("1")){strecken = MZConstants.YES;}
		else if(strecken.equals("2")){strecken = MZConstants.NO;}
		else if(strecken.equals("-98")){strecken = MZConstants.NO_ANSWER;}
		else if(strecken.equals("-97")){strecken = MZConstants.NOT_KNOWN;}
		else Gbl.errorMsg("This should never happen!  GA Second Class: " + strecken+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.ABBO_STRECKEN, strecken);
		
		
		//Gleis 7
		String gleis7 = entries[53];
		if(gleis7.equals("1")){gleis7 = MZConstants.YES;}
		else if(gleis7.equals("2")){gleis7 = MZConstants.NO;}
		else if(gleis7.equals("-99")){gleis7 = "not in age";}
		else if(gleis7.equals("-98")){gleis7 = MZConstants.NO_ANSWER;}
		else if(gleis7.equals("-97")){gleis7 = MZConstants.NOT_KNOWN;}
		else Gbl.errorMsg("This should never happen!  Gleis 7: " + gleis7+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.ABBO_GLEIS7, gleis7);
		
		//last education
		String education = entries[180];
		if(education.equals("1")){education = MZConstants.EDUCATION_NO_SCHOOL;}
		else if(education.equals("2")){education = MZConstants.EDUCATION_NOT_FINISHED_MANDATORY_SCHOOL;}
		else if(education.equals("3")){education = MZConstants.EDUCATION_MANDATORY_SCHOOL;}
		else if(education.equals("4")){education = MZConstants.EDUCATION_ONE_YEAR_AUSBILDUNG;}
		else if(education.equals("5")){education = MZConstants.EDUCATION_TWO_YEAR_BERUFLICHE_GRUNDBILDUNG;}
		else if(education.equals("6")){education = MZConstants.EDUCATION_TWO_YEAR_VOLLZEITBERUFSLEHRE;}
		else if(education.equals("7")){education = MZConstants.EDUCATION_TWO_THREE_YEARS_AUSBILDUNG;}
		else if(education.equals("8")){education = MZConstants.EDUCATION_THREE_FOUR_YEARS_BERUFSLEHRE;}
		else if(education.equals("9")){education = MZConstants.EDUCATION_THREE_FOUR_YEARS_VOLLZEITBERUFSLEHRE;}
		else if(education.equals("10")){education = MZConstants.EDUCATION_LEHRKRÄFTE;}
		else if(education.equals("11")){education = MZConstants.EDUCATION_MATURITÄTSCHULE;}
		else if(education.equals("12")){education = MZConstants.EDUCATION_BERUFSLEHRE;}
		else if(education.equals("13")){education = MZConstants.EDUCATION_HÖHERE_BERUFSAUSBILDUNG;}
		else if(education.equals("14")){education = MZConstants.EDUCATION_TECHNIKERSCHLE_HÖHEREFACHSSCHULE_FACHHOSCHSCHULE;}
		else if(education.equals("15")){education = MZConstants.EDUCATION_TECHNIKERSCHLE_HÖHEREFACHSSCHULE_FACHHOSCHSCHULE;}
		else if(education.equals("16")){education = MZConstants.EDUCATION_TECHNIKERSCHLE_HÖHEREFACHSSCHULE_FACHHOSCHSCHULE;}
		else if(education.equals("17")){education = MZConstants.EDUCATION_UNIVERSITÄT;}
		else if(education.equals("-98") || education.equals(" ")){education = MZConstants.NO_ANSWER;}
		else if(education.equals("-97")){education = MZConstants.NOT_KNOWN;}
		else Gbl.errorMsg("Last education: " + education+ " doesn't exist");
		populationAttributes.putAttribute(hhnr.concat(zielpnr), MZConstants.LAST_EDUCATION, education);
		
		
		
		//creating matsim person
		PersonImpl person = new PersonImpl(new IdImpl(hhnr.concat(zielpnr)));
		person.setAge(Integer.parseInt(age));
		person.setEmployed(employed);
		person.setLicence(licence);
		person.setSex(gender);
		population.addPerson(person);
		}
			
		br.close();
		fr.close();
		System.out.println("      done.");

		System.out.println("      # persons parsed = "  + population.getPersons().size());
		System.out.println();
	
	}
	
	
}
