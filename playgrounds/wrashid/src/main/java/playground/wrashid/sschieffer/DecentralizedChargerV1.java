/* *********************************************************************** *
 * project: org.matsim.*
 * DecentralizedChargerV1.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
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

package playground.wrashid.sschieffer;

import java.util.LinkedList;

import org.matsim.api.core.v01.Id;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.controler.events.AfterMobsimEvent;
import org.matsim.core.controler.listener.AfterMobsimListener;

import playground.wrashid.PSF2.pluggable.energyConsumption.EnergyConsumptionPlugin;
import playground.wrashid.PSF2.pluggable.parkingTimes.ParkingIntervalInfo;
import playground.wrashid.PSF2.pluggable.parkingTimes.ParkingTimesPlugin;
import playground.wrashid.lib.obj.LinkedListValueHashMap;

public class DecentralizedChargerV1 {

	
	
	public void performChargingAlgorithm(DecentralizedChargerInfo chargerInfo){
		// TODO: implement method (main starting point of whole programming exercise...)
		
		// STELLA MAIN
		
		
	}
	
	
	
	public void getElectricityFromGrid(double startChargingTime, double endChargingTime, Id agentId){
		//TODO: adopt (e.g. use default plug power at location)
	}



	public void performChargingAlgorithm(EnergyConsumptionPlugin energyConsumptionPlugin, ParkingTimesPlugin parkingTimesPlugin) {
		LinkedListValueHashMap<Id, Double> energyConsumptionOfLegs = energyConsumptionPlugin.getEnergyConsumptionOfLegs();
		LinkedListValueHashMap<Id, ParkingIntervalInfo> parkingTimeIntervals = parkingTimesPlugin.getParkingTimeIntervals();
		
		LinkedList<Double> legsOfAgent10 = energyConsumptionOfLegs.get(new IdImpl(10));
		
		LinkedList<ParkingIntervalInfo> parkingTimesOfAgent10 = parkingTimeIntervals.get(new IdImpl(10));
		
		System.out.println();
		for (int i=0;i<legsOfAgent10.size();i++){
			System.out.println(legsOfAgent10.get(i));
		}
		
	}

}
