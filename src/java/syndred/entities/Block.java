
package syndred.entities;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "key", "text", "type", "depth", "inlineStyleRanges", "entityRanges", "data" })
public class Block implements Serializable {

	@JsonProperty("key")
	private String key;

	@JsonProperty("text")
	private String text;

	@JsonProperty("type")
	private String type;

	@JsonProperty("depth")
	private Integer depth;

	@JsonProperty("inlineStyleRanges")
	private List<Range> inlineStyleRanges = null;

	@JsonProperty("entityRanges")
	private List<Object> entityRanges = null;

	@JsonProperty("data")
	private Data data;

	private final static long serialVersionUID = 2715789708240560557L;

	@JsonProperty("key")
	public String getKey() {
		return key;
	}

	@JsonProperty("key")
	public void setKey(String key) {
		this.key = key;
	}

	@JsonProperty("text")
	public String getText() {
		return text;
	}

	@JsonProperty("text")
	public void setText(String text) {
		this.text = text;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("depth")
	public Integer getDepth() {
		return depth;
	}

	@JsonProperty("depth")
	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	@JsonProperty("inlineStyleRanges")
	public List<Range> getInlineStyleRanges() {
		return inlineStyleRanges;
	}

	@JsonProperty("inlineStyleRanges")
	public void setInlineStyleRanges(List<Range> inlineStyleRanges) {
		this.inlineStyleRanges = inlineStyleRanges;
	}

	@JsonProperty("entityRanges")
	public List<Object> getEntityRanges() {
		return entityRanges;
	}

	@JsonProperty("entityRanges")
	public void setEntityRanges(List<Object> entityRanges) {
		this.entityRanges = entityRanges;
	}

	@JsonProperty("data")
	public Data getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(Data data) {
		this.data = data;
	}

}
