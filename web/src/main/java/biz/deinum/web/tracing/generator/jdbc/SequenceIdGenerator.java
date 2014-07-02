package biz.deinum.web.tracing.generator.jdbc;

import biz.deinum.web.tracing.IdGenerator;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.servlet.ServletRequest;

/**
 * Created by in329dei on 2-7-2014.
 */
public class SequenceIdGenerator implements IdGenerator {

	private DataFieldMaxValueIncrementer incrementer;

	@Override
	public String generate(ServletRequest request) {
		return incrementer.nextStringValue();
	}
}
