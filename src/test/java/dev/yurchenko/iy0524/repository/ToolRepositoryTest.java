package dev.yurchenko.iy0524.repository;

import dev.yurchenko.iy0524.entites.ToolEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ToolRepositoryTest {
	@Autowired
	private ToolRepository toolRepository;
	
	@Test
	public void findAll() {
		assertFalse(toolRepository.findAll().isEmpty());
	}
	
	@Test
	public void findById() {
		ToolEntity toolEntity = toolRepository.checkoutWith("CHNS");
		assertNotNull(toolEntity);
		assertEquals("CHNS", toolEntity.getCode());
		assertEquals(1, toolEntity.getId());
		assertNotNull(toolEntity.getToolType());
		assertEquals(2, toolEntity.getToolType().getId());
		assertEquals("Chainsaw", toolEntity.getToolType().getName());
		assertEquals(BigDecimal.valueOf(1.49).setScale(2, RoundingMode.HALF_UP),
				toolEntity.getToolType().getDailyCharge().setScale(2, RoundingMode.HALF_UP));
		assertEquals(false, toolEntity.getToolType().getWeekendCharge());
		assertEquals(true, toolEntity.getToolType().getWeekdayCharge());
		assertEquals(true, toolEntity.getToolType().getHolidayCharge());
	}
	
}