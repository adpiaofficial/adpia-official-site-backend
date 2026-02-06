package org.adpia.official.domain.executive;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "executive_members",
	indexes = {
		@Index(name = "idx_exe_active", columnList = "active"),
		@Index(name = "idx_exe_group_order", columnList = "groupTitle, orderIndex")
	})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutiveMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String groupTitle;

	@Column(nullable = false, length = 30)
	private String role;

	@Column(nullable = false, length = 10)
	private String generation;

	@Column(nullable = false, length = 20)
	private String department;

	@Column(nullable = false, length = 20)
	private String name;

	@Column(length = 500)
	private String imageUrl;

	@Column(nullable = false)
	private int orderIndex;

	@Column(nullable = false)
	private boolean active;
}
