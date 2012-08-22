package eu.trentorise.smartcampus.android.common.follow;

import java.io.Serializable;

public class FollowEntityObject implements Serializable {
	private static final long serialVersionUID = 2688483793376258555L;

	private Long entityId;
	private String title;
	private String type;
	
	
	public FollowEntityObject() {
		super();
	}


	public FollowEntityObject(Long entityId, String title, String type) {
		super();
		this.entityId = entityId;
		this.title = title;
		this.type = type;
	}


	public Long getEntityId() {
		return entityId;
	}


	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

}
