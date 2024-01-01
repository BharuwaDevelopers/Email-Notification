package com.bspl.bean;

import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

public class MailBean {
    RestAdapterCommonDao restadapterdao = new RestAdapterCommonDao();

    @SuppressWarnings("oracle.jdeveloper.java.semantic-warning")
    public Map<String, String> InfoFunction(String UnitCode) {
        PreparedStatement ps = null;
        ResultSet mailInfo = null;
        Connection conn = null;
        HashMap<String, String> m = new HashMap<String, String>();
        try {
            conn = restadapterdao.getConnection(UnitCode);
            //  ps =conn.prepareStatement("select HOST,PORT,auth,starttls,username,password,subject,body,signature  from PAYSLIP_MAIL_INFO where key_no=1");
            ps =
                conn.prepareStatement("SELECT host_name, port, auth, starttls,email_username, email_password, to_mail, cc_mail, bcc_mail, subject, body, signature, t_code, mail_type, file_path, file_name, key_no, maillineid, active_flag FROM public.sendmail_config where active_flag='Y'");
            mailInfo = ps.executeQuery();
            while (mailInfo.next()) {
                m.put("Host", (String) mailInfo.getString("host_name"));
                m.put("Port", (String) mailInfo.getString("port"));
                m.put("Auth", (String) mailInfo.getString("auth"));
                m.put("Starttls", (String) mailInfo.getString("starttls"));
                m.put("Username", (String) mailInfo.getString("email_username"));
                m.put("Password", (String) mailInfo.getString("email_password"));
                m.put("toUser", (String) mailInfo.getString("to_mail"));
                m.put("cc", (String) mailInfo.getString("cc_mail"));
                m.put("bcc", (String) mailInfo.getString("bcc_mail"));
                m.put("Subject", (String) mailInfo.getString("subject"));
                m.put("Body", (String) mailInfo.getString("body"));
                m.put("Signature", (String) mailInfo.getString("signature"));
            }
        } catch (Exception e) {
            if (conn != null) {
                restadapterdao.closeDataSourceConnection(conn);
            }
        } finally {
            if (conn != null) {
                restadapterdao.closeDataSourceConnection(conn);
            }
        }

        return m;
    }

