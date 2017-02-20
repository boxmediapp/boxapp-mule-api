package uk.co.boxnetwork.mule.components;


import javax.sql.DataSource;

import org.mule.api.MuleContext;
import org.mule.api.annotations.expressions.Lookup;
import org.mule.api.annotations.param.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class SQLExecutor {
	private static final Logger logger=LoggerFactory.getLogger(SQLExecutor.class);
	private String sql;
  public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
private JdbcTemplate jdbcTemplate;

  public void setDataSource(DataSource dataSource) {
      this.jdbcTemplate = new JdbcTemplate(dataSource);
  }
  public Object process(@Payload String payload, @Lookup MuleContext muleContext) throws Exception{
	  try{
	  this.jdbcTemplate.execute(" begin "+sql+" end;");
	  }
	  catch(Exception e){
		  logger.error(e+" while execiting:[["+sql+"]]");
	  }
	  return payload;
  }

}
