/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package biz.deinum.springframework.batch.item.excel;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.WorkbookParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import biz.deinum.jxl.util.JxlUtils;

/**
 * {@link ItemReader} implementation which uses the JExcelApi to read an Excel
 * file. It will read the file sheet for sheet and row for row. It is based on
 * the {@link org.springframework.batch.item.file.FlatFileItemReader}
 * 
 * @author Marten Deinum
 * 
 * @param <T> the type
 */
public class ExcelItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements
		ResourceAwareItemReaderItemStream<T>, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelItemReader.class);

	private Resource resource;

	private Workbook workbook;

	private int linesToSkip = 0;

	private int currentRow = 0;
	private int currentSheet = 0;

	private RowMapper<T> rowMapper;

	private RowCallbackHandler skippedRowsCallback;
	private boolean noInput = false;

	private boolean strict = true;

	public ExcelItemReader() {
		super();
		this.setName(ClassUtils.getShortName(ExcelItemReader.class));
	}

	@Override
	protected T doRead() throws Exception {
		if (this.noInput) {
			return null;
		}
		final Sheet sheet = this.workbook.getSheet(this.currentSheet);
		final Cell[] row = this.readRow(sheet);
		if (JxlUtils.isEmpty(row)) {
			this.currentSheet++;
			if (this.currentSheet >= this.workbook.getNumberOfSheets()) {
				LOG.debug("No more sheets in {}.", this.resource.getDescription());
				return null;
			} else {
				this.currentRow = 0;
				this.openSheet();
				return this.doRead();
			}
		} else {
			try {
				return this.rowMapper.mapRow(sheet, row, this.currentRow);
			} catch (final Exception e) {
				throw new ExcelFileParseException("Exception parsing Excel file.", e, this.resource.getDescription(),
						sheet.getName(), this.currentRow, JxlUtils.extractContents(row));
			}
		}
	}

	@Override
	protected void doOpen() throws Exception {
		Assert.notNull(this.resource, "Input resource must be set");
		this.workbook = WorkbookParser.getWorkbook(this.resource.getInputStream());
		this.noInput = true;
		if (!this.resource.exists()) {
			if (this.strict) {
				throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode): "
						+ this.resource);
			}
			LOG.warn("Input resource does not exist {}", this.resource.getDescription());
			return;
		}

		if (!this.resource.isReadable()) {
			if (this.strict) {
				throw new IllegalStateException("Input resource must be readable (reader is in 'strict' mode): "
						+ this.resource);
			}
			LOG.warn("Input resource is not readable {}", this.resource.getDescription());
			return;
		}

		this.noInput = false;
		LOG.debug("Opened workbook [{}] with {} sheets.", this.resource.getFilename(),
				this.workbook.getNumberOfSheets());
		this.openSheet();
	}

	private Cell[] readRow(final Sheet sheet) {
		this.currentRow++;
		if (this.currentRow < sheet.getRows()) {
			return sheet.getRow(this.currentRow);
		}
		return null;
	}

	private void openSheet() {
		final Sheet sheet = this.workbook.getSheet(this.currentSheet);
		LOG.debug("Opening sheet {}.", sheet.getName());
		for (int i = 0; i < this.linesToSkip; i++) {
			final Cell[] row = this.readRow(sheet);
			if (this.skippedRowsCallback != null) {
				this.skippedRowsCallback.handleRow(sheet, row);
			}
		}
		LOG.debug("Openend sheet {}, with {} rows.", sheet.getName(), sheet.getRows());

	}

	@Override
	protected void doClose() throws Exception {
		if (this.workbook != null) {
			this.workbook.close();
		}
	}

	public void setResource(final Resource resource) {
		this.resource = resource;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.rowMapper, "RowMapper must be set");
	}

	/**
	 * Set the number of lines to skip. This number is applied to all worksheet
	 * in the excel file! default to 0
	 * 
	 * @param linesToSkip
	 */
	public void setLinesToSkip(final int linesToSkip) {
		this.linesToSkip = linesToSkip;
	}

	/**
	 * In strict mode the reader will throw an exception on
	 * {@link #open(org.springframework.batch.item.ExecutionContext)} if the input resource does not exist.
	 * @param strict true by default
	 */
	public void setStrict(final boolean strict) {
		this.strict = strict;
	}

	public void setRowMapper(final RowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	public void setSkippedRowsCallback(final RowCallbackHandler skippedRowsCallback) {
		this.skippedRowsCallback = skippedRowsCallback;
	}
}
