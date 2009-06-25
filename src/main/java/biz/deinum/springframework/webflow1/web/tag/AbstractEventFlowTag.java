package biz.deinum.springframework.webflow1.web.tag;


public abstract class AbstractEventFlowTag extends AbstractWebFlowTag {

	private String eventId;
	
	public final void setEventId(final String eventId) {
		this.eventId=eventId;
	}
	
	protected final String getEventId() {
		return this.eventId;
	}

}
