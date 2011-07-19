package biz.deinum.springframework.batch.item.excel.jxl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.ClassPathResource;

import biz.deinum.springframework.batch.item.excel.RowCallbackHandler;
import biz.deinum.springframework.batch.item.excel.Sheet;
import biz.deinum.springframework.batch.item.excel.mapping.PassThroughRowMapper;

public class JxlItemReaderTests {

    private JxlItemReader itemReader;

    @Before
    public void setup() throws Exception {
        this.itemReader = new JxlItemReader();
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
        this.itemReader.doClose();
    }

    @Test
    public void readExcelFile() throws Exception {
        String[] row = null;
        do {
            row = (String[]) this.itemReader.read();
        } while (row != null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequiredProperties() throws Exception {
        final JxlItemReader reader = new JxlItemReader();
        reader.afterPropertiesSet();
    }

}
