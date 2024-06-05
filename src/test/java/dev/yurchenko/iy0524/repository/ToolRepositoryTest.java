package dev.yurchenko.iy0524.repository;

import dev.yurchenko.iy0524.entites.ToolEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ToolRepositoryTest {
	@Autowired
	private ToolRepository toolRepository;
	
	@Test
	public void testToolRepository_findToolByCode_CHNS() {
		Optional<ToolEntity> toolEntityOpt = toolRepository.getToolWithDetailsByCode("CHNS");
		assertTrue(toolEntityOpt.isPresent());
		ToolEntity toolEntity = toolEntityOpt.get();
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
	@Test
	public void testToolRepository_findToolByCode_LADW() {
		Optional<ToolEntity> toolEntityOpt = toolRepository.getToolWithDetailsByCode("LADW");
		assertTrue(toolEntityOpt.isPresent());
		ToolEntity toolEntity = toolEntityOpt.get();
		assertEquals("LADW", toolEntity.getCode());
		assertEquals(2, toolEntity.getId());
		assertNotNull(toolEntity.getToolType());
		assertEquals(1, toolEntity.getToolType().getId());
		assertEquals("Ladder", toolEntity.getToolType().getName());
		assertEquals(BigDecimal.valueOf(1.99).setScale(2, RoundingMode.HALF_UP),
				toolEntity.getToolType().getDailyCharge().setScale(2, RoundingMode.HALF_UP));
		assertEquals(true, toolEntity.getToolType().getWeekendCharge());
		assertEquals(true, toolEntity.getToolType().getWeekdayCharge());
		assertEquals(false, toolEntity.getToolType().getHolidayCharge());
	}
	@Test
	public void testToolRepository_findToolByCode_JAKD() {
		Optional<ToolEntity> toolEntityOpt = toolRepository.getToolWithDetailsByCode("JAKD");
		assertTrue(toolEntityOpt.isPresent());
		ToolEntity toolEntity = toolEntityOpt.get();
		assertEquals("JAKD", toolEntity.getCode());
		assertEquals(3, toolEntity.getId());
		assertNotNull(toolEntity.getToolType());
		assertEquals(3, toolEntity.getToolType().getId());
		assertEquals("Jackhammer", toolEntity.getToolType().getName());
		assertEquals(BigDecimal.valueOf(2.99).setScale(2, RoundingMode.HALF_UP),
				toolEntity.getToolType().getDailyCharge().setScale(2, RoundingMode.HALF_UP));
		assertEquals(false, toolEntity.getToolType().getWeekendCharge());
		assertEquals(true, toolEntity.getToolType().getWeekdayCharge());
		assertEquals(false, toolEntity.getToolType().getHolidayCharge());
	}
	@Test
	public void testToolRepository_findToolByCode_JAKR() {
		Optional<ToolEntity> toolEntityOpt = toolRepository.getToolWithDetailsByCode("JAKR");
		assertTrue(toolEntityOpt.isPresent());
		ToolEntity toolEntity = toolEntityOpt.get();
		assertEquals("JAKR", toolEntity.getCode());
		assertEquals(4, toolEntity.getId());
		assertNotNull(toolEntity.getToolType());
		assertEquals(3, toolEntity.getToolType().getId());
		assertEquals("Jackhammer", toolEntity.getToolType().getName());
		assertEquals(BigDecimal.valueOf(2.99).setScale(2, RoundingMode.HALF_UP),
				toolEntity.getToolType().getDailyCharge().setScale(2, RoundingMode.HALF_UP));
		assertEquals(false, toolEntity.getToolType().getWeekendCharge());
		assertEquals(true, toolEntity.getToolType().getWeekdayCharge());
		assertEquals(false, toolEntity.getToolType().getHolidayCharge());
	}
	
}