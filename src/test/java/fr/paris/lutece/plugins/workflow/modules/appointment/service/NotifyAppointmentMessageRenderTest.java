/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.modules.appointment.provider.AppointmentWorkflowConstants;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import freemarker.cache.FileTemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Renders the notifications of the appointment tasks as {@link AbstractTaskNotifyAppointment#sendEmail} does, with HTML
 * auto-escaping (LUT-33153): the mail or SMS template writes the message of the task, then the result is rendered again
 * as a FreeMarker template with the same model. The message and the recap are markup and keep their tags, the user's data
 * given by the other markers is escaped in the mail and written as text in the SMS and the subject. Only the FreeMarker
 * engine is needed.
 */
public class NotifyAppointmentMessageRenderTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String TEMPLATE_MAIL = "admin/plugins/workflow/modules/appointment/task_notify_appointment_mail.html";
    private static final String TEMPLATE_SMS = "admin/plugins/workflow/modules/appointment/task_notify_appointment_sms.html";

    private static final String MESSAGE = "<p>Bonjour ${firstName}</p>${recap!}<a href=\"${url_report}\">Report</a>";
    private static final String SMS = "Bonjour ${firstName}, l'adresse : ${url_report}";
    private static final String SUBJECT = "Rendez-vous de ${firstName}";
    private static final String FIRST_NAME = "<b>Ann</b> & \"Jo\"";
    private static final String FIRST_NAME_ESCAPED = "&lt;b&gt;Ann&lt;/b&gt; &amp; &quot;Jo&quot;";
    private static final String RECAP = "<div><label>Nom</label>a &lt;b&gt;</div>";
    private static final String URL_REPORT = "https://site/jsp/site/Portal.jsp?page=appointment&view=report";

    /**
     * The configuration of the core: HTML output format and auto-escaping.
     *
     * @return the configuration
     * @throws Exception
     *             if the templates directory cannot be read
     */
    private static Configuration configuration( ) throws Exception
    {
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setTemplateLoader( new FileTemplateLoader( new File( TEMPLATES_ROOT ) ) );

        return cfg;
    }

    /**
     * Renders a template of the module, then its result as a template, as sendEmail does.
     *
     * @param strTemplate
     *            the template of the module
     * @param model
     *            the model
     * @return the content
     * @throws Exception
     *             if the rendering fails
     */
    private static String renderContent( String strTemplate, Map<String, Object> model ) throws Exception
    {
        Configuration cfg = configuration( );
        StringWriter writer = new StringWriter( );
        cfg.getTemplate( strTemplate ).process( model, writer );

        return renderString( writer.toString( ), model );
    }

    /**
     * Renders a string as a template, as AppTemplateService.getTemplateFromStringFtl does.
     *
     * @param strSource
     *            the template source
     * @param model
     *            the model
     * @return the result
     * @throws Exception
     *             if the rendering fails
     */
    private static String renderString( String strSource, Map<String, Object> model ) throws Exception
    {
        StringWriter writer = new StringWriter( );
        new Template( "string", new StringReader( strSource ), configuration( ) ).process( model, writer );

        return writer.toString( );
    }

    /**
     * The markers of a notification, as AppointmentNotificationMarkers and fillModel give them.
     *
     * @param strMessage
     *            the message of the task
     * @return the markers
     */
    private static List<InfoMarker> markers( String strMessage )
    {
        List<InfoMarker> listMarkers = new ArrayList<>( );
        listMarkers.add( marker( AppointmentWorkflowConstants.MARK_FIRSTNAME, FIRST_NAME ) );
        listMarkers.add( marker( AppointmentWorkflowConstants.MARK_URL_REPORT, URL_REPORT ) );
        listMarkers.add( marker( AppointmentWorkflowConstants.MARK_MESSAGE, strMessage ) );
        listMarkers.add( marker( AppointmentWorkflowConstants.MARK_RECAP, RECAP ) );

        return listMarkers;
    }

    /**
     * A marker and its value.
     *
     * @param strName
     *            the name
     * @param strValue
     *            the value
     * @return the marker
     */
    private static InfoMarker marker( String strName, String strValue )
    {
        InfoMarker marker = new InfoMarker( strName );
        marker.setValue( strValue );

        return marker;
    }

    /**
     * The mail keeps the tags of the message and of the recap, and escapes the user's data.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testMailKeepsTheMessageAndEscapesTheData( ) throws Exception
    {
        String strContent = renderContent( TEMPLATE_MAIL, AbstractTaskNotifyAppointment.markersToModel( markers( MESSAGE ) ) );

        assertTrue( strContent.contains( "<p>Bonjour " + FIRST_NAME_ESCAPED + "</p>" ), strContent );
        assertTrue( strContent.contains( RECAP ), strContent );
        assertTrue( strContent.contains( "<a href=\"https://site/jsp/site/Portal.jsp?page=appointment&amp;view=report\">" ), strContent );
        assertFalse( strContent.contains( "&lt;p&gt;" ), strContent );
        assertFalse( strContent.contains( "&amp;amp;" ), strContent );
    }

    /**
     * A message left as a String is escaped by the mail template: the reason markersToModel marks it.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testAStringMessageIsEscaped( ) throws Exception
    {
        Map<String, Object> model = AbstractTaskNotifyAppointment.markersToModel( markers( MESSAGE ) );
        model.put( AppointmentWorkflowConstants.MARK_MESSAGE, MESSAGE );
        StringWriter writer = new StringWriter( );
        configuration( ).getTemplate( TEMPLATE_MAIL ).process( model, writer );

        assertTrue( writer.toString( ).startsWith( "&lt;p&gt;" ), writer.toString( ) );
    }

    /**
     * The SMS and the subject are plain text: the user's data is written as typed, without entities.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testSmsAndSubjectAreText( ) throws Exception
    {
        Map<String, Object> model = AbstractTaskNotifyAppointment.markersToModel( markers( SMS ) );

        assertEquals( "Bonjour " + FIRST_NAME + ", l'adresse : " + URL_REPORT,
                AbstractTaskNotifyAppointment.toPlainText( renderContent( TEMPLATE_SMS, model ) ) );
        assertEquals( "Rendez-vous de " + FIRST_NAME, AbstractTaskNotifyAppointment.toPlainText( renderString( SUBJECT, model ) ) );
    }
}
