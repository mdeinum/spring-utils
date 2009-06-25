package biz.deinum.springframework.webflow1.web.tag;

import javax.servlet.jsp.JspException;

import org.springframework.web.servlet.tags.form.TagWriter;

/**
 * Writes a hidden field containing the flowExecutionKey. The name of the flowExecutionKeyArgumentName
 * is retrieved from the current ExternalContext.
 * 
 * @author Marten Deinum
 * @version 1.0
 */
public class FlowExecutionKeyTag extends AbstractWebFlowTag {

    protected int writeTagContent(TagWriter tagWriter) throws JspException {
    	FlowExecutionWriter.writeTag(tagWriter, this);
        return SKIP_BODY;
    }    
}
