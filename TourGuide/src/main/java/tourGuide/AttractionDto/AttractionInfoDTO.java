package tourGuide.AttractionDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttractionInfoDTO {
    private String attractionName;
    private double attractionLatitude;
    private double attractionLongitude;
    private double distance;
    private int rewardPoints;

    public AttractionInfoDTO(String attractionName, double attractionLatitude, double attractionLongitude, double distance, int rewardPoints) {
        this.attractionName = attractionName;
        this.attractionLatitude = attractionLatitude;
        this.attractionLongitude = attractionLongitude;
        this.distance = distance;
        this.rewardPoints = rewardPoints;
    }
}
