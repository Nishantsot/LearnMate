// learm/learn/Dto/ScheduleClassRequest.java
package learm.learn.Dto;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
public class ScheduleClassRequest {
    private Long courseId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
