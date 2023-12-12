package com.bspl.bean;

import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.HashMap;
import java.util.Map;

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

public class CallJasperReports {
    
    public String callJasperForPdf(String unitCode) {
        RestAdapterCommonDao restadapterdao = new RestAdapterCommonDao();
        Connection conn = null;
        PreparedStatement ps1 = null;
        ResultSet rs1 = null;
        String fileName = null;
        String report_name = "po_print";
        String report_path = "/home/lenovo/jdeveloper/";
        //String report_path = "/root/Documents/reportspdf/";
        String report_format = "PDF";
        String report_param_val = null;
        Map parameters = new HashMap();
        Statement stmt = null;
        try {
            conn = restadapterdao.getConnection(unitCode.trim());
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.toString();
        }
        try {
            String query =
                "select ref_document_number,report_path,report_name,report_param_val,report_format,pdf_generated FROM public.email_notification where pdf_generated is null";
            ps1 = conn.prepareStatement(query);
            rs1 = ps1.executeQuery();
            while (rs1.next()) {
                String po = (String) rs1.getString("ref_document_number");
                String ref_document_id = (String) rs1.getString("ref_document_id");
                report_path = (String) rs1.getString("report_path");
                report_name = (String) rs1.getString("report_name");
                report_param_val = (String) rs1.getString("report_param_val");
                if (report_param_val != null || report_param_val != "") {
                    String[] recipientList = report_param_val.split(",");
                    int counter = 0;
                    for (String recipient : recipientList) {
                        System.out.println("recipient--" + recipient);
                        // System.out.println("before--"+before(recipient, "="));
                        //System.out.println("after--"+after(recipient, "="));
                        parameters.put(before(recipient, "="), after(recipient, "="));
                        counter++;
                    }
                }

                try {
                    // parameters.put("p_no", po);
                    // parameters.put("p_unit", unitCode.trim());
                    File repFile = new File(report_path + report_name + ".jasper");
                    JasperReport report = (JasperReport) JRLoader.loadObject(repFile);
                    JasperPrint jasperPrint = null;
                    jasperPrint = JasperFillManager.fillReport(report, parameters, conn);
                    JRExporter exporter = null;
                    if (true) {
                        if (report_format.equalsIgnoreCase("PDF")) {
                            fileName = "Doc_" + report_name + ".pdf";
                            exporter = new JRPdfExporter();
                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                            //   String exportPath = "/home/lenovo/jdeveloper/pdffile/test.pdf";
                            //String exportPath ="/root/Documents/reportspdf/" + po + ".pdf";
                            String exportPath = "/home/lenovo/jdeveloper/pdffile/" + po + ".pdf";
                            JasperExportManager.exportReportToPdfFile(jasperPrint, exportPath);
                        } else {
                            fileName = "Doc_" + report_name + ".xls";
                            exporter = new JRXlsExporter();
                            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
                                                  Boolean.TRUE);
                            exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                            // String exportPath = "/home/lenovo/jdeveloper/pdffile/test.xls";
                            String exportPath = "/root/Documents/reportspdf/" + po + ".pdf";
                            JasperExportManager.exportReportToPdfFile(jasperPrint, exportPath);
                        }
                    }
                    try {
                        System.out.println("in Try1 connection closed");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return e.toString();
                    }
                } catch (JRException e) {
                    e.printStackTrace();
                    try {
                        System.out.println("in catch finally connection closed");
                        conn.close();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                        return e1.toString();
                    }
                    return e.toString();
                }
                String updatepdf_generatedQuery =
                    "update email_notification set pdf_generated='Y' where ref_document_id='" + ref_document_id + "'";
                stmt.addBatch(updatepdf_generatedQuery);
            }
            int[] updateCounts = stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                restadapterdao.closeDataSourceConnection(conn);
            }
            e.printStackTrace();
            return e.toString();
        } finally {
            if (conn != null) {
                try {
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                }
                restadapterdao.closeDataSourceConnection(conn);
            }
        }
        return "success";
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
