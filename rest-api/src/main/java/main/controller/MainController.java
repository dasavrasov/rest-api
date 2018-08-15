package main.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import main.model.PaymentResponse;
import main.model.PaymentResponseWrapper;
import main.model.SimpleResponse;
import main.service.DatabaseService;

@RestController
//@RequestMapping("/api")
@Api(description="Api description", tags="example")
public class MainController {

	Logger log = LoggerFactory.getLogger(MainController.class);

	@Autowired
	DatabaseService myService;

	/**
	 * Тестовый endPoint для проверки работоспособности сервиса В ответ получает ОК
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public ResponseEntity<SimpleResponse> testSimple() {
		ResponseEntity<SimpleResponse> entity = new ResponseEntity<SimpleResponse>(new SimpleResponse("Ok"),
				HttpStatus.OK);
		return entity;
	}

	/**
	 * Тестовый endPoint для проверки работоспособности сервиса Обращается в DATABASE к
	 * процедуре Test_Connection В ответ получает ОК
	 */
	@RequestMapping(value = "/testDATABASE", method = RequestMethod.GET)
	public ResponseEntity<SimpleResponse> testConnection() {
		log.info("In Controller /testDATABASE");
		
		ResponseEntity<SimpleResponse> entity = null;
		try {
			SimpleResponse result = myService.testConnection();

			entity = new ResponseEntity<SimpleResponse>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}

		return entity;
	}

