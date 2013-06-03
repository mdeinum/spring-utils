package biz.deinum.multitenant.batch.item.excel.poi;

import biz.deinum.multitenant.batch.item.excel.AbstractExcelItemReader;
import biz.deinum.multitenant.batch.item.excel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;

public class PoiItemReader<T> extends AbstractExcelItemReader<T> {

    private Workbook workbook;

    @Override
    protected Sheet getSheet(final int sheet) {
        return new PoiSheet(this.workbook.getSheetAt(sheet));
    }

    @Override
    protected int getNumberOfSheets() {
        return this.workbook.getNumberOfSheets();
    }

    @Override
    protected void openExcelFile(final Resource resource) throws Exception {
        this.workbook = new HSSFWorkbook(resource.getInputStream());
    }

    @Override
    protected void doClose() throws Exception {

    }

}
