/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.appointment.web;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.appointment.business.ManualAppointmentNotificationHistory;
import fr.paris.lutece.plugins.workflow.modules.appointment.business.ManualAppointmentNotificationHistoryHome;
import fr.paris.lutece.plugins.workflow.web.task.NoConfigTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 *
 * NotifyAppointmentTaskComponent
 *
 */
public class ManualAppointmentNotificationTaskComponent extends NoConfigTaskComponent {
	// TEMPLATES
	private static final String TEMPLATE_MANUAL_APPOINTMENT_NOTIFICATION = "admin/plugins/workflow/modules/appointment/manual_appointment_notification_config.html";
	private static final String TEMPLATE_MANUAL_APPOINTMENT_NOTIFICATION_HISTORY = "admin/plugins/workflow/modules/appointment/manual_appointment_notification_history.html";

	// MESSAGES
	private static final String MESSAGE_MANDATORY_FIELD = "portal.util.message.mandatoryFields";

	// MARKS
	private static final String MARK_HISTORY_LIST = "listHistory";
	private static final String MARK_WEBAPP_URL = "webapp_url";
	private static final String MARK_LOCALE = "locale";
	private static final String MARK_DEFAULT_SENDER_NAME = "default_sender_name";

	// PARAMETERS
	private static final String PARAMETER_MESSAGE = "message";
	private static final String PARAMETER_SUBJECT = "subject";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDisplayTaskForm(int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
			ITask task) {
		Map<String, Object> model = new HashMap<String, Object>();

		model.put(MARK_WEBAPP_URL, AppPathService.getBaseUrl(request));
		model.put(MARK_LOCALE, locale);
		model.put(MARK_DEFAULT_SENDER_NAME, MailService.getNoReplyEmail());

		HtmlTemplate template = AppTemplateService.getTemplate(TEMPLATE_MANUAL_APPOINTMENT_NOTIFICATION, locale, model);

		return template.getHtml();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String doValidateTask(int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
			ITask task) {
		String strMessage = request.getParameter(PARAMETER_MESSAGE);
		String strSubject = request.getParameter(PARAMETER_SUBJECT);

		if (StringUtils.isBlank(strSubject) || StringUtils.isBlank(strMessage)) {
			return AdminMessageService.getMessageUrl(request, MESSAGE_MANDATORY_FIELD, AdminMessage.TYPE_STOP);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDisplayTaskInformation(int nIdHistory, HttpServletRequest request, Locale locale, ITask task) {
		List<ManualAppointmentNotificationHistory> listHistory = ManualAppointmentNotificationHistoryHome
				.findByIdHistory(nIdHistory);

		if (listHistory.size() > 0) {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put(MARK_HISTORY_LIST, listHistory);

			HtmlTemplate template = AppTemplateService.getTemplate(TEMPLATE_MANUAL_APPOINTMENT_NOTIFICATION_HISTORY,
					locale, model);

			return template.getHtml();
		}

		return StringUtils.EMPTY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTaskInformationXml(int nIdHistory, HttpServletRequest request, Locale locale, ITask task) {
		return null;
	}
}
