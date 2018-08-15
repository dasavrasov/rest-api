package main.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;


@JacksonXmlRootElement(localName="Response")
@Data
public class PaymentResponseWrapper {
	
	@JsonProperty(value="result")
	@JacksonXmlProperty(localName="Payment")
	@JacksonXmlElementWrapper(useWrapping=false)
	@JsonInclude(JsonInclude.Include.ALWAYS)
	private List<PaymentResponse> entry;
	
	private String addInfo;

	public PaymentResponseWrapper(List<PaymentResponse> entry, String addInfo) {
		super();
		this.entry = entry;
		this.addInfo = addInfo;
	}
	
}
