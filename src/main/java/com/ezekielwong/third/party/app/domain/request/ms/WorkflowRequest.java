package com.ezekielwong.third.party.app.domain.request.ms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MS workflow API request
 */
@Data
@Builder
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
