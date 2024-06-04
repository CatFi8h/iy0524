package dev.yurchenko.iy0524.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "TOOL_TYPE")
public class ToolTypeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private BigDecimal dailyCharge;
	@Column(nullable = false)
	private Boolean weekdayCharge;
	@Column(nullable = false)
	private Boolean weekendCharge;
	@Column(nullable = false)
	private Boolean holidayCharge;
}
