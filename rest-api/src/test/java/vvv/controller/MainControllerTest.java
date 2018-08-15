package vvv.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Value("${test_acc_num}")
	private String testAccNum;

	@Value("${test_begin_date}")
	private String testBeginDate;
	
	/**
	 * RequestMapping(value = "/test"
	 * Проверяется что в тексте ответа есть Ok
	 */
	@Test
	public void testSimpleTest() {
		try {
			mockMvc.perform(get("/test")).andExpect(status().isOk())
			.andExpect(content().string(containsString("Ok")));
		} catch (Exception e) {

			e.printStackTrace();
		}
	}	
	
	/**
	 * RequestMapping(value = "/testDATABASE"
	 * отличается от /test тем, что проверяет коннект в DATABASE
	 * Проверяется что в тексте ответа есть Ok
	 */
	@Test
	public void testConnectionTest() {
		try {
			mockMvc.perform(get("/testDATABASE").with(httpBasic("itfinance","test")))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Ok")));
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}	

	/**
	 * RequestMapping(value = "/example/payments"
	 * Параметры запроса задаются в файле src/test/resource/application.properties
	 * в секции 
	 * # test params
	 * 
	 * test_acc_num=1234567890
	 * test_begin_date=2018-04-12
	 * 
	 * Проверяется что в тексте ответа есть текст
	 * Погашение комиссии по гарантии
	 * 
	 * Когда тестовая база обновится, тест сломается, и надо будет 
	 * в src/test/resource/application.properties 
	 * заново задать параметры
	 * номер счета и дату начала
	 */
	@Test
	public void checkExamplePay() {
		String testURL = "/example/payments?acc_num=" +testAccNum+"&begin_date="+testBeginDate;  
		try {
			mockMvc.perform(get(testURL).with(httpBasic("itfinance","test")))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Погашение комиссии по гарантии")));
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}	
}
