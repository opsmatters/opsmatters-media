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

/**
 * Represents a data source for a chart.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ChartSource
{
    public static final String TYPE = "type";
    public static final String QUERY = "query";
    public static final String PARAMETERS = "parameters";
    public static final String RESULT_TYPES = "result-types";

    private SourceType type;
    private String query;
    private List<ParameterName> parameters;
    private List<ParameterType> resultTypes;

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
            setParameters(new ArrayList<ParameterName>(obj.getParameters()));
            setResultTypes(new ArrayList<ParameterType>(obj.getResultTypes()));
        }
    }

    /**
     * Reads the object from the given YAML Document.
     */
    public ChartSource(Map<String, Object> map)
    {
        if(map.containsKey(TYPE))
            setType((String)map.get(TYPE));
        if(map.containsKey(QUERY))
            setQuery((String)map.get(QUERY));
        if(map.containsKey(PARAMETERS))
            setStringParameters((List<String>)map.get(PARAMETERS));
        if(map.containsKey(RESULT_TYPES))
            setStringResultTypes((List<String>)map.get(RESULT_TYPES));
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
    public List<ParameterName> getParameters()
    {
        return parameters;
    }

    /**
     * Sets the parameters for the source.
     */
    public void setParameters(List<ParameterName> parameters)
    {
        this.parameters = parameters;
    }

    /**
     * Sets the parameters for the source.
     */
    public void setStringParameters(List<String> parameters)
    {
        if(this.parameters == null)
            this.parameters = new ArrayList<ParameterName>();
        this.parameters.clear();
        for(String parameter : parameters)
            this.parameters.add(ParameterName.valueOf(parameter));
    }

    /**
     * Returns the result types for the source.
     */
    public List<ParameterType> getResultTypes()
    {
        return resultTypes;
    }

    /**
     * Sets the result types for the source.
     */
    public void setResultTypes(List<ParameterType> resultTypes)
    {
        this.resultTypes = resultTypes;
    }

    /**
     * Sets the result types for the source.
     */
    public void setStringResultTypes(List<String> resultTypes)
    {
        if(this.resultTypes == null)
            this.resultTypes = new ArrayList<ParameterType>();
        this.resultTypes.clear();
        for(String resultType : resultTypes)
            this.resultTypes.add(ParameterType.valueOf(resultType));
    }
}