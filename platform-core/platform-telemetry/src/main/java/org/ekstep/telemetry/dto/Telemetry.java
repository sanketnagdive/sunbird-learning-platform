package org.ekstep.telemetry.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author mahesh
 *
 */

@JsonInclude(Include.NON_NULL)
public class Telemetry {

	public Telemetry(String eid, long ets, String ver, Actor actor, Context context,
			Map<String, Object> edata) {
		super();
		this.eid = eid;
		this.ets = ets;
		this.ver = ver;
		this.actor = actor;
		this.context = context;
		this.edata = edata;
	}

	public Telemetry(String eid, Actor actor, Context context, Map<String, Object> edata) {
		super();
		this.eid = eid;
		this.actor = actor;
		this.context = context;
		this.edata = edata;
	}

	String eid;
	long ets = System.currentTimeMillis();
	String ver = "3.0";
	String mid = "LP." + System.currentTimeMillis() + "." + UUID.randomUUID();;
	Actor actor;
	Context context;
	Target object;
	@JsonInclude(Include.NON_NULL)
	Map<String, Object> edata;
	List<String> tags;

	/**
	 * @return the eid
	 */
	public String getEid() {
		return eid;
	}

	/**
	 * @param eid
	 *            the eid to set
	 */
	public void setEid(String eid) {
		this.eid = eid;
	}

	/**
	 * @return the ets
	 */
	public long getEts() {
		return ets;
	}

	/**
	 * @param ets
	 *            the ets to set
	 */
	public void setEts(long ets) {
		this.ets = ets;
	}

	/**
	 * @return the ver
	 */
	public String getVer() {
		return ver;
	}

	/**
	 * @param ver
	 *            the ver to set
	 */
	public void setVer(String ver) {
		this.ver = ver;
	}

	/**
	 * @return the mid
	 */
	public String getMid() {
		return mid;
	}

	/**
	 * @param mid
	 *            the mid to set
	 */
	public void setMid(String mid) {
		this.mid = mid;
	}

	/**
	 * @return the actor
	 */
	public Actor getActor() {
		return actor;
	}

	/**
	 * @param actor
	 *            the actor to set
	 */
	public void setActor(Actor actor) {
		this.actor = actor;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @return the object
	 */
	public Target getObject() {
		return object;
	}

	/**
	 * @param object
	 *            the object to set
	 */
	public void setObject(Target object) {
		this.object = object;
	}

	/**
	 * @return the edata
	 */
	public Map<String, Object> getEdata() {
		return edata;
	}

	/**
	 * @param edata
	 *            the edata to set
	 */
	public void setEdata(Map<String, Object> edata) {
		this.edata = edata;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}