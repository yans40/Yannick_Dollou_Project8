package tourGuide.AttractionDto;

import gpsUtil.location.Attraction;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NearByAttractionDto {
    private List<AttractionInfoDTO> attractionInfoDTOS;
    private double userLatitude;
    private double userLongitude;


    public NearByAttractionDto(List<AttractionInfoDTO> attractionInfoDTOS, double userLatitude, double userLongitude) {
        this.attractionInfoDTOS = attractionInfoDTOS;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }
}
