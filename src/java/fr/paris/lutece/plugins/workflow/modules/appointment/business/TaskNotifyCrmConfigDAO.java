package fr.paris.lutece.plugins.workflow.modules.appointment.business;

import fr.paris.lutece.plugins.workflow.modules.appointment.service.WorkflowAppointmentPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

public class TaskNotifyCrmConfigDAO implements ITaskConfigDAO<TaskNotifyCrmConfig> {

	private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_task,id_demand_type, data, status_text, id_status_crm, object, message, sender FROM workflow_task_notify_appointment_crm WHERE id_task=?";
	private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_notify_appointment_crm( "
			+ "id_task,id_demand_type,data, status_text, id_status_crm, object, message, sender) VALUES (?,?,?,?,?,?,?,?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_notify_appointment_crm "
			+ " SET id_demand_type = ?, data = ?, status_text = ?, id_status_crm = ?, object = ?, message = ?, sender = ? WHERE id_task = ? ";
	private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_notify_appointment_crm WHERE id_task = ? ";

	@Override
	public void insert(TaskNotifyCrmConfig config) {

		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_INSERT, WorkflowAppointmentPlugin.getPlugin());

		int nIndex = 1;

		daoUtil.setInt(nIndex++, config.getIdTask());
		daoUtil.setString(nIndex++, config.getDemandeType());
		daoUtil.setString(nIndex++, config.getData());
		daoUtil.setString(nIndex++, config.getStatusText());
		daoUtil.setString(nIndex++, config.getIdStatusCRM());
		daoUtil.setString(nIndex++, config.getObject());
		daoUtil.setString(nIndex++, config.getMessage());
		daoUtil.setString(nIndex++, config.getSender());

		daoUtil.executeUpdate();
		daoUtil.free();

	}

	@Override
	public void store(TaskNotifyCrmConfig config) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_UPDATE, WorkflowAppointmentPlugin.getPlugin());

		int nIndex = 1;

		daoUtil.setString(nIndex++, config.getDemandeType());
		daoUtil.setString(nIndex++, config.getData());
		daoUtil.setString(nIndex++, config.getStatusText());
		daoUtil.setString(nIndex++, config.getIdStatusCRM());
		daoUtil.setString(nIndex++, config.getObject());
		daoUtil.setString(nIndex++, config.getMessage());
		daoUtil.setString(nIndex++, config.getSender());

		daoUtil.setInt(nIndex, config.getIdTask());

		daoUtil.executeUpdate();
		daoUtil.free();

	}

	@Override
	public TaskNotifyCrmConfig load(int nIdTask) {

		TaskNotifyCrmConfig config = null;
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_FIND_BY_PRIMARY_KEY, WorkflowAppointmentPlugin.getPlugin());

		daoUtil.setInt(1, nIdTask);

		daoUtil.executeQuery();

		int nIndex = 1;

		if (daoUtil.next()) {
			config = new TaskNotifyCrmConfig();
			config.setIdTask(daoUtil.getInt(nIndex++));
			config.setDemandeType(daoUtil.getString(nIndex++));
			config.setData(daoUtil.getString(nIndex++));
			config.setStatusText(daoUtil.getString(nIndex++));
			config.setIdStatusCRM(daoUtil.getString(nIndex++));
			config.setObject(daoUtil.getString(nIndex++));
			config.setMessage(daoUtil.getString(nIndex++));
			config.setSender(daoUtil.getString(nIndex++));

		}

		daoUtil.free();

		return config;
	}

	@Override
	public void delete(int nIdTask) {

		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, WorkflowAppointmentPlugin.getPlugin());

		daoUtil.setInt(1, nIdTask);
		daoUtil.executeUpdate();
		daoUtil.free();

	}

}