	/**
	 * Проверка оплаты
	 * 
	 * @param accNum
	 *            - Номер счета (не обязаетльно, если не указан, то берется номер
	 *            счета из настройки ИБСО)
	 * @param requestNum
	 *            - Номер продукта в назначении платежа
	 * @param start
	 *            - Начальная Дата проводки
	 * @param end
	 *            - Конечная дата проводки (не обязательно)
	 * @return
	 */
	@ApiOperation(value = "Получение информации об оплате комиссии по продукта")	
	@RequestMapping(value = "/example/payments", method = RequestMethod.GET, produces = { "application/json;charset=utf-8",
			"application/xml;charset=utf-8" })
	@ResponseBody
	public ResponseEntity<PaymentResponseWrapper> checkCustomEntityPay(
			@ApiParam(value= "Номер счета. Выбираются платежи по кредиту этого счета.Если параметр не указан, берется счет по умолчанию из настройки в систем. Рекомендуется счет указывать явно", required = false)
			@RequestParam(name = "acc_num", required = false) String accNum,
			@ApiParam(value="Фильтр по назначению платежа. Строковый параметр, проверяется вхождение строки, указанной в параметре request_num в назначении платежа. Если в назначении платежа есть номер заявки или номер продукта, можно использовать для поиска. Если параметр не указан, назначение платежа не проверяется", required = false)
			@RequestParam(name = "request_num", required = false) String requestNum,
			@ApiParam(value="Инн плательщика. Если указан, выбираются платежи, у которых ИНН плательщика = payer_inn", required = false)
			@RequestParam(name = "payer_inn", required = false) String payerInn,
			@ApiParam(value="Дата начала поиска. Формат YYYY-MM-DD", required = true)
			@RequestParam(name = "begin_date", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") String beginDate,
			@ApiParam(value="Дата окончания поиска. Формат YYYY-MM-DD. Если не указано, выбираются платежи за begin_date. Если указано, выбираются платежи с датой проводки >=begin_date и <=end_date. Максимальный интервал для поиска – 10 дней. Если указан интервал > 10 дней, выдается ошибка", required = false)
			@RequestParam(name = "end_date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate,
			@ApiParam(name="match", value="Дополнительный фильтр по уже сквитованным документам оплаты. Если указано значение 'new', то из выборки исключаются документы, по которым уже есть отметка о квитовке", required = false, allowableValues="new, all")
			@RequestParam(name = "match", required = false) String match			
			) {

		log.debug("----------------------------------------------------------------------------\n");
		String dbg="Получен запрос GET:/example/payments:Params:\n"+
		"acc_num:"+accNum+"\n"+
		"request_num:"+requestNum+"\n"+
		"payer_inn:"+payerInn+"\n"+
		"begin_date:"+beginDate+"\n"+
		"end_date:"+endDate+"\n";
		
		log.debug(dbg);
				
		if (!StringUtils.isEmpty(endDate)) {
			// check date interval
			// Если интервал дат > 10 дней - отлуп
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate dBeg = LocalDate.parse(beginDate, formatter);
				LocalDate dEnd = LocalDate.parse(endDate, formatter);
				long days = ChronoUnit.DAYS.between(dBeg, dEnd);
				PaymentResponseWrapper result=DatabaseService.emptyPayResponse("Интервал дат не может быть более 10 дней");
				if (Math.abs(days) > 10) {
					log.info("Ошибка! checkCustomEntityPay:"+"Интервал дат не может быть более 10 дней");
					return new ResponseEntity<PaymentResponseWrapper>(
							result, HttpStatus.OK);
				}
			} catch (Exception e1) {
				log.info("Ошибка! checkCustomEntityPay:"+e1.getMessage());
				PaymentResponseWrapper result=DatabaseService.emptyPayResponse("Интервал дат не может быть более 10 дней");
				log.info("Ошибка! checkCustomEntityPay:"+"Интервал дат не может быть более 10 дней");
				return new ResponseEntity<PaymentResponseWrapper>(result,
						HttpStatus.OK);
			}
		}


		try {
			List<PaymentResponse> entity = myService.checkCustomEntityPay(accNum, requestNum, payerInn, beginDate,
					endDate, match);
			PaymentResponseWrapper result=new PaymentResponseWrapper(entity,"");
			log.debug("checkCustomEntityPay:->OK");
			return(new ResponseEntity<PaymentResponseWrapper>(result, HttpStatus.OK));
		} catch (Exception e) {
			List<PaymentResponse> list = new ArrayList<PaymentResponse>();			
			PaymentResponseWrapper result = new PaymentResponseWrapper(list,e.getMessage()); 
			log.info("Ошибка! checkCustomEntityPay:"+e.getMessage());
			return new ResponseEntity<PaymentResponseWrapper>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Запись готовой сделки в DATABASE
	 * 
	 * @param xmlobj
	 * @return
	 */
	@ApiOperation(value = "Экспорт сделки")	
	@RequestMapping(value = "/example/export/{searchkey}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SimpleResponse> saveCustomEntity(
			@ApiParam(value="Содержание Body", required = false, format = "Text in XML format")
			@RequestBody String xmlobj,
			@ApiParam(value="Ключ для поиска. Ключ должен содержать ID заявки (УИН)", required = true)
			@PathVariable(name = "searchkey", required = true) String searchkey,
			@ApiParam(value="Если значение mode = new, в случае, если заявка с таким searchKey уже есть в систем, выдается ошибка, в режиме reload, заявка загружается без проверки на дубликаты. По умолчанию mode=new - проверка на дубликаты выполняется", required = false, allowableValues="new, reload")
			@RequestParam(name = "mode", required = false) String mode			
			) {

		log.debug("----------------------------------------------------------------------------\n");		
		String dbg="Получен запрос POST:/example/export/:Params:\n"+
		"searchkey:"+searchkey+"\n"+
		"mode:"+mode+"\n"+
		"xmlobj:"+xmlobj+"\n";
		
		log.debug(dbg);
				
		ResponseEntity<SimpleResponse> entity = null;		
		//Empty obj
		if (StringUtils.isEmpty(xmlobj)) {
			entity = new ResponseEntity<SimpleResponse>(new SimpleResponse("Body не может быть пустым"),
					HttpStatus.BAD_REQUEST);
			dbg="Ошибка! saveCustomEntity:Body не может быть пустым \n";
			log.debug(dbg);
			return entity;
		}
		
		try {
			SimpleResponse result = myService.saveCustomEntity(xmlobj, searchkey, mode);

			entity = new ResponseEntity<SimpleResponse>(result, HttpStatus.OK);
			log.debug("saveCustomEntity:->OK");
			return entity;
		} catch (Exception e) {
			entity = new ResponseEntity<SimpleResponse>(new SimpleResponse(e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
			log.error("Ошибка! saveCustomEntity Exception: "+e.getMessage());
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * Запись информации о квитовке
	 * 
	 * @param xmlobj
	 * @return
	 */
	@ApiOperation(value = "Проставить отметку о квитовке платежа комиссии")	
	@RequestMapping(value = "/example/payments/{doc_id}/match", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<SimpleResponse> savePaymentUin(
			@ApiParam(value="ID документа в систем", required = true, format = "Text")
			@PathVariable(name = "doc_id", required = true) String docId,
			@ApiParam(value="ID заявки (УИН)", required = true)
			@RequestParam(name = "uin", required = true) String uin) {
		
		log.debug("----------------------------------------------------------------------------\n");		
		String dbg="Получен запрос POST:/example/payments/{"+docId+"}/match"+"\n"+":Params:\n"+
				"uin:"+uin+"\n";
		
		log.debug(dbg);
		
		ResponseEntity<SimpleResponse> entity = null;		
		
		try {
			SimpleResponse result = myService.savePaymentUin(docId, uin);
			
			entity = new ResponseEntity<SimpleResponse>(result, HttpStatus.OK);
			log.debug("savePaymentUin:->OK");
			return entity;
		} catch (Exception e) {
			entity = new ResponseEntity<SimpleResponse>(new SimpleResponse(e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
			log.error("Ошибка! savePaymentUin Exception: "+e.getMessage());
			e.printStackTrace();
		}
		return entity;
	}
		
}
