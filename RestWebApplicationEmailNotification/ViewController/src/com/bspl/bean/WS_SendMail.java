package com.bspl.bean;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
//@Path("resources")
//@ApplicationPath("resources") 
@Path("SendMail")
public class WS_SendMail {
    MailBean mailbean=new MailBean();
    @GET
    @Produces("application/json")
    public String getSendMail(@QueryParam("UnitCode") String unitCode,@QueryParam("Tcode") String tcode,@QueryParam("DocNo") String docNo){
      return mailbean.sendMailMethod(unitCode,tcode,docNo) ;
       // return mailbean.emailSendTesting() ;
    }
    
}
