/*
 * Copyright 2025 Gerald Curley
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
package com.opsmatters.media.model.admin;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.opsmatters.media.model.MessageTemplate;

/**
 * Class representing an email template.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class EmailTemplate extends MessageTemplate
{
    private EmailTemplateId templateId;
    private EmailTemplateType type = EmailTemplateType.NONE;
    private Template mustacheTemplate;

    /**
     * Default constructor.
     */
    public EmailTemplate()
    {
    }

    /**
     * Constructor that takes a name.
     */
    public EmailTemplate(String name)
    {
        super(name);
    }

    /**
     * Copies the attributes of the given object.
     */
    public void copyAttributes(EmailTemplate obj)
    {
        if(obj != null)
        {
            super.copyAttributes(obj);
            setType(obj.getType());
            setCode(obj.getCode());
            setMessage(obj.getMessage());
        }
    }

    /**
     * Sets the code for the template.
     */
    @Override
    public void setCode(String code)
    {
        super.setCode(code);
        setTemplateId(code);
    }

    /**
     * Returns the template id.
     */
    public EmailTemplateId getTemplateId()
    {
        return templateId;
    }

    /**
     * Sets the template id.
     */
    private void setTemplateId(EmailTemplateId templateId)
    {
        this.templateId = templateId;
    }

    /**
     * Sets the template id.
     */
    private void setTemplateId(String code)
    {
        setTemplateId(EmailTemplateId.fromCode(code));
    }

    /**
     * Returns the template type.
     */
    public EmailTemplateType getType()
    {
        return type;
    }

    /**
     * Sets the template type.
     */
    public void setType(String type)
    {
        setType(EmailTemplateType.valueOf(type));
    }

    /**
     * Sets the template type.
     */
    public void setType(EmailTemplateType type)
    {
        this.type = type;
    }

    /**
     * Sets the template message.
     */
    @Override
    public void setMessage(String message)
    {
        super.setMessage(message);

        mustacheTemplate = Mustache.compiler().compile(message);
    }

    /**
     * Returns the mustache template.
     */
    public Template getMustacheTemplate()
    {
        return mustacheTemplate;
    }
}