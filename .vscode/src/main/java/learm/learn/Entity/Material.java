// learm/learn/Entity/Material.java
package learm.learn.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="materials")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Material {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url; // store S3/local path/link

    @ManyToOne(optional = false)
    private Course course;
}
