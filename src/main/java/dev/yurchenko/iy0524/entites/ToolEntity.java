package dev.yurchenko.iy0524.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TOOLS")
public class ToolEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
//	@Column(unique = true)
	private String code;
	@ManyToOne(fetch = FetchType.LAZY)
	private ToolTypeEntity toolType;
	@ManyToOne(fetch = FetchType.LAZY)
	private BrandEntity brand;
}
