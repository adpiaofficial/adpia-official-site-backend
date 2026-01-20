package org.adpia.official.domain.member;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 30)
	private String name;                // 이름

	@Column(nullable = false, length = 30)
	private String department;          // 부서

	@Column(nullable = false, unique = true, length = 50)
	private String email;               // 이메일

	@Column(nullable = false)
	private String password;            // 비밀번호

	@Column(length = 10)
	private String gender;              // 성별

	@Column(columnDefinition = "int default 0")
	private int generation;             // 기수

	@Column(length = 20)
	private String grade; // 회장단, 활동기수, OB

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private MemberRole role;            // 권한

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;    // 생성일

	private LocalDateTime updatedAt;    // 수정일

	@Column(nullable = false)
	private Boolean active = true;      // 활성 여부
}