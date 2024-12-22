package vis.backend.demo.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    //@CreatedDate
    @CreationTimestamp
    @Column(updatable = false) // 수정시 관여x
    private LocalDateTime createdAt;

    //@LastModifiedDate
    @UpdateTimestamp
    @Column(insertable = false) // 삽입시 관여x
    private LocalDateTime updatedAt;
}
