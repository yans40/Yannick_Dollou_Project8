package tourGuide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;

@Service
public class UserService {

    public void updateUserPreferences(User user) {

        UserPreferences preferences= user.getUserPreferences();
        preferences.setTicketQuantity(3);
        preferences.setTripDuration(3);
        preferences.setNumberOfAdults(3);
        preferences.setNumberOfChildren(3);
    }
}
