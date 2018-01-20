package syndred.entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "offset", "length", "style" })
public class InlineStyleRange implements Serializable {

	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("length")
	private Integer length;

	@JsonProperty("style")
	private String style;

	private final static long serialVersionUID = -7052774662304691500L;

	@JsonProperty("offset")
	public Integer getOffset() {
		return offset;
	}

	@JsonProperty("offset")
	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	@JsonProperty("length")
	public Integer getLength() {
		return length;
	}

	@JsonProperty("length")
	public void setLength(Integer length) {
		this.length = length;
	}

	@JsonProperty("style")
	public String getStyle() {
		return style;
	}

	@JsonProperty("style")
	public void setStyle(String style) {
		this.style = style;
	}

}
