package tourGuide;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tourGuide.service.TourGuideService;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tripPricer.Provider;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    @Autowired
    UserService userService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) throws ExecutionException, InterruptedException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    //  TODO: Change this method to no longer return a List of Attractions.
    //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
    //  Return a new JSON object that contains:
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the attractions.
    // The reward points for visiting each Attraction.
    //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) throws ExecutionException, InterruptedException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation,userName));
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        // TODO: Get a list of every user's most recent location as JSON
        //- Note: does not use gpsUtil to query for their current location,
        //        but rather gathers the user's current location from their stored location history.
        //
        // Return object should be the just a JSON mapping of userId to Locations similar to:
        //     {
        //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
        //        ...
        //     }
        List<User> userList = tourGuideService.getAllUsers();
        Map<UUID, Location> userLocationMap = new HashMap<>();

        for (User user : userList) {
            userLocationMap.put(user.getUserId(), user.getLastVisitedLocation().location);
        }

        Map<String, Location> finalUserLocationMap = new HashMap<>();

        for (Map.Entry<UUID, Location> entry : userLocationMap.entrySet()) {
            String key = entry.getKey().toString();
            Location value = entry.getValue();
            finalUserLocationMap.put(key, value);
        }

        return JsonStream.serialize(finalUserLocationMap);
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    @PostMapping("/setUserPreferences")
    public void updateUserPreferences(@RequestParam String userName){

        userService.updateUserPreferences(getUser(userName));

    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }

    @PutMapping("/userPreferences/{userName}")
    public ResponseEntity<?> putUserPreferences(@PathVariable String userName, @RequestBody UserPreferences userPreferences) {

        try {
            User userSaved = tourGuideService.updateUserPreferences(userName, userPreferences);
            System.out.println(userSaved.getUserPreferences().getNumberOfChildren());// ligne qui me permet de voir que les changements sont bien réalisé
            return ResponseEntity.status(HttpStatus.OK).body(userSaved);
        } catch (NoSuchElementException e) {
            String logAndBodyMessage = "error while putting user because missing user with userName=" + userName;

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(logAndBodyMessage);
        }
    }
}