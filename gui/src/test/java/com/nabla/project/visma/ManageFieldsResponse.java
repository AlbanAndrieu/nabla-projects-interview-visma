package com.nabla.project.visma;

import static com.google.common.collect.Lists.newLinkedList;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
@JsonAutoDetect
public class ManageFieldsResponse
{

    @JsonProperty
    private final List<String> errors = newLinkedList();

    @JsonProperty
    private String             fields = "";

    public void setErrors(List<String> errors)
    {
        this.errors.addAll(errors);
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public void setFields(String fields)
    {
        this.fields = fields;
    }
}
