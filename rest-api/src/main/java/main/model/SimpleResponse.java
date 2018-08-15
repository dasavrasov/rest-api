package main.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * Простой ответ
 * содержит только 1 поле RESULT
 * Если ОК возвращает ОК или Done
 * @author savrasov
 *
 */
@XmlRootElement(name="Response")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class SimpleResponse {

	public SimpleResponse() {
	}

	public SimpleResponse(String result) {
		this.result = result;
	}

	@XmlAttribute(name = "Result")
	private String result;
}
