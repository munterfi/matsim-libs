package playground.dhosse.prt.task;

import java.util.List;

import org.matsim.contrib.dvrp.router.VrpPathWithTravelData;

import playground.michalm.taxi.data.TaxiRequest;
import playground.michalm.taxi.schedule.TaxiPickupDriveTask;

public class MPPickupDriveTask extends TaxiPickupDriveTask{

	List<TaxiRequest> requests;
	
	public MPPickupDriveTask(VrpPathWithTravelData path, List<TaxiRequest> requests) {
		
		super(path, requests.get(0));
		this.requests = requests;
		
		for(TaxiRequest request : requests){
			request.setPickupDriveTask(this);
		}
	}
	
	@Override
    public void removeFromRequest()
    {
    	for(TaxiRequest request : this.requests){
    		request.setPickupDriveTask(null);
    	}
    }


    @Override
    public TaxiTaskType getTaxiTaskType()
    {
        return TaxiTaskType.PICKUP_DRIVE;
    }

    public List<TaxiRequest> getRequests()
    {
        return requests;
    }


    @Override
    protected String commonToString()
    {
        return "[" + getTaxiTaskType().name() + "]" + super.commonToString();
    }

	@Override
	public TaxiRequest getRequest() {
		return this.requests.get(0);
	}
	
	public void appendRequest(TaxiRequest request){
		this.requests.add(request);
		request.setPickupDriveTask(this);
	}

}