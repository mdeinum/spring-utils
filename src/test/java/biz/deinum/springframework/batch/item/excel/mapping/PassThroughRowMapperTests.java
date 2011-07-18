package biz.deinum.springframework.batch.item.excel.mapping;

import static org.junit.Assert.assertEquals;
import jxl.Cell;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@link PassThroughRowMapper}.
 * 
 * @author Marten Deinum
 *
 */
public class PassThroughRowMapperTests {

	private final PassThroughRowMapper rowMapper = new PassThroughRowMapper();

	@Test
	public void mapRowShouldReturnSameValues() throws Exception {
		final Cell c1 = Mockito.mock(Cell.class, "c1");
		final Cell c2 = Mockito.mock(Cell.class, "c2");
		final Cell[] row = new Cell[] { c1, c2 };

		assertEquals(row, this.rowMapper.mapRow(null, row, 0));
	}

}
