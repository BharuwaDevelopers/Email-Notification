package com.bspl.bean;
import java.io.IOException;
import java.sql.SQLException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import net.sf.jasperreports.engine.JRException;

@Path("GenJasper")
public class WS_GenratePdfFile {
    CallJasperReports calljasperreports=new CallJasperReports();
    @GET
    @Produces("application/json")
    public String callJasper(@QueryParam("UnitCode")String unitCode) throws JRException, IOException, SQLException {
      return calljasperreports.callJasperForPdf(unitCode) ;
    }
}
