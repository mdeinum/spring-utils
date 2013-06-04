package biz.deinum.multitenant.batch.item.excel.poi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import biz.deinum.multitenant.batch.item.excel.RowCallbackHandler;
import biz.deinum.multitenant.batch.item.excel.Sheet;
import biz.deinum.multitenant.batch.item.excel.mapping.PassThroughRowMapper;

public class PoiItemReaderXlsTests {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PoiItemReader itemReader;

    @Before
    public void setup() throws Exception {
        this.itemReader = new PoiItemReader();
        this.itemReader.setLinesToSkip(1); //First line is column names
        this.itemReader.setResource(new ClassPathResource("/MAP-ICONS.xls"));
        this.itemReader.setRowMapper(new PassThroughRowMapper());
        this.itemReader.setSkippedRowsCallback(new RowCallbackHandler() {

            public void handleRow(final Sheet sheet, final String[] row) {
                System.out.println("Skipping: " + row);
            }
        });
        this.itemReader.afterPropertiesSet();
        this.itemReader.open(new ExecutionContext());
    }

    @After
    public void after() throws Exception {
        this.itemReader.close();
    }

    @Test
    public void readExcelFile() throws Exception {
        String[] row = null;
        do {
            row = (String[]) this.itemReader.read();
            this.logger.debug("Read: {}", StringUtils.arrayToCommaDelimitedString(row));
        } while (row != null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequiredProperties() throws Exception {
        final PoiItemReader reader = new PoiItemReader();
        reader.afterPropertiesSet();
    }

}
