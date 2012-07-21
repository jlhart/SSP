package org.jasig.mygps.web;

import javax.mail.SendFailedException;

import org.jasig.mygps.model.transferobject.MessageTO;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.model.SubjectAndBody;
import org.jasig.ssp.service.MessageService;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.SecurityService;
import org.jasig.ssp.service.reference.MessageTemplateService;
import org.jasig.ssp.web.api.AbstractBaseController;
import org.jasig.ssp.web.api.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/1/mygps/message")
public class MyGpsMessageController extends AbstractBaseController {

	@Autowired
	private transient SecurityService securityService;

	@Autowired
	private transient MessageService messageService;

	@Autowired
	private transient MessageTemplateService messageTemplateService;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MyGpsMessageController.class);

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	Boolean contactCoach(@RequestBody final MessageTO messageTO)
			throws ObjectNotFoundException,
			SendFailedException, ValidationException {

		final Person student = securityService.currentUser().getPerson();

		if ((student == null) || (student.getCoach() == null)) {
			return false;
		}

		final Person coach = student.getCoach();

		final SubjectAndBody subjAndBody = messageTemplateService
				.createContactCoachMessage(
						messageTO.getMessage(), messageTO.getSubject(), student);

		messageService.createMessage(coach, null, subjAndBody);

		return true;
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	public MyGpsMessageController() {
		super();
	}

	public MyGpsMessageController(final MessageService messageService,
			final MessageTemplateService messageTemplateService,
			final SecurityService securityService) {
		super();
		this.messageService = messageService;
		this.messageTemplateService = messageTemplateService;
		this.securityService = securityService;
	}
}
