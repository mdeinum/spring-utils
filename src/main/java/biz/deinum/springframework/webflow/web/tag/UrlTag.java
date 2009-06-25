package biz.deinum.springframework.webflow.web.tag;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.TagWriter;

public class UrlTag extends AbstractEventFlowTag {
    private Logger logger = LoggerFactory.getLogger(UrlTag.class);
    private String flowId;
    private String url;
    
    private boolean noExecutionKey = false;
    private String paramName;
    private String paramValue;
    private Map params = Collections.EMPTY_MAP;
    private String target;
    
    public UrlTag() {
        super();
    }

    
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    
    private String getUrl() throws JspException {
        URLBuilder builder = new URLBuilder(this.url);
        addParam(builder, this.paramName, this.paramValue);
        
        Iterator paramIter = params.entrySet().iterator();
        
        while (paramIter.hasNext()) {
        	Entry entry = (Entry) paramIter.next();
        	String key = (String) entry.getKey();
        	Object value = entry.getValue();
        	addParam(builder, key, value);
        }
        
        if (StringUtils.hasLength(flowId)) {
            builder.addParam(getFlowIdArgumentName(), flowId);
        }

        if (StringUtils.hasLength(getEventId())) {
            builder.addParam(getEventIdArgumentName(), getEventId());
        }

        if (!noExecutionKey && StringUtils.hasLength(getFlowExecutionKey())) {
            builder.addParam(getFlowExecutionKeyArgumentName(), getFlowExecutionKey());
        }
        return builder.getURL();
    }
    
    protected int writeTagContent(TagWriter tagWriter) throws JspException {

        String printUrl = getUrl();
        if (logger.isDebugEnabled()) {
            logger.debug("Writing url [{}]", printUrl);
        }
        
        tagWriter.startTag("a");
        tagWriter.writeAttribute("href", printUrl);
        writeOptionalAttribute(tagWriter, "target", target);
        writeDefaultAttributes(tagWriter);
        
        
        tagWriter.forceBlock();
        return EVAL_BODY_INCLUDE;
    }
    
    public int doEndTag() throws JspException {
        tagWriter.endTag(); 
        return EVAL_PAGE;
    }

    protected void initTag() throws Exception {
        Assert.notNull(url, "URL must be set!");
        if (!StringUtils.hasLength(flowId) && !StringUtils.hasLength(getFlowExecutionKey())) {
            throw new IllegalStateException("Either flowId or executionKey must be set!");
        }
    }
    
    private void addParam(final URLBuilder builder, final String paramName, final Object paramValue) throws JspException {
        String value = getDisplayString(evaluate(paramName, paramValue));
        builder.addParam(paramName, value);        

    }

    public void setFlowId(final String flowId) {
        this.flowId = flowId;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
    
    public void setParams(Map params) {
        this.params=params;
    }

    public void setNoExecutionKey(final String noExecutionKey) {
        this.noExecutionKey = Boolean.valueOf(noExecutionKey).booleanValue();
    }
    
    public void setTarget(final String target) {
    	this.target=target;
    }

}
