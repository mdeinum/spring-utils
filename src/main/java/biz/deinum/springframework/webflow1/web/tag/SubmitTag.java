package biz.deinum.springframework.webflow1.web.tag;

import javax.servlet.jsp.JspException;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.TagWriter;

/**
 * This Tag writes a submit button according to the specification of the event
 * submit button as described by Spring Web Flow. Optionally a hidden
 * flowExecutionKey field can also be written.
 * 
 * @author Marten Deinum (marten.deinum@conspect.nl)
 * @version 1.1
 */
public class SubmitTag extends AbstractEventFlowTag {

	/** defines if a hidden flowExectionKey field must be written, default false */
	private boolean writeFlowExecutionKey = false;

	/** The (default) value for the submit button */
	private String value = "Submit";

	/** The key to use to resolve the value to use for the submit button */
	private String valueKey = null;

	protected int writeTagContent(TagWriter tagWriter) throws JspException {

		if (writeFlowExecutionKey) {
			FlowExecutionWriter.writeTag(tagWriter, this);
		}

		tagWriter.startTag("input");
		tagWriter.writeAttribute("type", "submit");
		tagWriter.writeAttribute("name", getName());
		tagWriter.writeAttribute("value", getValue());
		writeDefaultAttributes(tagWriter);
		tagWriter.endTag();
		return SKIP_BODY;
	}

	/**
	 * Gets the value to use in the submit button. When only the value is set 
	 * this is used as the label. When also the {@link #valueKey} is set, the
	 * label is retrieved from the messageSource and the given value is used as 
	 * the message.
	 * 
	 * @return
	 */
	protected String getValue() {
		String resolvedValue = value;
		if (StringUtils.hasText(valueKey)) {
				resolvedValue = getRequestContext().getMessage(valueKey, value);
		}
		return resolvedValue;
	}

	protected String getName() {
		return getEventIdArgumentName() + "_" + getEventId();
	}

	public void setValue(final String value) {
		Assert.notNull(value, "'value' cannot be null.");
		this.value = value;
	}

	public void setValueKey(final String valueKey) {
		this.valueKey = valueKey;
	}

	public void setWriteFlowExecutionKey(final String writeFlowExecutionKey) {
		this.writeFlowExecutionKey = Boolean.parseBoolean(writeFlowExecutionKey);
	}
}
