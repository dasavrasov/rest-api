package main.service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Service;

import main.model.PaymentResponse;
import main.model.PaymentResponseWrapper;
import main.model.SimpleResponse;

@Service
public class DatabaseService {
	private static final String EMPTY_PAY_REPONSE = "Платежей не надено!";

	Logger log = LoggerFactory.getLogger(DatabaseService.class);

	@Autowired
	DataSource dataSource;
	
	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	/**
	 * Тестовый запрос по урлу /test. Лезет в DATABASE и дергает процедуру
	 * TEST_CONNECTION которая должна вернуть OK
	 * 
	 * @return OK
	 */
	public SimpleResponse testConnection() {
		String sql = "select IBS.STORED_PROC_PACK.TEST_CONNECTION from DUAL";
		try {
			String string = (String) jdbcTemplate.getJdbcTemplate().queryForObject(sql, String.class);
			return new SimpleResponse(string);

		} catch (Exception e) {
			log.error("testConnection:"+e.getMessage()); // write log
			throw new RuntimeException("Connection Failed"); // throw exception
		}
	}

	/**
	 * Проверка уплаты
	 * 
	 * @param accNum
	 * @param requestNum
	 * @param start
	 * @param end
	 * @return
	 */
	public List<PaymentResponse> checkCustomEntityPay(String accNum, String requestNum, String payerInn, String startDate, String endDate, String match)
			throws Exception {

		String dbg="";
		
		String sql = "select  * from table(IBS.STORED_PROC_PACK.CHECK_PAY("
				+ ":P_ACC_NUM,"
				+ ":P_REQUEST_NUM,"
				+ ":P_PAYER_INN,"
				+ "TO_DATE("
				+ ":P_START_DATE,"
				+ "'YYYY-MM-DD'),TO_DATE("
				+ ":P_END_DATE,"
				+ "'YYYY-MM-DD'),"
				+ ":P_ONLY_NEW"
				+ "))";

		SqlParameterSource params = new MapSqlParameterSource()
				.addValue("P_ACC_NUM", accNum)
				.addValue("P_REQUEST_NUM", requestNum)				
				.addValue("P_START_DATE", startDate)
				.addValue("P_END_DATE", endDate)
				.addValue("P_PAYER_INN", payerInn)
				.addValue("P_ONLY_NEW", match);

		try {
			dbg="DatabaseService:checkCustomEntityPay:Выполняем запрос к БД с параметрами"+"\n"+
					"accNum:"+accNum+"\n"+
					"requestNum:"+requestNum+"\n"+
					"payerInn:"+payerInn+"\n"+
					"startDate:"+startDate+"\n"+
					"endDate"+endDate+"\n";
		
			log.debug(dbg);
			List<PaymentResponse> responses=jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<PaymentResponse>(PaymentResponse.class));								

			if (responses.isEmpty())
				return responses;
			
			dbg="DatabaseService:checkCustomEntityPay:Запрос к БД ->OK";
			log.debug(dbg);
			return responses;

		} catch (EmptyResultDataAccessException e) {
			dbg="Ошибка! DatabaseService:checkCustomEntityPay:Запрос к БД ->Return Пусто";
			log.debug(dbg);
			return new ArrayList<PaymentResponse>();
		} catch (Exception e) {
			log.error("Ошибка! DatabaseService:checkCustomEntityPay:"+e.getMessage()); // write log
			throw new RuntimeException("DatabaseService:checkCustomEntityPay:"+e.getMessage()); // throw exception
		}

	}

	static public PaymentResponseWrapper emptyPayResponse(String message) {
		List<PaymentResponse> response = new ArrayList<PaymentResponse>();
		return new PaymentResponseWrapper(response,message);
	}


	/**
	 * Запись XML по заключенной сделке в DATABASE
	 * 
	 * @param xmlobj
	 * @param comment 
	 * @return
	 */
	public SimpleResponse saveCustomEntity(String xmlobj, String comment, String mode) throws Exception {
		String dbg="";
		
		MyStoredProcedure myStoredProcedure = new MyStoredProcedure(jdbcTemplate.getJdbcTemplate(), "IBS.STORED_PROC_PACK.SAVEEXAMPLE");

		dbg="DatabaseService:saveCustomEntity:Params:"+"\n"+
		"comment:"+comment+"\n";
		
		//Sql parameter mapping
		SqlParameter fXML = new SqlParameter("P_XML", Types.CLOB);
		SqlParameter fComment = new SqlParameter("P_COMMENT", Types.VARCHAR);
		SqlParameter fMode = new SqlParameter("P_MODE", Types.VARCHAR);
		SqlOutParameter fId = new SqlOutParameter("P_ID", Types.VARCHAR);
		SqlParameter[] paramArray = {fXML, fComment, fMode, fId};

		dbg="DatabaseService:saveCustomEntity:Вызов хранимой процедуры STORED_PROC_PACK.SAVEEXAMPLE"+"\n";
		log.debug(dbg);

		try {
			myStoredProcedure.setParameters(paramArray);
			myStoredProcedure.compile();

			//Call stored procedure
			Map<String,Object> storedProcResult = myStoredProcedure.execute(xmlobj, comment, mode);

			dbg="DatabaseService:saveCustomEntity:Вызов STORED_PROC_PACK.SAVEEXAMPLE выполнен успешно-> OK"+"\n";
			log.debug(dbg);
			
			return new SimpleResponse(storedProcResult.get("P_ID").toString());
		} catch (Exception e) {
			log.error("Ошибка! DatabaseService:saveCustomEntity:"+e.getMessage());
			throw new RuntimeException("Ошибка DatabaseService:saveCustomEntity:"+e.getMessage());
		}
		
	}

	/**
	 * Запись информации о квитовке платежа комисиии
	 * 
	 * @param docId
	 * @param uin 
	 * @return
	 */
	public SimpleResponse savePaymentUin(String docId, String uin) throws Exception {
		String dbg="";
		
		MyStoredProcedure myStoredProcedure = new MyStoredProcedure(jdbcTemplate.getJdbcTemplate(), "IBS.STORED_PROC_PACK.SAVEPAYMENTUIN");
		
		dbg="DatabaseService:savePaymentUin:Params:"+"\n"+
				"docId:"+docId+"\n"+
				"uin:"+uin+"\n";
		
		//Sql parameter mapping
		SqlParameter fdocId = new SqlParameter("P_DOC", Types.VARCHAR);
		SqlParameter fuin = new SqlParameter("P_UIN", Types.VARCHAR);
		SqlOutParameter fmess = new SqlOutParameter("P_MESS", Types.VARCHAR);
		SqlParameter[] paramArray = {fdocId, fuin, fmess};
		
		dbg="DatabaseService:savePaymentUin:Params:Вызов хранимой процедуры STORED_PROC_PACK.SAVEPAYMENTUIN"+"\n";
		log.debug(dbg);
		
		try {
			myStoredProcedure.setParameters(paramArray);
			myStoredProcedure.compile();
			
			//Call stored procedure
			Map<String,Object> storedProcResult = myStoredProcedure.execute(docId, uin);
			
			dbg="DatabaseService:savePaymentUin:Вызов STORED_PROC_PACK.SAVEPAYMENTUIN выполнен успешно-> OK"+"\n";
			log.debug(dbg);
			
			String result;
			try {
				result=storedProcResult.get("P_MESS").toString();
			} catch (NullPointerException e) {
				result=null;
			}
			return new SimpleResponse(result);
		} catch (Exception e) {
			log.error("Ошибка! DatabaseService:savePaymentUin:"+e.getMessage());
			throw new RuntimeException("Ошибка DatabaseService:savePaymentUin:"+e.getMessage());
		}
		
	}
	
	class MyStoredProcedure extends StoredProcedure {

		public MyStoredProcedure(JdbcTemplate jdbcTemplate, String name) {

			super(jdbcTemplate, name);
			setFunction(false);
		}

	}
		
}
