package main.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Ответ на запрос проверки оплаты
 * Если найдено, возвращает реквизиты документа
 * @author savrasov
 *
 */
@ApiModel()
@JacksonXmlRootElement(localName = "Payment")
@Data
public class PaymentResponse {

	@JacksonXmlProperty(localName="Summa")
	private String summa;

	@JacksonXmlProperty(localName="DateProv")
	private String dateProv;

	@JacksonXmlProperty(localName="NumDoc")
	private String numDoc;

	@JacksonXmlProperty(localName="DocId")
	private String docId;

	@JacksonXmlProperty(localName="PayerInn")
	private String payerInn;
	
	@JacksonXmlProperty(localName="Payer")
	private String payer;

	@JacksonXmlProperty(localName="Details")
	private String details;
	
	@JacksonXmlProperty(localName="AddInfo")
	private String addInfo;
		

	public PaymentResponse() {
	}

	public PaymentResponse(String summa, String dateProv, String numDoc, String docId, String payerInn, String payer, String details, String addInfo) {		
		this.summa = summa;
		this.dateProv = dateProv;
		this.numDoc = numDoc;
		this.docId = docId;
		this.payerInn = payerInn;
		this.payer = payer;
		this.details = details;
		this.addInfo = addInfo;		
	}		
	
}
