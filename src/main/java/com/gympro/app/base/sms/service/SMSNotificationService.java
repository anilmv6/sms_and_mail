package com.gympro.app.base.sms.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gympro.app.base.db.EntityFactory;
import com.gympro.app.base.sms.config.SMSConfig;
import com.gympro.app.base.sms.util.SMSUtils;
import com.gympro.app.base.type.request.RequestContext;
import com.gympro.app.organization.domain.SMSNotification;
import com.gympro.app.organization.dto.SMSNotificationDTO;
import com.gympro.app.organization.dto.SendSMSDTO;
import com.gympro.app.organization.repository.SMSNotificationRepository;

@Service
public class SMSNotificationService {

	@Autowired
	private SMSNotificationRepository smsNotificationRepository;
	@Autowired
	private EntityFactory entityFactory;
	
	@Autowired
	private SMSConfig smsConfig;
	
	String string = null;

	public List<SMSNotification> findAll() throws Exception {
		return smsNotificationRepository.findAll();
	}

	public SMSNotification findById(SMSNotificationDTO smsNotificationDTO) throws Exception {
		SMSNotification smsNotification = null;

		Optional<SMSNotification> sMSNotificationObject = smsNotificationRepository
				.findById(smsNotificationDTO.getId());

		if (sMSNotificationObject.isPresent()) {
			smsNotification = sMSNotificationObject.get();
		}
		return smsNotification;
	}

	public void removeById(Long id) throws Exception {
		smsNotificationRepository.deleteById(id);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public SMSNotification save(SMSNotificationDTO smsNotificationDTO) throws Exception {
		RequestContext context = RequestContext.getRequestContext();
		SMSNotification smsNotificationObject = entityFactory.newEntity(SMSNotification.class);
		BeanUtils.copyProperties(smsNotificationDTO, smsNotificationObject);
		smsNotificationObject.setEnabled(smsNotificationDTO.getIsEnabled());
		smsNotificationObject.setRequestId(context.getRequestId());
		SMSNotification smsNotification = null;
		try {
			smsNotification = smsNotificationRepository.save(smsNotificationObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return smsNotification;
	}

	public String sendAndSaveSMS(SendSMSDTO sendSms) throws Exception {
		SMSNotification smsNotification =  new SMSNotification();
		StringBuilder postdata = new StringBuilder();
		List<String> stringOne = new ArrayList<String>();
		for (Map.Entry<String, String> entry : sendSms.getNamesAndMobileNumbers().entrySet()) {
		    System.out.println(entry.getKey() + " = " + entry.getValue());
		String stringBuilderMsg=SMSUtils.getCustomMessageBuilder(sendSms,entry.getKey()).toString();
		postdata.append("&mobiles=" + entry.getValue());
		postdata.append("&message=" + URLEncoder.encode(stringBuilderMsg));

		 String string = smsConfig.sendSMS(sendSms,postdata);
		 stringOne.add(string);
		System.out.println(string);

		}
		return stringOne.toString();
	}

	public String sendLicenceExpiryAlertSMS(SendSMSDTO sendSms) throws Exception {
		StringBuilder postdata = new StringBuilder();
		List<String> stringOne = new ArrayList<String>();
		for (Map.Entry<String, String> entry : sendSms.getNamesAndMobileNumbers().entrySet()) {
		    System.out.println(entry.getKey() + " = " + entry.getValue());
		String stringBuilderMsg=SMSUtils.getCMBuilderForLicenseEandR(sendSms,entry.getKey()).toString();
		postdata.append("&mobiles=" + entry.getValue());
		postdata.append("&message=" + URLEncoder.encode(stringBuilderMsg));

		 String string = smsConfig.sendSMS(sendSms,postdata);
		 stringOne.add(string);
		System.out.println(string);

		}
		return stringOne.toString();
	}
	
}
