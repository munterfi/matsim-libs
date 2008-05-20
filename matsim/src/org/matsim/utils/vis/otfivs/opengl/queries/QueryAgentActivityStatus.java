/* *********************************************************************** *
 * project: org.matsim.*
 * QueryAgentActivityStatus.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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

package org.matsim.utils.vis.otfivs.opengl.queries;

import java.util.Collection;

import org.matsim.events.Events;
import org.matsim.mobsim.QueueLink;
import org.matsim.mobsim.QueueNetworkLayer;
import org.matsim.mobsim.Vehicle;
import org.matsim.plans.Act;
import org.matsim.plans.Person;
import org.matsim.plans.Plan;
import org.matsim.plans.Plans;
import org.matsim.utils.vis.otfivs.interfaces.OTFDrawer;
import org.matsim.utils.vis.otfivs.interfaces.OTFQuery;


public class QueryAgentActivityStatus implements OTFQuery{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8532403277319196797L;

	public final String agentID;

	boolean calcOffset = true;

	double now;
	//out
	int activityNr = -1;
	double finished = 0;

	public QueryAgentActivityStatus(String agentID, double d) {
		this.agentID = agentID;
		this.now = d;
	}

	public void query(QueueNetworkLayer net, Plans plans, Events events) {
		Person person = plans.getPerson(this.agentID);
		if (person == null) return;

		Plan plan = person.getSelectedPlan();

		// find the acual activity by searchin all activity links
		// for a vehicle with this agent id

		for (int i=0;i< plan.getActsLegs().size(); i+=2) {
			Act act = (Act)plan.getActsLegs().get(i);
			QueueLink link = net.getQueueLink(act.getLinkId());
			Collection<Vehicle> vehs = link.getAllVehicles();
			for (Vehicle info : vehs) {
				if (info.getDriver().getId().toString().compareTo(this.agentID) == 0) {
					// we found the little nutty, now lets reason about the lngth of ist activity
					double departure = info.getDepartureTime_s();
					double diff =  departure - info.getLastMovedTime();
					this.finished = (this.now - info.getLastMovedTime()) / diff;
					this.activityNr = i/2;
				}
			}
		}

	}

	public void remove() {
		// TODO Auto-generated method stub

	}

	public void draw(OTFDrawer drawer) {
		// TODO Auto-generated method stub

	}

}
