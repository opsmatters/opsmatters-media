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

package com.opsmatters.media.model.chart;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.opsmatters.media.model.ConfigElement;
import com.opsmatters.media.model.ConfigParser;

/**
 * Represents a data source for a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartSource implements ConfigElement
{
    private SourceType type;
    private String query;
    private List<ChartParameter> parameters;
    private List<ChartParameterType> resultTypes;

    /**
     * Default constructor.
     */
    public ChartSource()
    {
    }

    /**
     * Copy constructor.
     */
    public ChartSource(ChartSource obj)
    {
        copyAttributes(obj);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(ChartSource obj)
    {
        if(obj != null)
        {
            setType(obj.getType());
            setQuery(obj.getQuery());
            setParameters(new ArrayList<ChartParameter>(obj.getParameters()));
            setResultTypes(new ArrayList<ChartParameterType>(obj.getResultTypes()));
        }
    }

    /**
     * Returns the type for the source.
     */
    public SourceType getType()
    {
        return type;
    }

    /**
     * Sets the type for the source.
     */
    public void setType(SourceType type)
    {
        this.type = type;
    }

    /**
     * Sets the type for the source.
     */
    public void setType(String type)
    {
        setType(SourceType.valueOf(type));
    }

    /**
     * Returns the query for the source.
     */
    public String getQuery()
    {
        return query;
    }

    /**
     * Sets the query for the source.
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /**
     * Returns the parameters for the source.
     */
    public List<ChartParameter> getParameters()
    {
        return parameters;
    }

    /**
     * Sets the parameters for the source.
     */
    public void setParameters(List<ChartParameter> parameters)
    {
        this.parameters = parameters;
    }

    /**
     * Sets the parameters for the source.
     */
    public void setStringParameters(List<String> parameters)
    {
        if(this.parameters == null)
            this.parameters = new ArrayList<ChartParameter>();
        this.parameters.clear();
        for(String parameter : parameters)
            this.parameters.add(ChartParameter.valueOf(parameter));
    }

    /**
     * Returns the result types for the source.
     */
    public List<ChartParameterType> getResultTypes()
    {
        return resultTypes;
    }

    /**
     * Sets the result types for the source.
     */
    public void setResultTypes(List<ChartParameterType> resultTypes)
    {
        this.resultTypes = resultTypes;
    }

    /**
     * Sets the result types for the source.
     */
    public void setStringResultTypes(List<String> resultTypes)
    {
        if(this.resultTypes == null)
            this.resultTypes = new ArrayList<ChartParameterType>();
        this.resultTypes.clear();
        for(String resultType : resultTypes)
            this.resultTypes.add(ChartParameterType.valueOf(resultType));
    }

    /**
     * Returns a builder for the configuration.
     * @return The builder instance.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder to make configuration construction easier.
     */
    public static class Builder implements ConfigParser<ChartSource>
    {
        // The config attribute names
        private static final String TYPE = "type";
        private static final String QUERY = "query";
        private static final String PARAMETERS = "parameters";
        private static final String RESULT_TYPES = "result-types";

        private ChartSource ret = new ChartSource();

        /**
         * Parse the configuration using the given attribute map.
         * @param map The map of attributes
         * @return This object
         */
        @Override
        public Builder parse(Map<String, Object> map)
        {
            if(map.containsKey(TYPE))
                ret.setType((String)map.get(TYPE));
            if(map.containsKey(QUERY))
                ret.setQuery((String)map.get(QUERY));
            if(map.containsKey(PARAMETERS))
                ret.setStringParameters((List<String>)map.get(PARAMETERS));
            if(map.containsKey(RESULT_TYPES))
                ret.setStringResultTypes((List<String>)map.get(RESULT_TYPES));

            return this;
        }

        /**
         * Returns the configured configuration instance
         * @return The configuration instance
         */
        public ChartSource build()
        {
            return ret;
        }
    }
}