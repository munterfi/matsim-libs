/* *********************************************************************** *
 * project: org.matsim.*
 * LinkLeaveEventTest.java
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

package org.matsim.core.events;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.testcases.MatsimTestUtils;
import org.matsim.vehicles.Vehicle;

/**
 * @author mrieser
 */
public class LinkLeaveEventTest {

	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();


	@Test public void testWriteReadXml() {
		final LinkLeaveEvent event1 = new LinkLeaveEvent(68423.98, Id.create("veh", Vehicle.class),
				Id.create(".235", Link.class));
		final LinkLeaveEvent event2 = XmlEventsTester.testWriteReadXml(utils.getOutputDirectory() + "events.xml", event1);
		assertEquals(event1.getTime(), event2.getTime(), MatsimTestUtils.EPSILON);
		assertEquals(event1.getLinkId(), event2.getLinkId());
		assertEquals(event1.getVehicleId(), event2.getVehicleId());
	}
}
