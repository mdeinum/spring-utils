package biz.deinum.springframework.webflow1.web.tag;

import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.tags.form.TagWriter;

final class FlowExecutionWriter {

	static void writeTag(TagWriter tagWriter, AbstractWebFlowTag webflowTag) throws JspException {
        tagWriter.startTag("input");
        tagWriter.writeAttribute("type", "hidden");
        tagWriter.writeAttribute("name", webflowTag.getFlowExecutionKeyArgumentName());
        tagWriter.writeAttribute("value", webflowTag.getFlowExecutionKey());
        tagWriter.endTag();

	}
	
}
