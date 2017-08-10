
package de.uk.spinfo.syndred.draftjs;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "entityMap", "blocks" })
public class RawDraftContentState implements Serializable {

	@JsonProperty("entityMap")
	private EntityMap entityMap;

	@JsonProperty("blocks")
	private List<Block> blocks = null;

	private final static long serialVersionUID = 8697339451658053396L;

	@JsonProperty("entityMap")
	public EntityMap getEntityMap() {
		return entityMap;
	}

	@JsonProperty("entityMap")
	public void setEntityMap(EntityMap entityMap) {
		this.entityMap = entityMap;
	}

	@JsonProperty("blocks")
	public List<Block> getBlocks() {
		return blocks;
	}

	@JsonProperty("blocks")
	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}

}
