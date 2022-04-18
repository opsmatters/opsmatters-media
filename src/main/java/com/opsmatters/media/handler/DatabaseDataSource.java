/*
 * Copyright 2020 Gerald Curley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opsmatters.media.handler;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneOffset;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import nl.crashdata.chartjs.data.simple.SimpleChartJsXYDataPoint;
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.model.platform.Site;
import com.opsmatters.media.model.chart.SourceType;
import com.opsmatters.media.model.chart.ChartSource;
import com.opsmatters.media.model.chart.ChartParameter;
import com.opsmatters.media.model.chart.ChartParameters;
import com.opsmatters.media.model.chart.ChartParameterType;
import com.opsmatters.media.util.AppSession;

import static com.opsmatters.media.model.chart.ChartParameterType.*;
import static com.opsmatters.media.model.chart.ChartParameterValue.*;

/**
 * Represents a chart data source from a database.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DatabaseDataSource<E extends Serializable> implements DataSource<E>
{
    private static final Logger logger = Logger.getLogger(DatabaseDataSource.class.getName());

    private static Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private static int QUERY_TIMEOUT = 60;

    private JDBCDatabaseConnection conn;
    private Site site;

    /**
     * Constructor that takes a connection and site.
     */
    public DatabaseDataSource(JDBCDatabaseConnection conn, Site site)
    {
        this.conn = conn;
        this.site = site;
    }

    /**
     * Returns the data source type.
     */
    @Override
    public SourceType getType()
    {
        return SourceType.DATABASE;
    }

    /**
     * Returns the data from the plot.
     */
    @Override
    public List<E> getDataPoints(ChartSource source, ChartParameters parameters) throws Exception
    {
        ResultSet rs = null;
        PreparedStatement statement = null;
        List<E> ret = null;
        List<ChartParameterType> types = source.getResultTypes();

        try
        {
            if(conn != null && conn.isConnected())
            {
                // Replace the configured parameters in the query
                int idx = 1;
                String sql = source.getQuery();
                if(source.getParameters() != null)
                {
                    for(ChartParameter parameter : source.getParameters())
                    {
                        Object obj = parameters.get(parameter);
                        if(obj != null)
                        {
                            if(obj instanceof String)
                            {
                                String str = (String)obj;

                                // Replace the site default with the current site id
                                if(str == CURRENT_SITE.name())
                                    str = site != null ? site.getId() : "";

                                // Replace the session default with the current session id
                                if(str == CURRENT_SESSION.name())
                                    str = Integer.toString(AppSession.id());

                                if(str == null || str.length() == 0)
                                    sql = sql.replaceAll(String.format("=[ ]?:%s", parameter.name()), "LIKE '%'");
                                else
                                    sql = sql.replaceAll(String.format(":%s", parameter.name()),
                                        String.format("'%s'", str));
                            }
                            else if(obj instanceof LocalDateTime)
                            {
                                LocalDateTime dt = (LocalDateTime)obj;
                                long millis = dt.toInstant(ZoneOffset.UTC).toEpochMilli();
                                sql = sql.replaceAll(String.format(":%s", parameter.name()),
                                    String.format("'%s'", new Timestamp(millis)));
                            }
                            else if(obj instanceof List<?>)
                            {
                                List<?> list = (List<?>)obj;

                                // If the list is empty replace with "LIKE '%'" to select all values
                                if(list.size() == 0)
                                {
                                    sql = sql.replaceAll(String.format("IN \\(:%s\\)", parameter.name()), "LIKE '%'");
                                }
                                else // otherwise replace with comma-separated list of values
                                {
                                    StringBuilder str = new StringBuilder();
                                    for(Object item : list)
                                    {
                                        if(str.length() > 0)
                                            str.append(",");
                                        if(item.toString().length() > 0)
                                            str.append("'").append(item.toString()).append("'");
                                    }

                                    sql = sql.replaceAll(String.format(":%s", parameter.name()), str.toString());
                                }
                            }
                        }
                        else
                        {
                            logger.warning("parameter not found: "+parameter);
                        }

                        ++idx;
                    }
                }

                statement = conn.getConnection().prepareStatement(sql);
                statement.setQueryTimeout(QUERY_TIMEOUT);
                rs = statement.executeQuery();
                ret = new ArrayList<E>();
                while(rs.next())
                {
                    if(types.size() == 2) // X,Y co-ordinates
                    {
                        Serializable x = getResult(1, types.get(0), rs);
                        Serializable y = getResult(2, types.get(1), rs);
                        ret.add((E)new SimpleChartJsXYDataPoint(x, y));
                    }
                    else // Single number value
                    {
                        ret.add((E)getResult(1, types.get(0), rs));
                    }
                }
            }
        }
        finally
        {
            try
            {
                if(rs != null)
                    rs.close();
                if(statement != null)
                    statement.clearParameters();
            }
            catch (SQLException ex) 
            {
            } 
        }

        return ret;
    }

    /**
     * Returns the result from the query for the given index.
     */
    private Serializable getResult(int idx, ChartParameterType type, ResultSet rs) throws SQLException
    {
        switch(type)
        {
            case LOCAL_DATE:
            {
                long millis = rs.getTimestamp(idx, UTC).getTime();
                return LocalDate.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
            }
            case LOCAL_DATE_TIME:
            {
                long millis = rs.getTimestamp(idx, UTC).getTime();
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
            }
            case STRING:
            {
                return rs.getString(idx);
            }
            case INTEGER:
            {
                return Integer.valueOf(rs.getInt(idx));
            }
        }

        return null;
    }
}