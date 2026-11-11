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
package fr.paris.lutece.plugins.workflow.modules.appointment.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.editor.RichTextContentMethod;
import fr.paris.lutece.portal.service.template.HtmlAttributesMethod;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.test.LuteceTestCase;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateMethodModelEx;

/**
 * Renders the templates of workflow-appointment that receive HTML from Java, or whose attributes moved to a params map,
 * with the configuration of the core (LUT-33153): HTML output format, auto-escaping, the back office commons of the
 * exploded webapp and htmlAttributes strict. The entries of the appointment form are markup and keep their tags, the
 * user's data is escaped, the mail kept in the history is sanitized by richText(). The front office layout macros are
 * stubbed.
 */
public class WorkflowAppointmentTemplatesRenderTest extends LuteceTestCase
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String CORE_TEMPLATES_ROOT = "target/lutece/WEB-INF/templates";
    private static final String COMMONS_BO = "commons_bs5_tabler.html";
    private static final String TEMPLATE_TASK_FORM_BO = "admin/plugins/workflow/modules/appointment/task_update_appointment.html";
    private static final String TEMPLATE_TASK_FORM_FO = "skin/plugins/workflow/modules/appointment/task_update_appointment.html";
    private static final String TEMPLATE_HISTORY = "admin/plugins/workflow/modules/appointment/manual_appointment_notification_history.html";
    private static final String TEMPLATE_RECAP = "admin/plugins/workflow/modules/appointment/task_notify_appointment_recap.html";
    private static final String TEMPLATE_MANUAL_CONFIG = "admin/plugins/workflow/modules/appointment/manual_appointment_notification_config.html";
    private static final String TEMPLATE_NOTIFY_CONFIG = "admin/plugins/workflow/modules/appointment/task_notify_appointment_config.html";

    /** Stand-ins for the front office macros: they write their nested content and the value they are given. */
    private static final String MACROS_FO = "workflow_appointment_test_macros_fo.ftl";
    private static final String MACROS_FO_SOURCE = "<#macro cFieldset legend='' rest...><fieldset><legend>${legend}</legend><#nested></fieldset></#macro>"
            + "<#macro cAlert title='' rest...><div class=\"alert\">${title}</div></#macro>"
            + "<#macro cRow rest...><#nested></#macro><#macro cCol rest...><#nested></#macro>"
            + "<#macro cField label='' rest...><label>${label}</label><#nested></#macro>"
            + "<#macro cInput name value='' rest...><input name=\"${name}\" value=\"${value}\"></#macro>";

    private static final String HOSTILE = "Ann <b>x</b> \"q\" <script>alert(1)</script>";
    private static final String HOSTILE_ESCAPED = "Ann &lt;b&gt;x&lt;/b&gt; &quot;q&quot; &lt;script&gt;alert(1)&lt;/script&gt;";
    private static final String ENTRY = "<div class=\"entry\"><label for=\"attribute_1\">Phone</label><input id=\"attribute_1\" value=\"a &lt;b&gt;\"></div>";

    /**
     * Renders a template of the module.
     *
     * @param strTemplate
     *            the template path
     * @param strAutoInclude
     *            the commons or the stubs included first
     * @param model
     *            the model
     * @return the HTML
     * @throws Exception
     *             if the rendering fails
     */
    private static String render( String strTemplate, String strAutoInclude, Map<String, Object> model ) throws Exception
    {
        File coreTemplates = new File( CORE_TEMPLATES_ROOT );
        assertTrue( new File( coreTemplates, COMMONS_BO ).isFile( ), "core templates not found under " + CORE_TEMPLATES_ROOT + ": run lutece:exploded first" );
        StringTemplateLoader stubs = new StringTemplateLoader( );
        stubs.putTemplate( MACROS_FO, MACROS_FO_SOURCE );
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setURLEscapingCharset( "UTF-8" );
        cfg.setTemplateLoader( new MultiTemplateLoader( new TemplateLoader [ ] {
                stubs, new FileTemplateLoader( new File( TEMPLATES_ROOT ) ), new FileTemplateLoader( coreTemplates )
        } ) );
        cfg.addAutoInclude( strAutoInclude );
        cfg.setSharedVariable( "htmlAttributes", new HtmlAttributesMethod( true ) );
        cfg.setSharedVariable( "i18n", (TemplateMethodModelEx) arguments -> String.valueOf( arguments.get( 0 ) ) );
        cfg.setSharedVariable( "dskey", (TemplateMethodModelEx) arguments -> "DS" );
        cfg.setSharedVariable( "richText", new RichTextContentMethod( ) );
        cfg.setSharedVariable( "richTextEditorProfile", (TemplateMethodModelEx) arguments -> Map.of( "editor", "" ) );
        StringWriter writer = new StringWriter( );
        cfg.getTemplate( strTemplate ).process( model, writer );

        return writer.toString( );
    }

    /**
     * The model UpdateAppointmentTaskComponent builds, the entries of the form given as markup.
     *
     * @param entries
     *            the entries
     * @return the model
     */
    private static Map<String, Object> taskFormModel( Object entries )
    {
        Map<String, Object> form = new HashMap<>( );
        form.put( "displayTitleFo", true );
        form.put( "title", HOSTILE );
        form.put( "idForm", 1234 );
        Map<String, Object> appointment = new HashMap<>( );
        appointment.put( "lastName", HOSTILE );
        appointment.put( "firstName", "Jo" );
        appointment.put( "email", "jo@example.org" );
        Map<String, Object> model = new HashMap<>( );
        model.put( "form", form );
        model.put( "formMessages", Map.of( "fieldLastNameTitle", "Nom" ) );
        model.put( "appointment", appointment );
        model.put( "date_of_display", "2026-11-10" );
        model.put( "str_entry", entries );

        return model;
    }

    /**
     * The back office and front office task forms write the entries marked as markup as they are, and escape the title of
     * the form and the user's data; the form id is written without grouping.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testTaskFormKeepsTheEntriesAndEscapesTheData( ) throws Exception
    {
        for ( String [ ] template : new String [ ] [ ] {
                {
                        TEMPLATE_TASK_FORM_BO, COMMONS_BO
                }, {
                        TEMPLATE_TASK_FORM_FO, MACROS_FO
                }
        } )
        {
            String strHtml = render( template [0], template [1], taskFormModel( HtmlMarkup.of( ENTRY ) ) );

            assertTrue( strHtml.contains( ENTRY ), strHtml );
            assertTrue( strHtml.contains( HOSTILE_ESCAPED ), strHtml );
            assertFalse( strHtml.contains( "<script>alert" ), strHtml );
            assertTrue( strHtml.contains( "value=\"1234\"" ), strHtml );
        }
    }

    /**
     * Entries left as a String are escaped: the reason the task component marks them.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testStringEntriesAreEscaped( ) throws Exception
    {
        String strHtml = render( TEMPLATE_TASK_FORM_BO, COMMONS_BO, taskFormModel( ENTRY ) );

        assertTrue( strHtml.contains( "&lt;div class=&quot;entry&quot;&gt;" ), strHtml );
    }

    /**
     * The mail kept in the history is written as sanitized HTML: its tags are kept, a script is removed, the address and
     * the subject are escaped.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testHistoryWritesTheMailAsSanitizedHtml( ) throws Exception
    {
        Map<String, Object> history = new HashMap<>( );
        history.put( "emailTo", "jo@example.org" );
        history.put( "emailSubject", HOSTILE );
        history.put( "emailMessage", "<p>Bonjour <strong>Jo</strong></p><script>alert(1)</script>" );
        String strHtml = render( TEMPLATE_HISTORY, COMMONS_BO, Map.of( "listHistory", List.of( history ) ) );

        assertTrue( strHtml.contains( "<p>Bonjour <strong>Jo</strong></p>" ), strHtml );
        assertTrue( strHtml.contains( HOSTILE_ESCAPED ), strHtml );
        assertFalse( strHtml.contains( "<script>alert" ), strHtml );
    }

    /**
     * The recap of the notifications escapes the answers and the titles of the entries.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testRecapEscapesTheAnswers( ) throws Exception
    {
        Map<String, Object> response = new HashMap<>( );
        response.put( "recapValue", HOSTILE );
        response.put( "entry", Map.of( "idEntry", 1, "title", HOSTILE ) );
        String strHtml = render( TEMPLATE_RECAP, COMMONS_BO, Map.of( "listResponse", List.of( response ) ) );

        assertTrue( strHtml.contains( HOSTILE_ESCAPED ), strHtml );
        assertFalse( strHtml.contains( "<script>alert" ), strHtml );
    }

    /**
     * The iCal block of the notification configurations is hidden through the params map: htmlAttributes, strict, writes
     * hidden on the block when the iCal notification is off, and nothing when it is on.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testICalBlockHiddenThroughParams( ) throws Exception
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "default_sender_name", "noreply@example.org" );
        model.put( "list_markers", List.of( ) );
        model.put( "config", Map.of( "sendICalNotif", false, "createNotif", true, "message", "" ) );
        String strHtml = render( TEMPLATE_MANUAL_CONFIG, COMMONS_BO, model );
        assertTrue( strHtml.matches( "(?s).*<div[^>]*id=\"ical\"[^>]*\\shidden[\\s>].*" ), strHtml );

        model.put( "config", Map.of( "sendICalNotif", true, "createNotif", true, "message", "" ) );
        strHtml = render( TEMPLATE_MANUAL_CONFIG, COMMONS_BO, model );
        assertFalse( strHtml.matches( "(?s).*<div[^>]*id=\"ical\"[^>]*\\shidden[\\s>].*" ), strHtml );

        Map<String, Object> config = new HashMap<>( );
        config.put( "isSms", true );
        config.put( "sendICalNotif", false );
        config.put( "createNotif", true );
        model.put( "config", config );
        model.put( "notify_admin", false );
        model.put( "list_actions", List.of( ) );
        strHtml = render( TEMPLATE_NOTIFY_CONFIG, COMMONS_BO, model );
        assertTrue( strHtml.matches( "(?s).*<div[^>]*id=\"ical-block\"[^>]*\\shidden[\\s>].*" ), strHtml );
    }
}
