ALTER TABLE workflow_task_notify_appointment_cf
ADD id_appointment_form_selected INT DEFAULT NULL,
AFTER id_task;

ALTER TABLE workflow_task_notify_admin_appointment_cf
ADD id_appointment_form_selected INT DEFAULT NULL,
AFTER id_task;