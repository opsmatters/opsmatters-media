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

import java.util.Map;
import java.util.TreeMap;
import java.util.List;
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
import com.opsmatters.media.db.JDBCDatabaseConnection;
import com.opsmatters.media.model.chart.SourceType;
import com.opsmatters.media.model.chart.ChartSource;
import com.opsmatters.media.model.chart.Parameter;
import com.opsmatters.media.model.chart.Parameters;
import com.opsmatters.media.model.chart.ParameterType;

/**
 * Represents a chart data source.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DatabaseDataSource<X extends Serializable,Y extends Serializable> implements DataSource<X,Y>
{
    private static final Logger logger = Logger.getLogger(DatabaseDataSource.class.getName());

    private static Calendar UTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private static int QUERY_TIMEOUT = 60;

    private JDBCDatabaseConnection conn;

    /**
     * Default constructor.
     */
    public DatabaseDataSource(JDBCDatabaseConnection conn)
    {
        this.conn = conn;
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
    public Map<X,Y> getDataPoints(ChartSource source, Parameters parameters) throws Exception
    {
        ResultSet rs = null;
        PreparedStatement statement = null;
        Map<X,Y> ret = null;
        List<ParameterType> types = source.getResultTypes();

        try
        {
            if(conn != null && conn.isConnected())
            {
                // Replace the configured parameters in the query
                int idx = 1;
                String sql = source.getQuery();
                for(Parameter parameter : source.getParameters())
                {
                    Object obj = parameters.get(parameter);
                    if(obj != null)
                    {
                        if(obj instanceof LocalDateTime)
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

                statement = conn.getConnection().prepareStatement(sql);
                statement.setQueryTimeout(QUERY_TIMEOUT);
                rs = statement.executeQuery();
                ret = new TreeMap<X,Y>();
                while(rs.next())
                    ret.put((X)getResult(1, types.get(0), rs), (Y)getResult(2, types.get(1), rs));
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
    private Object getResult(int idx, ParameterType type, ResultSet rs) throws SQLException
    {
        Object ret = null;

        if(type == ParameterType.LOCAL_DATE)
        {
            long millis = rs.getTimestamp(idx, UTC).getTime();
            ret = LocalDate.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
        }
        else if(type == ParameterType.LOCAL_DATE_TIME)
        {
            long millis = rs.getTimestamp(idx, UTC).getTime();
            ret = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
        }
        else if(type == ParameterType.INTEGER)
        {
            ret = Integer.valueOf(rs.getInt(idx));
        }

        return ret;
    }
}