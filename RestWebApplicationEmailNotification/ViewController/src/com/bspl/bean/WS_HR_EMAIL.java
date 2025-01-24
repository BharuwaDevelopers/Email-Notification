package com.bspl.bean;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("SendMailHr")
public class WS_HR_EMAIL {
    MailBean mailbean=new MailBean();
    @GET
    public String getSendHrMail(@QueryParam("UnitCode") String unitCode,@QueryParam("Tcode") String tcode,@QueryParam("DocNo") String docNo){
    return mailbean.sendMailHrMethod(unitCode, tcode, docNo) ;
    }
}
