package tourGuide.user;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReward {

	private  VisitedLocation visitedLocation;
	private Attraction attraction;
	private int rewardPoints;
	public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
		this.rewardPoints = rewardPoints;
	}
	
	public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
		this.visitedLocation = visitedLocation;
		this.attraction = attraction;
	}

//    public UserReward() {
//
//    }

	public void setRewardPoints(int rewardPoints) {
		this.rewardPoints = rewardPoints;
	}
	
	public int getRewardPoints() {//TODO nous avons 2 methodes getRewardsPoints
		return rewardPoints;
	}
	
}
