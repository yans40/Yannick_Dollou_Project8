package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private final GpsUtil gpsUtil;
    private final RewardCentral rewardsCentral;
    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;
    //    int availableProcessors = Runtime.getRuntime().availableProcessors();
//    int poolSize = availableProcessors/2;
//    ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
    private ExecutorService executorServiceCalculateRewards = Executors.newFixedThreadPool(150);

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

    public Future<Void> calculateRewards(User user) {
        return CompletableFuture.supplyAsync(() -> {
            List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
            List<Attraction> attractions = gpsUtil.getAttractions();

            for (VisitedLocation visitedLocation : userLocations) {
                for (Attraction attraction : attractions) {
                    if (user.getUserRewards().stream().noneMatch(reward -> reward.attraction.attractionName.equals(attraction.attractionName))) {
                        if (nearAttraction(visitedLocation, attraction)) {
                            user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
                        }
                    }
                }
            }
            return null;
        }, executorServiceCalculateRewards);
    }

//    public void calculateRewards(User user) {
//
//        List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
//        List<Attraction> attractions = gpsUtil.getAttractions();
//
//        for (VisitedLocation visitedLocation : userLocations) {
//            for (Attraction attraction : attractions) {
//                if (user.getUserRewards().stream().noneMatch(reward -> reward.attraction.attractionName.equals(attraction.attractionName))) {
//                    if (nearAttraction(visitedLocation, attraction)) {
//                        user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
//                    }
//                }
//            }
//        }
//    }

//public void calculateRewards(User user) {
//    CompletableFuture.runAsync(() -> {
//        List<VisitedLocation> userVisitedLocations = new ArrayList<>(user.getVisitedLocations());
//        List<Attraction> attractions = gpsUtil.getAttractions();
//        for (VisitedLocation visitedLocation : userVisitedLocations) {
//            for (Attraction attraction : attractions) {
//                if (nearAttraction(visitedLocation, attraction)) {
//                    user.addUserReward(new UserReward(visitedLocation, attraction,  getRewardPoints(attraction, user)));
//                }
//            }
//        }
//    }, executorServiceCalculateRewards);
//}

//    public CompletableFuture<Void> calculateRewards(User user) {
//        List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
//        List<Attraction> attractions = gpsUtil.getAttractions();
//
//        return CompletableFuture.supplyAsync(() -> {
//            userLocations.forEach(visitedLocation -> {
//                attractions.parallelStream().forEach(attraction -> {
//
//                        if (nearAttraction(visitedLocation, attraction)) {
//                            user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction,user)));
//                        }
//
//                });
//            });
//
//             return null;
//         }, executorServiceCalculateRewards);
//    }

//public Future<Void> calculateRewards(User user) {
//    return CompletableFuture.supplyAsync(() -> {
//        List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
//        List<Attraction> attractions = gpsUtil.getAttractions();
//
//        userLocations.forEach(visitedLocation -> {
//            attractions.parallelStream().forEach(attraction -> {
//                if (user.getUserRewards().stream().noneMatch(reward -> reward.attraction.attractionName.equals(attraction.attractionName))) {
//                    if (nearAttraction(visitedLocation, attraction)) {
//                        user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
//                    }
//                }
//            });
//        });
//
//        return null;
//    }, executorServiceCalculateRewards);
//}


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
//                            boolean isNear = nearAttraction(visitedLocation,attraction);
//                            if (isNear){
//                                user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction,user)));
//                            }
//                        }, executorServiceCalculateRewards));
//                    }
//                }
//            });
//        });
//
//        return null;
//    }

//    public CompletableFuture<Void> calculateRewards(User user) {
//
//        List<VisitedLocation> userLocations = user.getVisitedLocations();
//        List<Attraction> attractions = gpsUtil.getAttractions();
//        return CompletableFuture.supplyAsync(() -> {
//
//            for (VisitedLocation visitedLocation : userLocations)  {
//                attractions.parallelStream().forEach(attraction ->{
//                    if (user.getUserRewards().stream().noneMatch(reward -> reward.attraction.attractionName.equals(attraction.attractionName))) {
//                        if (nearAttraction(visitedLocation, attraction)) {
//                            user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
//                        }
//                    }
//                });
//
//            }
//
//            return null;
//        }, executorServiceCalculateRewards);
//
//    }


    public void awaitCalculateRewardsEnding() {
        executorServiceCalculateRewards.shutdown();
        try {
            if (!executorServiceCalculateRewards.awaitTermination(20, TimeUnit.MINUTES)) {
                executorServiceCalculateRewards.shutdownNow();
            }

        } catch (InterruptedException e) {
            executorServiceCalculateRewards.shutdownNow();
            Thread.currentThread().interrupt();
        }
        executorServiceCalculateRewards = Executors.newFixedThreadPool(150);
    }

    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return getDistance(attraction, location) > attractionProximityRange ? false : true;
    }

    boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
    }

    public int getRewardPoints(Attraction attraction, User user) { //getRewardsPoints
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
