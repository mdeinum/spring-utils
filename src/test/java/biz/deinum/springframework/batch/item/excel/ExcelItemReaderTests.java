package biz.deinum.springframework.batch.item.excel;

import jxl.Cell;
import jxl.Sheet;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import biz.deinum.springframework.batch.item.excel.ExcelItemReader;
import biz.deinum.springframework.batch.item.excel.RowCallbackHandler;
import biz.deinum.springframework.batch.item.excel.mapping.PassThroughRowMapper;

public class ExcelItemReaderTests  {

	private ExcelItemReader itemReader;
	
	
	@Before
	public void setup() throws Exception {
		itemReader = new ExcelItemReader();
		itemReader.setLinesToSkip(1); //First line is column names
		itemReader.setResource(new ClassPathResource("/MAP-ICONS.xls"));
		itemReader.setRowMapper(new PassThroughRowMapper());
		itemReader.setSkippedRowsCallback(new RowCallbackHandler() {
			
			public void handleRow(Sheet sheet, Cell[] row) {
				System.out.println("Skipping: " + row);
			}
		});
		itemReader.afterPropertiesSet();
		itemReader.doOpen();
	}
	
	@After
	public void after() throws Exception {
		itemReader.doClose();
	}
	
	@Test
	public void readExcelFile() throws Exception {
		Cell[] row = null;
		do {
			row = (Cell[]) itemReader.read();
		} while ( row != null);
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testRequiredProperties() throws Exception {
		ExcelItemReader reader = new ExcelItemReader();
		reader.afterPropertiesSet();
	}

}
