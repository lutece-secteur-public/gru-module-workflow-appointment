DROP TABLE IF EXISTS workflow_task_notify_appointment_cf;
DROP TABLE IF EXISTS workflow_task_change_appointment_status_cf;
DROP TABLE IF EXISTS workflow_task_manual_app_notify;
DROP TABLE IF EXISTS workflow_task_notify_admin_appointment_cf;
DROP TABLE IF EXISTS workflow_task_update_appointment_cancel_cf;


CREATE TABLE workflow_task_notify_appointment_cf(
  id_task INT NOT NULL,
  sender_name VARCHAR(255) DEFAULT NULL, 
  subject VARCHAR(255) DEFAULT NULL, 
  message long VARCHAR DEFAULT NULL,
  recipients_cc VARCHAR(255) DEFAULT '' NOT NULL,
  recipients_bcc VARCHAR(255) DEFAULT '' NOT NULL,
  id_action_cancel INT DEFAULT NULL,
  PRIMARY KEY  (id_task)
);
  
CREATE TABLE workflow_task_change_appointment_status_cf(
  id_task INT NOT NULL,
  appointment_status INT DEFAULT NULL,
  PRIMARY KEY  (id_task)
);

CREATE TABLE workflow_task_manual_app_notify(
  id_notif INT NOT NULL,
  id_history INT DEFAULT NULL,
  id_appointment INT DEFAULT NULL,
  email VARCHAR(255) DEFAULT NULL, 
  subject VARCHAR(255) DEFAULT NULL, 
  message long VARCHAR DEFAULT NULL,
  PRIMARY KEY  (id_notif)
);

CREATE TABLE workflow_task_notify_admin_appointment_cf(
  id_task INT NOT NULL,
  id_admin_user INT DEFAULT NULL,
  sender_name VARCHAR(255) DEFAULT NULL, 
  subject VARCHAR(255) DEFAULT NULL, 
  message long VARCHAR DEFAULT NULL,
  recipients_cc VARCHAR(255) DEFAULT '' NOT NULL,
  recipients_bcc VARCHAR(255) DEFAULT '' NOT NULL,
  id_action_cancel INT DEFAULT NULL,
  id_action_validate INT DEFAULT NULL,
  PRIMARY KEY  (id_task)
);

CREATE TABLE workflow_task_update_appointment_cancel_cf(
  id_task INT NOT NULL,
  id_action_cancel INT DEFAULT NULL,
  PRIMARY KEY  (id_task)
);
