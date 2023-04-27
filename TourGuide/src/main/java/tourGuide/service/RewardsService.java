package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static sun.management.Agent.error;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private final GpsUtil gpsUtil;
    private final RewardCentral rewardsCentral;
    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;

    private ExecutorService executorService = Executors.newFixedThreadPool(50);

    public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
        this.gpsUtil = gpsUtil;
        this.rewardsCentral = rewardCentral;
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

//    	public Void calculateRewards(User user) {
//
//		List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
//		List<Attraction> attractions = gpsUtil.getAttractions();
//
//		for(VisitedLocation visitedLocation : userLocations) {
//			for(Attraction attraction : attractions) {
//				if(user.getUserRewards().stream().noneMatch(reward -> reward.attraction.attractionName.equals(attraction.attractionName))) {
//					if(nearAttraction(visitedLocation, attraction)) {
//						user.addUserReward(new UserReward(visitedLocation, attraction, getRewPoints(attraction,user)));
//					}
//				}
//			}
//		}
//        return null;
//    }

    public void calculateRewards(User user) {
        List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
        List<Attraction> attractions = gpsUtil.getAttractions();

         CompletableFuture.supplyAsync(() -> {
            userLocations.forEach(visitedLocation -> {
                attractions.parallelStream().forEach(attraction -> {
                    if (user.getUserRewards().stream().noneMatch(reward -> reward.attraction.attractionName.equals(attraction.attractionName))) {
                        if (nearAttraction(visitedLocation, attraction)) {
                            user.addUserReward(new UserReward(visitedLocation, attraction, getRewPoints(attraction,user)));
                        }
                    }
                });
            });

             return null;
         },executorService);
    }





//    public Future<Void> calculateRewards(User user) {
//        List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
//        List<Attraction> attractions = gpsUtil.getAttractions();
//        List<CompletableFuture<Void>> futures = new ArrayList<>();
//
//        userLocations.forEach(visitedLocation -> {
//            attractions.forEach(attraction -> {
//                if (user.getUserRewards().stream().noneMatch(reward -> reward.attraction.attractionName.equals(attraction.attractionName))) {
//                    if (nearAttraction(visitedLocation, attraction)) {
//                        futures.add(CompletableFuture.runAsync(() -> {
//                        user.addUserReward(new UserReward(visitedLocation, attraction, getRewPoints(attraction,user)));
//                        }, executorService));
//                    }
//                }
//            });
//        });
//
//        return null;
//    }

//    public void calculateRewardsAwaitTerminationAfterShutdown() {
//
//        executorService.shutdown();
//
//        try {
//
//            if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
//
//                executorService.shutdownNow();
//
//            }
//
//        } catch (InterruptedException ex) {
//
//            executorService.shutdownNow();
//
//            Thread.currentThread().interrupt();
//
//        }
//
//
//
//    }



//    public Future<Void> calculateRewards(User user) {
//
//        return CompletableFuture.supplyAsync(() -> {
//
//            List<VisitedLocation> userLocations = user.getVisitedLocations();
//
//            List<Attraction> attractions = gpsUtil.getAttractions();
//
//            for (VisitedLocation visitedLocation : userLocations) {
//
//                attractions.parallelStream().forEach(attraction -> {
//
//                    if (user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))){
//
//                        boolean isNear = nearAttraction(visitedLocation, attraction);
//
//                        if (isNear) {
//
//                            user.addUserReward(new UserReward(visitedLocation, attraction, getRewPoints(attraction, user)));
//
//                        }
//
//                    }
//
//                });
//
//            }
//
//            return null;
//
//        }, executorService);
//
//    }

    public void awaitCalculateRewardsEnding() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(20, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }

        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        executorService = Executors.newFixedThreadPool(100);
    }

    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return getDistance(attraction, location) > attractionProximityRange ? false : true;
    }

    boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
    }

    public int getRewPoints(Attraction attraction, User user) { //getRewardsPoints
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
    }

    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }


}