    @SuppressWarnings({ "oracle.jdeveloper.java.semantic-warning", "deprecation" })
    public String sendMailMethod(String unitCode, String tcode, String docNo) {
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        Connection conn = null;
        Statement stmt = null;
        Statement stmt1 = null;
        Statement stmt2 = null;
        String bodyMsg = null;
        String subject = null;
        String toUser = null;
        String ccUser = null;
        String bcc = null;
        String mailhost = null;
        String auth = null;
        String Starttls = null;
        String mailUsername = null;
        String emailPassword = null;
        String isAnyAtchmapt = null;
        String report_param_val = null;
        String fileNameNPath = null;
        String filePath = null;
        String mailFilename = null;
        String result = "No Record Found";
        String sent_status = "N";
        String pdf_generated = null;
        String report_format = null;
        String report_path = null;
        String report_name = null;
        Map parameters = new HashMap();
        try {
            conn = restadapterdao.getConnection(unitCode);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            stmt1 = conn.createStatement();
            stmt2 = conn.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            String emailQuery =
                "SELECT host_name, port, auth, starttls,email_username, email_password, to_mail, cc_mail, bcc_mail, subject, body, signature, t_code, mail_type, file_path, file_name, key_no, maillineid, active_flag FROM public.sendmail_config where active_flag='Y' and t_code='" +
                tcode.trim().toUpperCase() + "'";
            System.out.println("emailQuery---" + emailQuery);
            ps = conn.prepareStatement(emailQuery);
            rs = ps.executeQuery();
            if (rs.next()) {
                mailhost = (String) rs.getString("host_name");
                auth = (String) rs.getString("auth");
                Starttls = (String) rs.getString("starttls");
                mailUsername = (String) rs.getString("email_username");
                emailPassword = (String) rs.getString("email_password");
            } else {
                return "Email Authentication not maintain";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String query = null;
            if (docNo.equalsIgnoreCase("API")) {
                query = "select * from email_notification where mail_sent_status<>'Y' or mail_sent_status is null";
            } else {
                query =
                    "select * from email_notification where mail_sent_status<>'Y' or mail_sent_status is null and ref_document_id='" +
                    docNo.trim() + "'";
            }

            ps1 = conn.prepareStatement(query);
            rs1 = ps1.executeQuery();
            while (rs1.next()) {
                String ref_document_number = (String) rs1.getString("ref_document_number");
                String ref_document_id = (String) rs1.getString("ref_document_id");
                bodyMsg = (String) rs1.getString("body");
                ccUser = (String) rs1.getString("cc_mail");
                bcc = (String) rs1.getString("bcc_mail");
                subject = (String) rs1.getString("subject");
                isAnyAtchmapt = (String) rs1.getString("attachment_required");
                report_param_val = (String) rs1.getString("report_param_val");
                pdf_generated = (String) rs1.getString("pdf_generated");
                report_format = (String) rs1.getString("report_format");
                report_path = (String) rs1.getString("report_path");
                // report_path = "/home/lenovo/jdeveloper/";
                report_name = (String) rs1.getString("report_name");
                if (isAnyAtchmapt.equalsIgnoreCase("Y") && pdf_generated == null) {
                    if (report_param_val != null || report_param_val != "") {
                        String[] recipientList = report_param_val.split(",");
                        int counter = 0;
                        for (String recipient : recipientList) {
                            parameters.put(before(recipient, "="), after(recipient, "="));
                            counter++;
                        }
                    }
                    try {
                        File repFile = new File(report_path + report_name + ".jasper");
                        JasperReport report = (JasperReport) JRLoader.loadObject(repFile);
                        JasperPrint jasperPrint = null;
                        jasperPrint = JasperFillManager.fillReport(report, parameters, conn);
                        JRExporter exporter = null;
                        if (true) {
                            if (report_format.equalsIgnoreCase("PDF")) {
                                // fileName = "Doc_" + report_name + ".pdf";
                                exporter = new JRPdfExporter();
                                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                String exportPath = "/root/Documents/reportspdf/" + ref_document_number + ".pdf";
                                // String exportPath = "/home/lenovo/jdeveloper/pdffile/" + ref_document_number + ".pdf";
                                JasperExportManager.exportReportToPdfFile(jasperPrint, exportPath);
                            } else {
                                // fileName = "Doc_" + report_name + ".xls";
                                exporter = new JRXlsExporter();
                                exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                                                      Boolean.TRUE);
                                exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                                // String exportPath = "/home/lenovo/jdeveloper/pdffile/test.xls";
                                String exportPath = "/root/Documents/reportspdf/" + ref_document_number + ".pdf";
                                JasperExportManager.exportReportToPdfFile(jasperPrint, exportPath);
                            }
                        }
                        String updatepdf_generatedQuery =
                            "update email_notification set pdf_generated='Y' where ref_document_id='" +
                            ref_document_id + "'";
                        stmt2.addBatch(updatepdf_generatedQuery);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    parameters.clear();
                }

                // filePath = (String) rs1.getString("report_path");
                System.out.println("po---" + ref_document_number);
                // subject = subject;
                fileNameNPath = "/root/Documents/reportspdf/" + ref_document_number + ".pdf";

                // fileNameNPath = "/home/lenovo/jdeveloper/pdffile/"+ref_document_number+".pdf";
                mailFilename = ref_document_number + ".pdf";
                // subject = "New PO No"()+po+" & {Vendor Name}";
                result =
                    emailSend(mailhost, auth, Starttls, mailUsername, emailPassword, ccUser, bcc, subject,
                              isAnyAtchmapt, fileNameNPath, mailFilename, toUser, ref_document_number, bodyMsg);
                if (result.equalsIgnoreCase("success")) {
                    sent_status = "Y";
                    String updatePo_headsQuery =
                        "update po_head set mail_sent='Y' where po_id='" + ref_document_id + "'";
                    stmt1.addBatch(updatePo_headsQuery);
                }
                String updateEmailInfoQuery =
                    "update email_notification set mail_sent_status='" + sent_status + "', mail_renarks='" + result +
                    "' where ref_document_number='" + ref_document_number + "'";
                stmt.addBatch(updateEmailInfoQuery);
            }
            int[] updateCounts = stmt.executeBatch();
            int[] updateCounts1 = stmt1.executeBatch();
            int[] updateCounts2 = stmt2.executeBatch();
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                restadapterdao.closeDataSourceConnection(conn);
            }
            result = e.toString();
            return result;
        } finally {
            if (conn != null) {
                try {
                    stmt.close();
                    stmt1.close();
                    stmt2.close();
                    restadapterdao.closeDataSourceConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
        return result;
    }

    public String emailSend(String mailhost, String auth, String Starttls, String mailUsername, String emailPassword,
                            String ccUser, String bcc, String subject, String isAnyAtchmapt, String fileNameNPath,
                            String mailFilename, String toUser, String po, String bodyMsg) {
        toUser = "sawan.kumar@bharuwasolutions.com;rajat.khanduri@bharuwasolutions.com";
        // ccUser = "sawan.kumar@bharuwasolutions.com,rajat.khanduri@bharuwasolutions.com";
        // bcc = "sawan.kumar@bharuwasolutions.com,rajat.khanduri@bharuwasolutions.com";
        Properties emailProperties = new Properties();
        emailProperties.put("mail.smtp.host", mailhost);
        emailProperties.put("mail.smtp.auth", auth);
        emailProperties.put("mail.smtp.starttls.enable", Starttls);
        //Login Credentials
        final String user = mailUsername; //change accordingly
        final String password = emailPassword; //change accordingly
        //Authenticating...
        Session session = Session.getInstance(emailProperties, new javax.mail.Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        //1) create MimeBodyPart object and set your message content
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(user));

            String[] recipientList = toUser.split(";");
            InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
            int counter = 0;
            for (String recipient : recipientList) {
                recipientAddress[counter] = new InternetAddress(recipient.trim());
                counter++;
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddress);
            //  message.addRecipient(Message.RecipientType.TO, (Address) new InternetAddress(toUser));
           // message.setRecipients(Message.RecipientType.TO, (Address[]) InternetAddress.parse(toUser));
            if (!ccUser.equalsIgnoreCase("")) {
                if (!ccUser.isEmpty()) {
                    String[] recipientListCC = ccUser.split(";");
                    InternetAddress[] recipientAddressCC = new InternetAddress[recipientList.length];
                    int counterCC = 0;
                    for (String recipient : recipientListCC) {
                        recipientAddressCC[counterCC] = new InternetAddress(recipient.trim());
                        counterCC++;
                    }
                    message.setRecipients(Message.RecipientType.CC, recipientAddressCC);
                    //message.setRecipients(Message.RecipientType.CC, (Address[]) InternetAddress.parse(ccUser));
                }
            }
            if (!bcc.equalsIgnoreCase("")) {
                if (!bcc.isEmpty()) {
                    String[] recipientListBCC = ccUser.split(";");
                    InternetAddress[] recipientAddressBCC = new InternetAddress[recipientList.length];
                    int counterBCC = 0;
                    for (String recipient : recipientListBCC) {
                        recipientAddressBCC[counterBCC] = new InternetAddress(recipient.trim());
                        counterBCC++;
                    }
                    message.setRecipients(Message.RecipientType.BCC, recipientAddressBCC);
                    //message.setRecipients(Message.RecipientType.BCC, (Address[]) InternetAddress.parse(bcc));
                }
            }
            message.setSubject(subject);
            BodyPart messageBody = new MimeBodyPart();
            // bodyMsg ="<html><body><H5>||OM||</H5></br><p>Dear "+created_by+",</br>  </p><p>Please find the attached here with our Purchase order/Service/Contact order No. (" +po+ ") dated ("+date+")  along with Terms & Conditions. You are requested to go through the same and send us the order confirmation.</p><br/> <br/> <p style=\"color:red;\">** Note it is auto generated mail, do not reply on this email. Please contact buyer, if need any assistance/query**</p></br></br><p>Thanks & Regards</br><b>"+signature+"</b></p></body></html>";
            messageBody.setText(bodyMsg);
            messageBody.setContent(bodyMsg, "text/html");
            // If there is any attachment to send
            if ("Y".equalsIgnoreCase(isAnyAtchmapt)) {
                //2) create new MimeBodyPart object and set DataHandler object to this object
                MimeBodyPart messageBodyPart2 = new MimeBodyPart();
                System.out.println("Exact path--->" + fileNameNPath);
                javax.activation.DataSource source = new FileDataSource(fileNameNPath);
                messageBodyPart2.setDataHandler(new DataHandler(source));
                messageBodyPart2.setFileName(mailFilename);
                //5) create Multipart object and add MimeBodyPart objects to this object
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBody);
                multipart.addBodyPart(messageBodyPart2);
                //6) set the multiplart object to the message object
                message.setContent(multipart);
            }
            //If there is plain eMail- No Attachment
            else {
                message.setContent(bodyMsg, "text/html"); //for a html email
            }
        } catch (MessagingException e) {
            return e.toString();
        }
        Transport transport = null;
        try {
            transport = session.getTransport("smtp");
        } catch (NoSuchProviderException e) {
            System.out.println("No such Provider Exception");
            return e.toString();
        }
        try {
            transport.connect(mailhost, user, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Email sent successfully.");
            return "success";
        } catch (MessagingException e) {
            System.out.println("somthing went wrong-->Messaging Exception" + e);
            return e.toString();
        }
    }


    public String emailSendTesting() {

        String mailhost = "smtp.office365.com", auth = "false", Starttls = "true", mailUsername =
            "berp.noreply@bharuwasolutions.com", emailPassword = "Pab89764", ccUser = "", bcc = "", subject =
            "fgf", isAnyAtchmapt = "N", fileNameNPath = "", mailFilename = "", toUser = "", po = "", bodyMsg = "HIIII";
        //toUser = "sawan.kumar@bharuwasolutions.com";
        toUser = "sawan.kumar@bharuwasolutions.com;rajat.khanduri@bharuwasolutions.com;";
        ccUser = "sawan.kumar@bharuwasolutions.com;rajat.khanduri@bharuwasolutions.com;";
        bcc = "sawan.kumar@bharuwasolutions.com;rajat.khanduri@bharuwasolutions.com;";
        Properties emailProperties = new Properties();
        emailProperties.put("mail.smtp.host", mailhost);
        emailProperties.put("mail.smtp.auth", auth);
        emailProperties.put("mail.smtp.starttls.enable", Starttls);
        //Login Credentials
        final String user = mailUsername; //change accordingly
        final String password = emailPassword; //change accordingly
        //Authenticating...
        Session session = Session.getInstance(emailProperties, new javax.mail.Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        //1) create MimeBodyPart object and set your message content
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(user));

            String[] recipientList = toUser.split(";");
            InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
            int counter = 0;
            for (String recipient : recipientList) {
                recipientAddress[counter] = new InternetAddress(recipient.trim());
                counter++;
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddress);
            //  message.addRecipient(Message.RecipientType.TO, (Address) new InternetAddress(toUser));
           // message.setRecipients(Message.RecipientType.TO, (Address[]) InternetAddress.parse(toUser));
            if (!ccUser.equalsIgnoreCase("")) {
                if (!ccUser.isEmpty()) {
                    String[] recipientListCC = ccUser.split(";");
                    InternetAddress[] recipientAddressCC = new InternetAddress[recipientList.length];
                    int counterCC = 0;
                    for (String recipient : recipientListCC) {
                        recipientAddressCC[counterCC] = new InternetAddress(recipient.trim());
                        counterCC++;
                    }
                    message.setRecipients(Message.RecipientType.CC, recipientAddressCC);
                    //message.setRecipients(Message.RecipientType.CC, (Address[]) InternetAddress.parse(ccUser));
                }
            }
            if (!bcc.equalsIgnoreCase("")) {
                if (!bcc.isEmpty()) {
                    String[] recipientListBCC = ccUser.split(";");
                    InternetAddress[] recipientAddressBCC = new InternetAddress[recipientList.length];
                    int counterBCC = 0;
                    for (String recipient : recipientListBCC) {
                        recipientAddressBCC[counterBCC] = new InternetAddress(recipient.trim());
                        counterBCC++;
                    }
                    message.setRecipients(Message.RecipientType.BCC, recipientAddressBCC);
                    //message.setRecipients(Message.RecipientType.BCC, (Address[]) InternetAddress.parse(bcc));
                }
            }
            message.setSubject(subject);
            BodyPart messageBody = new MimeBodyPart();
            // bodyMsg ="<html><body><H5>||OM||</H5></br><p>Dear "+created_by+",</br>  </p><p>Please find the attached here with our Purchase order/Service/Contact order No. (" +po+ ") dated ("+date+")  along with Terms & Conditions. You are requested to go through the same and send us the order confirmation.</p><br/> <br/> <p style=\"color:red;\">** Note it is auto generated mail, do not reply on this email. Please contact buyer, if need any assistance/query**</p></br></br><p>Thanks & Regards</br><b>"+signature+"</b></p></body></html>";
            messageBody.setText(bodyMsg);
            messageBody.setContent(bodyMsg, "text/html");
            // If there is any attachment to send
            if ("Y".equalsIgnoreCase(isAnyAtchmapt)) {
                //2) create new MimeBodyPart object and set DataHandler object to this object
                MimeBodyPart messageBodyPart2 = new MimeBodyPart();
                System.out.println("Exact path--->" + fileNameNPath);
                javax.activation.DataSource source = new FileDataSource(fileNameNPath);
                messageBodyPart2.setDataHandler(new DataHandler(source));
                messageBodyPart2.setFileName(mailFilename);
                //5) create Multipart object and add MimeBodyPart objects to this object
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBody);
                multipart.addBodyPart(messageBodyPart2);
                //6) set the multiplart object to the message object
                message.setContent(multipart);
            }
            //If there is plain eMail- No Attachment
            else {
                message.setContent(bodyMsg, "text/html"); //for a html email
            }
        } catch (MessagingException e) {
            return e.toString();
        }
        Transport transport = null;
        try {
            transport = session.getTransport("smtp");
        } catch (NoSuchProviderException e) {
            System.out.println("No such Provider Exception");
            return e.toString();
        }
        try {
            transport.connect(mailhost, user, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Email sent successfully.");
            return "success";
        } catch (MessagingException e) {
            System.out.println("somthing went wrong-->Messaging Exception" + e);
            return e.toString();
        }
    }


    static String before(String value, String a) {
        // Return substring containing all characters before a string.
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        return value.substring(0, posA);
    }

    static String after(String value, String a) {
        // Returns a substring containing all characters after a string.
        int posA = value.lastIndexOf(a);
        if (posA == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= value.length()) {
            return "";
        }
        return value.substring(adjustedPosA);
    }

}
