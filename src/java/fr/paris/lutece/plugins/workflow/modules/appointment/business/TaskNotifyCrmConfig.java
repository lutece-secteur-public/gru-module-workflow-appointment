package fr.paris.lutece.plugins.workflow.modules.appointment.business;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

public class TaskNotifyCrmConfig extends TaskConfig
{

    private String _strDemandeType;
    private String _strData;
    private String _strIdStatusCRM;
    private String _strStatusText;
    private String _strObject;
    private String _strMessage;
    private String _strSender;

    public String getDemandeType( )
    {
        return _strDemandeType;
    }

    public void setDemandeType( String strDemandeType )
    {
        _strDemandeType = strDemandeType;
    }

    public String getData( )
    {
        return _strData;
    }

    public void setData( String strData )
    {
        _strData = strData;
    }

    public String getIdStatusCRM( )
    {
        return _strIdStatusCRM;
    }

    public void setIdStatusCRM( String strIdStatusCRM )
    {
        _strIdStatusCRM = strIdStatusCRM;
    }

    public String getStatusText( )
    {
        return _strStatusText;
    }

    public void setStatusText( String strStatusText )
    {
        _strStatusText = strStatusText;
    }

    public String getObject( )
    {
        return _strObject;
    }

    public void setObject( String strObject )
    {
        _strObject = strObject;
    }

    public String getMessage( )
    {
        return _strMessage;
    }

    public void setMessage( String strMessage )
    {
        _strMessage = strMessage;
    }

    public String getSender( )
    {
        return _strSender;
    }

    public void setSender( String strSender )
    {
        _strSender = strSender;
    }

}
