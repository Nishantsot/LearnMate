// learm/learn/Dto/CreateCourseRequest.java
package learm.learn.Dto;
import lombok.*;

@Getter @Setter
public class CreateCourseRequest {
    private String title;
    private String description;
    private String category;
    private Double price;
    private Integer durationMinutes;
}
