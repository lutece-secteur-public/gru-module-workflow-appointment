DROP TABLE IF EXISTS workflow_task_notify_appointment_cf;
DROP TABLE IF EXISTS workflow_task_change_appointment_status_cf;
DROP TABLE IF EXISTS workflow_task_manual_app_notify;
DROP TABLE IF EXISTS workflow_task_notify_admin_appointment_cf;
DROP TABLE IF EXISTS workflow_task_update_appointment_cancel_cf;
DROP TABLE IF EXISTS workflow_task_update_admin_appointment;
DROP TABLE IF EXISTS workflow_task_notify_appointment_crm;


CREATE TABLE workflow_task_notify_appointment_cf(
  id_task INT NOT NULL,
  sender_name VARCHAR(255) DEFAULT NULL,
  sender_email VARCHAR(255) DEFAULT NULL,
  subject VARCHAR(255) DEFAULT NULL,
  message long VARCHAR DEFAULT NULL,
  recipients_cc VARCHAR(255) DEFAULT '' NOT NULL,
  recipients_bcc VARCHAR(255) DEFAULT '' NOT NULL,
  id_action_cancel INT DEFAULT NULL,
  ical_notification SMALLINT DEFAULT 0,
  create_notif SMALLINT DEFAULT 0,
  location VARCHAR(255) DEFAULT '' NOT NULL,
  is_sms SMALLINT DEFAULT 0,
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

CREATE INDEX idx_wf_task_manual_app_notify ON workflow_task_manual_app_notify (id_appointment);
ALTER TABLE workflow_task_manual_app_notify ADD CONSTRAINT fk_wf_appoint_man_ap_notif_hist FOREIGN KEY (id_appointment)
      REFERENCES appointment_appointment (id_appointment) ON DELETE RESTRICT ON UPDATE RESTRICT ;

CREATE TABLE workflow_task_notify_admin_appointment_cf(
  id_task INT NOT NULL,
  id_admin_user INT DEFAULT NULL,
  sender_name VARCHAR(255) DEFAULT NULL,
  sender_email VARCHAR(255) DEFAULT NULL,
  subject VARCHAR(255) DEFAULT NULL,
  message long VARCHAR DEFAULT NULL,
  recipients_cc VARCHAR(255) DEFAULT '' NOT NULL,
  recipients_bcc VARCHAR(255) DEFAULT '' NOT NULL,
  id_action_cancel INT DEFAULT NULL,
  id_action_validate INT DEFAULT NULL,
  ical_notification SMALLINT DEFAULT 0,
  create_notif SMALLINT DEFAULT 0,
  location VARCHAR(255) DEFAULT '' NOT NULL,
  PRIMARY KEY  (id_task)
);

CREATE TABLE workflow_task_update_appointment_cancel_cf(
  id_task INT NOT NULL,
  id_action_cancel INT DEFAULT NULL,
  PRIMARY KEY  (id_task)
);

CREATE TABLE workflow_task_update_admin_appointment(
  id_update INT NOT NULL,
  id_history INT DEFAULT NULL,
  id_appointment INT DEFAULT NULL,
  id_admin_user INT DEFAULT NULL,
  PRIMARY KEY  (id_update)
);
CREATE TABLE workflow_task_notify_appointment_crm(
  
  id_task INT NOT NULL,
  id_demand_type VARCHAR(255) DEFAULT NULL,
  data VARCHAR(255) DEFAULT NULL,
  status_text VARCHAR(255) DEFAULT NULL,
  id_status_crm VARCHAR(255) DEFAULT NULL,
  object  VARCHAR(255) DEFAULT NULL,
  message  VARCHAR(255) DEFAULT NULL,
  sender  VARCHAR(255) DEFAULT NULL,
  
  PRIMARY KEY  (id_task)

);

CREATE INDEX idx_wf_task_update_admin_app ON workflow_task_update_admin_appointment (id_appointment);
