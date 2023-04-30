package com.ezekielwong.third.party.app.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MS workflow API request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRequest implements Serializable {

    /**
     * Name of the workflow
     */
    private String name;

    /**
     * XML data used when starting the workflow
     */
    private String params;
}
