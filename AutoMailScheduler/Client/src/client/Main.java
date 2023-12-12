package client;


import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;


import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Main extends TimerTask {
    /* date Time Set  */
    // perform the task once a day at 4 a.m., starting tomorrow morning
    private final static long fONCE_PER_DAY = 1000 * 60 * 60 * 24;
    //   private final static int fONE_DAY = 0;
    //  private final static int fFOUR_AM = 16;
    // private final static int fZERO_MINUTES = 34;
    int count = 0;
    private String name;


    public Main(String n) {
        this.name = n;
    }


    public static void main(String... arguments) {
        // Create and set up a frame window
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Auto Mail");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton watch = new JButton();
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println("Task performed on " + new java.util.Date());
                watch.setText(new java.util.Date().toString());
            }
        }, 0, 1000);
        watch.setBackground(new Color(59, 89, 182));
        watch.setBounds(100, 100, 100, 50);
        watch.setBorderPainted(true);
        watch.setForeground(Color.WHITE);
        watch.setFocusPainted(false);
        watch.setFont(new Font("Tahoma", Font.BOLD, 12));
        frame.add(watch);


        // Define new buttons with different width on help of the ---
        JButton sentEmail = new JButton();
        int delay = 0; // delay for x minutes
        int period = 30 * 60 * 1000; // repeat every x minutes
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                // put your task here
                System.out.println("Task performed on " + new java.util.Date());
                sentEmail.setText("Auto Email sent every 30 minutes "+new java.util.Date().toString());

            }
        }, delay, period);

        sentEmail.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    sentEmailCall();
                } catch (Exception f) {
                    System.out.println(f.toString());
                }
            }
        });

        JButton genPdf = new JButton();
        
        int delay1 = 0; // delay for x minutes
        int period1 = 10 * 60 * 1000; // repeat every x minutes
        Timer timer11 = new Timer();
        timer11.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                // put your task here
                System.out.println("Task performed on " + new java.util.Date());
                genPdf.setText("Auto Generate Pdf for Email every 10 minutes "+new java.util.Date().toString());

            }
        }, delay1, period1);
        
       // genPdf.setText("Auto Generate Pdf for Email");
        genPdf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    generatePdf();
                } catch (Exception f) {
                    System.out.println(f.toString());
                }
            }
        });


        // Define the panel to hold the buttons
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        // JPanel panel3 = new JPanel();
        // Set up the title for different panels
        panel1.setBorder(BorderFactory.createTitledBorder("Email Notification"));
        panel2.setBorder(BorderFactory.createTitledBorder("Gernate PDF"));
        //  panel3.setBorder(BorderFactory.createTitledBorder("Ecms Notification"));

        // Set up the BoxLayout
        BoxLayout layout1 = new BoxLayout(panel1, BoxLayout.Y_AXIS);
        BoxLayout layout2 = new BoxLayout(panel2, BoxLayout.Y_AXIS);
        //BoxLayout layout3 = new BoxLayout(panel3, BoxLayout.Y_AXIS);

        panel1.setLayout(layout1);
        panel2.setLayout(layout2);
        // panel3.setLayout(layout3);

        sentEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        // panel1.add(jb1);
        panel1.add(sentEmail);
        panel2.add(genPdf);

        // jb3.setAlignmentX(Component.CENTER_ALIGNMENT);

        //helpMe.setAlignmentX(Component.LEFT_ALIGNMENT);


        // Add the three panels into the frame
        frame.setLayout(new FlowLayout());
        frame.add(panel1);
        frame.add(panel2);
        //  frame.add(panel3);


        // Set the window to be visible as the default to be false
        frame.pack();
        frame.setSize(600, 400);
        frame.setVisible(true);

        // TimerTask fetchMail = new Main();
        // perform the task once a day at 4 a.m., starting tomorrow morning


        Main email = new Main("email");
        Main pdf = new Main("gernatePDF");
       

        Timer timerObj = new Timer();
        // t.scheduleAtFixedRate(te1, 0,5*1000);
        // t.scheduleAtFixedRate(te2, 0,1000);

        System.out.println("hi--");
        timerObj.scheduleAtFixedRate(email, 0, 30 * 60 * 1000);
        timerObj.scheduleAtFixedRate(pdf, 0, 10 * 60 * 1000);
        //timerObj.scheduleAtFixedRate(apmail, getTimeApprovalMail(), fONCE_PER_DAY);
        // timerObj.scheduleAtFixedRate(commail, getTime_Compliance(), fONCE_PER_DAY);
    }

    private static Date getTimeApprovalMail() {
        //  long fONCE_PER_DAY = 1000 * 60 * 60 * 24;
        int fONE_DAY = 1;
        int fFOUR_AM = 22;
        int fZERO_MINUTES = 00;
        Calendar tomorrow = new GregorianCalendar();
        tomorrow.add(Calendar.DATE, fONE_DAY);
        Calendar result =
            new GregorianCalendar(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH),
                                  tomorrow.get(Calendar.DATE), fFOUR_AM, fZERO_MINUTES);
        return result.getTime();
    }


    @Override
    public void run() {
        System.out.println("name--->" + name);
        try {
            if ("email".equalsIgnoreCase(name)) {
                sentEmailCall();
            }
            if ("gernatePDF".equalsIgnoreCase(name)) {
                generatePdf();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void hello() {
        System.out.println("welcome swing call button---");
        JOptionPane.showMessageDialog(null, "Hi Manuel ");
    }


    public void url() {
        try {
            URI uri = new URI("http://google.com/");
            Desktop dt = Desktop.getDesktop();
            dt.browse(uri);
        } catch (Exception ex) {
        }

    }

    public static void sentEmailCall() throws Exception {
        System.out.println("call purchaseOrderCall_me");
        //String url = "http://127.0.0.1:7101/WebServiceApp/resources/SendMail?UnitCode=13001&MailType=PO";
        String url = "http://10.0.6.171:9090/WebApi/resources/SendMail?UnitCode=13001&Tcode=MMPO&DocNo=API";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String status = response.toString();
        System.out.println("response----" + status);
    }

    public static void generatePdf() throws Exception {
      //  String url = " http://127.0.0.1:7101/WebServiceApp/resources/GenJasper?UnitCode=13001";
        String url = "http://10.0.6.171:9090/WebApi/resources/GenJasper?UnitCode=13001";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String status = response.toString();
        System.out.println("response----" + status);
        // JOptionPane.showMessageDialog(null,"programme run status--"+status);
    }


}

