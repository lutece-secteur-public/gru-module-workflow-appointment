package fr.paris.lutece.plugins.workflow.modules.appointment.business;

import fr.paris.lutece.test.LuteceTestCase;

public class UpdateAdminAppointmentHistoryBusinessTest extends LuteceTestCase
{

    public void testCRUD( )
    {
        UpdateAdminAppointmentHistory his = new UpdateAdminAppointmentHistory( );
        his.setIdAdminUser( 1 );
        his.setIdAppointment( 2 );
        his.setIdHistory( 3 );
        
        UpdateAdminAppointmentHistoryHome.create( his );
        
        UpdateAdminAppointmentHistory loaded = UpdateAdminAppointmentHistoryHome.findByPrimaryKey( his.getIdUpdate( ) );
        assertEquals( his.getIdAdminUser( ), loaded.getIdAdminUser( ) );
        assertEquals( his.getIdAppointment( ), loaded.getIdAppointment( ) );
        assertEquals( his.getIdHistory( ), loaded.getIdHistory( ) );
        
        UpdateAdminAppointmentHistoryHome.delete( his.getIdUpdate( ) );
        
        loaded = UpdateAdminAppointmentHistoryHome.findByPrimaryKey( his.getIdUpdate( ) );
        assertNull( loaded );
    }
}
